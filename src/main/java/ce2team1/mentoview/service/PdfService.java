package ce2team1.mentoview.service;

import ce2team1.mentoview.exception.InterviewException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class PdfService {

    public String extractTextFromPDF(InputStream inputStream) throws IOException {
        // PDF 파일에 대해 텍스트를 추출하는 로직을 구현
        PDFParser parser = new PDFParser(new RandomAccessReadBuffer(inputStream));

        try (PDDocument document = parser.parse()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String extractText = stripper.getText(document);

            if (extractText.trim().isEmpty() || extractText.length() < 10) {
                extractText = extractTextFromImage(document);
            }

            return extractText;
        } catch (IOException e) {
            e.printStackTrace();
            return "텍스트 추출 실패: " + e.getMessage();
        } catch (TesseractException e) {
            e.printStackTrace();
            throw new InterviewException("Tesseract가 연결되어 있지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public String extractTextFromImage(PDDocument document) throws IOException, TesseractException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder extractedText = new StringBuilder();
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/opt/homebrew/share/tessdata");
        tesseract.setLanguage("kor+eng"); // 한글 & 영어 인식
        tesseract.setPageSegMode(4);

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, 100); // 100 DPI로 이미지 변환
            File tempImage = new File("temp.png");
            ImageIO.write(image, "png", tempImage);

            try {
                // OCR 수행
                String ocrText = tesseract.doOCR(tempImage);
                extractedText.append(ocrText).append("\n");
            } catch (UnsatisfiedLinkError e) {
                throw new InterviewException("Tesseract 라이브러리를 로드할 수 없습니다. 설치 경로를 확인하세요.",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                Files.delete(tempImage.toPath()); // 임시 이미지 삭제
            }
        }

        return extractedText.toString();
    }
}
