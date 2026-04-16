package at.oeh.uni.innsbruck.stadtrad.examValidation.validation;

import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.PdfSignatureVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
public class PdfSigantureVerifierTest {

    @Test
    public void testHavingSignatureDE() throws IOException {
        PdfSignatureVerifier pdfSignatureVerifier = new PdfSignatureVerifier();
        boolean result = pdfSignatureVerifier.verifySignature(new FileInputStream("testdata/record-de.pdf"));
        Assertions.assertTrue(result);
    }

    @Test
    public void testHavingSignatureEN() throws IOException {
        PdfSignatureVerifier pdfSignatureVerifier = new PdfSignatureVerifier();
        boolean result = pdfSignatureVerifier.verifySignature(new FileInputStream("testdata/record-en.pdf"));
        Assertions.assertTrue(result);
    }

    @Test
    public void testNotHavingSignature() throws IOException {
        PdfSignatureVerifier pdfSignatureVerifier = new PdfSignatureVerifier();
        boolean result = pdfSignatureVerifier.verifySignature(new FileInputStream("testdata/record-de_not-sign.pdf"));
        Assertions.assertFalse(result);
    }

    @Test
    public void testWrongSignature() throws IOException {
        PdfSignatureVerifier pdfSignatureVerifier = new PdfSignatureVerifier();
        boolean result = pdfSignatureVerifier.verifySignature(new FileInputStream("testdata/record-de_not-wrong_sign.pdf"));
        Assertions.assertFalse(result);
    }
}
