package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;


import java.io.IOException;
import java.io.InputStream;

public class PdfSignatureVerifier {
    /**
     * Verifies if the PDF document has a valid official signature (amtssignatur)
     *
     * @param inputStream The PDF file input stream
     * @return true if the document has a valid signature, false otherwise
     */
    public boolean verifySignature(InputStream inputStream) throws IOException {

        CommonTrustedCertificateSource trustedSource = new CommonTrustedCertificateSource();

        // PDF laden
        DSSDocument document = new InMemoryDocument(inputStream);

        // Zertifikatsverifier konfigurieren
        CommonCertificateVerifier verifier = new CommonCertificateVerifier();
        verifier.setTrustedCertSources(trustedSource);
        verifier.setCheckRevocationForUntrustedChains(false);
        verifier.setRevocationFallback(false);

        // Automatisch passenden Validator erzeugen
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(document);

        // Setze den CertificateVerifier
        validator.setCertificateVerifier(verifier);

        // Validieren!
        Reports reports = validator.validateDocument();

        for (String id : reports.getSimpleReport().getSignatureIdList()) {
            SignatureWrapper signatureWrapper = reports.getDiagnosticData().getSignatureById(id);
            String certificateDN = signatureWrapper.getSigningCertificate().getCertificateDN();

            return certificateDN.toLowerCase().contains("universität innsbruck") && signatureWrapper.isSignatureIntact();
        }

        return false;
    }
}


