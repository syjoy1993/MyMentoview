package ce2team1.mentoview.interview.infra;

import ce2team1.mentoview.exception.InterviewException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {

    private final WebClient webClient;

    // OCR 전용 스레드풀
    private static final ExecutorService ocrExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    @Value("${tesseract.tessdata.path}")
    private String tessDataPath;

    @PreDestroy
    public void executorShutdown() {
        ocrExecutor.shutdown();
    }

    public Mono<String> extractTextFromS3(String s3Key) {
        return webClient.get()
                .uri(s3Key)
                .retrieve()
                .bodyToMono(byte[].class)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(bytes ->
                        tryExtractWithPdfBox(bytes)
                                .filter(text -> text.length() >= 10)
                                .switchIfEmpty(Mono.defer(() -> tryExtractWithOcr(bytes)))
                );
    }
    // PDFBox
    private Mono<String> tryExtractWithPdfBox(byte[] bytes) {
        return Mono.fromCallable(() -> {
            try (PDDocument pdDocument = new PDFParser(new RandomAccessReadBuffer(bytes)).parse()) {
                PDFTextStripper textStripper = new PDFTextStripper();
                String text = textStripper.getText(pdDocument);
                return text != null ? text.trim() : "";
            } catch (IOException e) {
                log.warn("PDFBox extraction failed (Start OCR): {}", e.getMessage());
                return "";
            }
        });
    }

    // Tesseract OCR (Heavyweight)
    private Mono<String> tryExtractWithOcr(byte[] bytes) {
        return Mono.fromCallable(()->{
            log.info("PDFBox extraction failed -> Start OCR");
            return performOcrExecution(bytes);
        });
    }
    // Internal Helper Methods (실제 CPU 작업)
    // ocr 실제 실행(기존 performOcrOnPdf 메서드 이름 변경)
    private String performOcrExecution(byte[] pdfBytes) {
        List<Callable<String>> tasks = new ArrayList<>();
        try (PDDocument pdDocument = new PDFParser(new RandomAccessReadBuffer(pdfBytes)).parse()) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
            // 페이지 별 -> 이미지로
            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);
                tasks.add(() -> processImageWithTesseract(bufferedImage));
            }
            //ocrExecutor 애서 병렬로 OCR
            List<Future<String>> futures = ocrExecutor.invokeAll(tasks);
            StringBuilder builder = new StringBuilder();
            for (Future<String> future : futures) {
                builder.append(future.get()).append("\n");
            }
            return builder.toString();

        } catch (Exception e) {
            log.error("OCR 오류 발생 : ", e);
            throw new InterviewException("OCR 처리중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Tesseract사용 :
    private String processImageWithTesseract(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath); //언어 셋트 주기
        tesseract.setLanguage("kor+eng");
        tesseract.setPageSegMode(4);// 텍스트 베치 힌트 옵션
        tesseract.setOcrEngineMode(1); // 어떤 엔진? LSTM 기반 신형 엔진(Neural nets only)
        tesseract.setVariable("user_defined_dpi", "300");//DPI 정보가 없으면 ->이미지를 300 DPI로간주
        //  300 DPI 이상 부터 인식률이 좋음

        // 전처리 수행
        BufferedImage preprocessImage = preprocessImage(image);

        return tesseract.doOCR(preprocessImage);
    }

    // 전처리 수행 (재활용) : 같은 크기의 새이미지 (흑백) 생성 -> 흑백일때 인식률이 상승
    private BufferedImage preprocessImage(BufferedImage image) {
        BufferedImage processedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY); // TYPE_BYTE_GRAY : 흑백으로 그려라
        Graphics2D g2d = processedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null); // (0,0)에 이미지 전체 새로그림
        g2d.dispose();// 자원정리
        return processedImage;
    }
}
