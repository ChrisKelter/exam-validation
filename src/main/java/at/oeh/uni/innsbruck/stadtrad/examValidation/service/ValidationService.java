package at.oeh.uni.innsbruck.stadtrad.examValidation.service;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.ValidationDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.ValidationType;
import at.oeh.uni.innsbruck.stadtrad.examValidation.repository.ValidationRepository;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.*;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Record;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {
    private final ValidationRepository validationRepository;

    public ValidationService(ValidationRepository validationRepository) {
        this.validationRepository = validationRepository;
    }

    public Validation save(Validation validation) {
        validation.setLastUpdate(new Date());
        return validationRepository.save(validation);
    }

    public Page<Validation> getValidationPaged(Pageable page) {
        return this.validationRepository.findAll(page);
    }

    public Page<Validation> getValidationPaged(Pageable page, String search) {
        return this.validationRepository.findAllByFilters(search, page);
    }

    public Validation createManualValidation(ValidationDto dto) {
        Validation validation = new Validation();
        validation.setStudentId(dto.getStudentId());
        validation.setValidUntil(dto.getValidUntil());
        validation.setEmail(dto.getEmail());
        validation.setType(ValidationType.MANUAL);
        return save(validation);
    }

    public Validation createAutomaticValidation(Student student, String email, Date validUntil) {
        Validation validation = new Validation();
        validation.setValidUntil(validUntil);
        validation.setEmail(email);
        validation.setType(ValidationType.AUTOMATIC);
        return save(validation);
    }

    public void updateAutomaticValidation(Student student, String email, Date validUntil) {
        Optional<Validation> oV = this.loadValidation(student.getMatrikelNr());
        if (oV.isEmpty()) {
            return;
        }
        Validation validation = oV.get();
        validation.setEmail(email);
        validation.setType(ValidationType.AUTOMATIC);
        validation.setValidUntil(validUntil);
        save(validation);
    }

    public Optional<Validation> loadValidation(String studentId) {
        return this.validationRepository.findById(studentId);
    }

    public Validation updateManualValidation(ValidationDto dto) {
        Validation validation = validationRepository.findByStudentId(dto.getStudentId());
        validation.setValidUntil(dto.getValidUntil());
        validation.setEmail(dto.getEmail());
        validation.setType(ValidationType.MANUAL);
        validation.setLastUpdate(new Date());
        return save(validation);
    }

    public void remove(String studentId) {
        Validation validation = validationRepository.findByStudentId(studentId);
        validationRepository.delete(validation);
    }

    public boolean verifyPdfSignature(MultipartFile file) {
        PdfSignatureVerifier pdfSignatureVerifier = new PdfSignatureVerifier();
        try {
            return pdfSignatureVerifier.verifySignature(file.getInputStream());
        } catch (IOException e) {
            return false;
        }
    }

    public Student parsePdf(MultipartFile file) throws IOException {
        PdfProcessor pdfProcessor = new PdfProcessor();
        return pdfProcessor.extractStudent(file.getInputStream());
    }

    public void validateStudent(Student student) throws ValidationException {
        if (!student.isFirstYear()) {
            List<Record> records = student.getRecordsInRange(Util.getPreviousAcademicYear());
            double sumCredits = records.stream().mapToDouble(Record::getCredits).sum();
            if (sumCredits <= 15.99) {
                throw new ValidationException("Student does not have enough credits");
            }
        }
    }

    public void validateAndSaveRequest(MultipartFile file, String email) throws ValidationException {
        // check signature of file
        if (!verifyPdfSignature(file)) {
            throw new ValidationException("Signature verification failed");
        }

        try {
            Student student = parsePdf(file);
            validateStudent(student);
            createOrUpdateValidation(student, email);
        } catch (IOException e) {
            throw new ValidationException("Failed to parse pdf");
        }
    }

    public void createOrUpdateValidation(Student student, String email) {
        String studentId = student.getMatrikelNr();
        LocalDate validUntil = Util.getNextAcademicYear().getSecond().plusMonths(2); // until end of november
        Date validUntilDate = Date.from(validUntil.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (this.validationRepository.existsByStudentId(studentId)) {
            updateAutomaticValidation(student, email, validUntilDate);
        } else {
            createAutomaticValidation(student, email, validUntilDate);
        }
    }

    public boolean isEligible(String studentId, String email) {
        return this.validationRepository.existsByStudentIdAndEmailAndValidUntilAfter(studentId, email, new Date());
    }
}
