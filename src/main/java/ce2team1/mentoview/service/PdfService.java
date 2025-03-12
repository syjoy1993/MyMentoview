package ce2team1.mentoview.service;

import ce2team1.mentoview.exception.InterviewException;
import jakarta.annotation.PreDestroy;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class PdfService {

    // 클래스 레벨에서 전역 ExecutorService 생성 (Lazy Initialization)
    private static final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Value("${tesseract.tessdata.path}")
    private String tessDataPath;

    @PreDestroy
    public void executorShutdown() {
        executor.shutdown();
    }

    public String extractTextFromPDF(InputStream inputStream) throws IOException {
        // PDF 파일 메모리로 로딩
        byte[] pdfBytes = inputStream.readAllBytes();

        // PDFParser를 사용하여 PDF 문서 읽기
        PDFParser parser = new PDFParser(new RandomAccessReadBuffer(pdfBytes));

        try (PDDocument document = parser.parse()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String extractText = stripper.getText(document);

            if (extractText.trim().isEmpty() || extractText.length() < 10) {
                extractText = extractTextFromImage(pdfBytes); // PDF를 바이트 배열로 전달
            }

            return extractText;
        } catch (TesseractException e) {
            e.printStackTrace();
            throw new InterviewException("Tesseract가 연결되어 있지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public String extractTextFromImage(byte[] pdfBytes) throws IOException, TesseractException {

        List<Callable<String>> tasks = new ArrayList<>();

        List<BufferedImage> images = new ArrayList<>();

        // 1. PDDocument를 한 번만 로드하고, 모든 페이지를 이미지로 변환
        try (PDDocument document = new PDFParser(new RandomAccessReadBuffer(pdfBytes)).parse()) {
            PDFRenderer renderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300); // DPI 300으로 변환
                images.add(image);
            }
        }

        // 2. 변환된 이미지 리스트를 병렬로 OCR 수행
        for (BufferedImage image : images) {
            tasks.add(() -> processImage(image)); // PDDocument 공유 없이 OCR 수행
        }

        try {
            List<Future<String>> futures = executor.invokeAll(tasks);
            StringBuilder extractedText = new StringBuilder();
            for (Future<String> future : futures) {
                extractedText.append(future.get()).append("\n");
            }
            return extractedText.toString();
        } catch (InterruptedException | ExecutionException e) {
            throw new InterviewException("OCR 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String processImage(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("kor+eng");
        tesseract.setPageSegMode(4);
        tesseract.setOcrEngineMode(1);
        tesseract.setVariable("user_defined_dpi", "300");

        BufferedImage preprocessImage = preprocessImage(image);

        return tesseract.doOCR(preprocessImage);
    }


    // PDF에서 추출한 이미지에 대해 전처리 수행
    private BufferedImage preprocessImage(BufferedImage image) {
        // Grayscale 변환
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayscaleImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return grayscaleImage;
    }
}
