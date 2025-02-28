package ce2team1.mentoview.service;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfService {

    public String extractTextFromPDF(InputStream inputStream) throws IOException {
        // PDF 파일에 대해 텍스트를 추출하는 로직을 구현
        PDFParser parser = new PDFParser(new RandomAccessReadBuffer(inputStream));

        try (PDDocument document = parser.parse()) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return "텍스트 추출 실패: " + e.getMessage();
        }

    }
}
