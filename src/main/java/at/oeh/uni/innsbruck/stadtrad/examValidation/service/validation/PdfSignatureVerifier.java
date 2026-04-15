package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import eu.europa.esig.dss.alert.StatusAlert;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.pades.validation.PAdESSignature;
import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.xml.common.ValidatorConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;


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


