package at.oeh.uni.innsbruck.stadtrad.examValidation.service;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.ValidationDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.ValidationType;
import at.oeh.uni.innsbruck.stadtrad.examValidation.repository.ValidationRepository;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.ExamRecord;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Student;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Util;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.ECTSLimitException;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.PdfParseException;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.SignaturInvalidException;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ValidationServiceTest {

    private static final Path SIGNED_PDF = Path.of("testdata/record-de.pdf");
    private static final Path UNSIGNED_PDF = Path.of("testdata/record-de_not-sign.pdf");

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ValidationRepository validationRepository;

    @Test
    @WithMockUser(authorities = {"admin"})
    void getValidationPagedWithoutSearchReturnsPagedData() {
        Page<Validation> page = validationService.getValidationPaged(PageRequest.of(0, 5));

        assertEquals(5, page.getContent().size());
        assertEquals(20, page.getTotalElements());
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void getValidationPagedWithSearchFiltersByEmailOrStudentId() {
        Page<Validation> emailPage = validationService.getValidationPaged(PageRequest.of(0, 10), "clara.weber");
        Page<Validation> idPage = validationService.getValidationPaged(PageRequest.of(0, 10), "1000010");

        assertEquals(1, emailPage.getTotalElements());
        assertEquals("1000003", emailPage.getContent().getFirst().getStudentId());
        assertEquals(1, idPage.getTotalElements());
        assertEquals("jonas.braun@example.com", idPage.getContent().getFirst().getEmail());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void createManualValidationPersistsManualValidation() {
        ValidationDto dto = validationDto("2000001", "manual@example.com", dateAtStartOfDay(LocalDate.of(2027, 1, 15)));

        Validation saved = validationService.createManualValidation(dto);

        assertEquals(ValidationType.MANUAL, saved.getType());
        assertEquals("manual@example.com", saved.getEmail());
        assertNotNull(saved.getLastUpdate());
        assertEquals(saved, validationRepository.findByStudentId("2000001"));
    }

    @Test
    void createAutomaticValidationPersistsAutomaticValidation() {
        Date validUntil = dateAtStartOfDay(LocalDate.of(2027, 11, 30));

        Validation saved = validationService.createAutomaticValidation(student("3000001", firstYearMatriculationDate(), List.of()), "auto@example.com", validUntil);

        assertEquals(ValidationType.AUTOMATIC, saved.getType());
        assertEquals(validUntil, saved.getValidUntil());
        assertNotNull(saved.getLastUpdate());
        assertEquals(saved, validationRepository.findByStudentId("3000001"));
    }

    @Test
    void updateAutomaticValidationReturnsWhenValidationDoesNotExist() {
        validationService.updateAutomaticValidation(student("3999999", firstYearMatriculationDate(), List.of()), "missing@example.com", new Date());

        assertFalse(validationRepository.existsByStudentId("3999999"));
    }

    @Test
    void updateAutomaticValidationOverwritesExistingValidation() {
        Date newValidUntil = dateAtStartOfDay(LocalDate.of(2027, 12, 1));

        validationService.updateAutomaticValidation(student("1000001", firstYearMatriculationDate(), List.of()), "updated-auto@example.com", newValidUntil);

        Validation updated = validationRepository.findByStudentId("1000001");
        assertEquals("updated-auto@example.com", updated.getEmail());
        assertEquals(ValidationType.AUTOMATIC, updated.getType());
        assertEquals(newValidUntil, updated.getValidUntil());
        assertNotNull(updated.getLastUpdate());
    }

    @Test
    void loadValidationReturnsExistingAndMissingEntries() {
        Optional<Validation> existing = validationService.loadValidation("1000002");
        Optional<Validation> missing = validationService.loadValidation("9999999");

        assertTrue(existing.isPresent());
        assertEquals("ben.schmidt@example.com", existing.get().getEmail());
        assertTrue(missing.isEmpty());
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void updateManualValidationUpdatesExistingValidation() {
        Date newValidUntil = dateAtStartOfDay(LocalDate.of(2028, 2, 20));
        ValidationDto dto = validationDto("1000002", "manual-update@example.com", newValidUntil);

        Validation updated = validationService.updateManualValidation(dto);

        assertEquals(ValidationType.MANUAL, updated.getType());
        assertEquals("manual-update@example.com", updated.getEmail());
        assertEquals(newValidUntil, updated.getValidUntil());
        assertNotNull(updated.getLastUpdate());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void removeDeletesExistingValidation() {
        assertTrue(validationRepository.existsByStudentId("1000003"));

        validationService.remove("1000003");

        assertFalse(validationRepository.existsByStudentId("1000003"));
    }

    @Test
    void verifyPdfSignatureReturnsTrueForSignedPdf() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "record-de.pdf",
                "application/pdf",
                Files.readAllBytes(SIGNED_PDF)
        );

        assertTrue(validationService.verifyPdfSignature(file));
    }

    @Test
    void verifyPdfSignatureReturnsFalseForUnsignedPdf() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "record-de_not-sign.pdf",
                "application/pdf",
                Files.readAllBytes(UNSIGNED_PDF)
        );

        assertFalse(validationService.verifyPdfSignature(file));
    }

    @Test
    void verifyPdfSignatureReturnsFalseWhenInputStreamFails() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("boom"));

        assertFalse(validationService.verifyPdfSignature(file));
    }

    @Test
    void parsePdfExtractsStudentFromPdf() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "record-en.pdf",
                "application/pdf",
                Files.readAllBytes(Path.of("testdata/record-en.pdf"))
        );

        Student student = validationService.parsePdf(file);

        assertEquals("11722390", student.getMatrikelNr());
        assertEquals("Christopher", student.getFirstName());
        assertEquals("Kelter", student.getLastName());
    }

    @Test
    void validateStudentAcceptsFirstYearStudent() {
        Student student = student("4000001", firstYearMatriculationDate(), List.of());

        assertDoesNotThrow(() -> validationService.validateStudent(student));
    }

    @Test
    void validateStudentAcceptsOlderStudentWithEnoughCredits() {
        Student student = student(
                "4000002",
                olderMatriculationDate(),
                List.of(
                        new ExamRecord("Algorithms", previousAcademicYearDate(2), 1, 2, 8.0),
                        new ExamRecord("Databases", previousAcademicYearDate(20), 2, 2, 8.1),
                        new ExamRecord("Ignored", currentAcademicYearDate(10), 1, 1, 30.0)
                )
        );

        assertDoesNotThrow(() -> validationService.validateStudent(student));
    }

    @Test
    void validateStudentRejectsOlderStudentWithInsufficientCredits() {
        Student student = student(
                "4000003",
                olderMatriculationDate(),
                List.of(
                        new ExamRecord("Algorithms", previousAcademicYearDate(2), 1, 2, 10.0),
                        new ExamRecord("Databases", previousAcademicYearDate(20), 2, 2, 5.99),
                        new ExamRecord("No Date", null, 1, 1, 99.0)
                )
        );

        assertThrows(ECTSLimitException.class, () -> validationService.validateStudent(student));
    }

    @Test
    void validateAndSaveRequestRejectsInvalidSignature() {
        ValidationService spyService = Mockito.spy(new ValidationService(validationRepository));
        MultipartFile file = new MockMultipartFile("file", "dummy.pdf", "application/pdf", new byte[0]);

        doReturn(false).when(spyService).verifyPdfSignature(file);

        assertThrows(SignaturInvalidException.class, () -> spyService.validateAndSaveRequest(file, "invalid@example.com"));
    }

    @Test
    void validateAndSaveRequestWrapsParsingErrors() throws Exception {
        ValidationService spyService = Mockito.spy(new ValidationService(validationRepository));
        MultipartFile file = new MockMultipartFile("file", "dummy.pdf", "application/pdf", new byte[0]);

        doReturn(true).when(spyService).verifyPdfSignature(file);
        doThrow(new IOException("parse failed")).when(spyService).parsePdf(file);

        assertThrows(PdfParseException.class, () -> spyService.validateAndSaveRequest(file, "parse@example.com"));
    }

    @Test
    void validateAndSaveRequestCreatesValidationForEligibleStudent() throws Exception {
        ValidationService spyService = Mockito.spy(new ValidationService(validationRepository));
        MultipartFile file = new MockMultipartFile("file", "dummy.pdf", "application/pdf", new byte[0]);
        Student student = student(
                "5000001",
                olderMatriculationDate(),
                List.of(new ExamRecord("Algorithms", previousAcademicYearDate(5), 1, 2, 16.0))
        );

        doReturn(true).when(spyService).verifyPdfSignature(file);
        doReturn(student).when(spyService).parsePdf(file);

        assertDoesNotThrow(() -> spyService.validateAndSaveRequest(file, "created@example.com"));
        assertEquals("created@example.com", validationRepository.findByStudentId("5000001").getEmail());
    }

    @Test
    void createOrUpdateValidationCreatesNewAutomaticValidation() {
        Student student = student("6000001", olderMatriculationDate(), List.of());

        validationService.createOrUpdateValidation(student, "new-auto@example.com");

        Validation saved = validationRepository.findByStudentId("6000001");
        assertEquals(ValidationType.AUTOMATIC, saved.getType());
        assertEquals("new-auto@example.com", saved.getEmail());
        assertEquals(expectedAutomaticValidUntil(), saved.getValidUntil());
    }

    @Test
    void createOrUpdateValidationUpdatesExistingAutomaticValidation() {
        Student student = student("1000004", olderMatriculationDate(), List.of());

        validationService.createOrUpdateValidation(student, "updated-existing@example.com");

        Validation updated = validationRepository.findByStudentId("1000004");
        assertEquals(ValidationType.AUTOMATIC, updated.getType());
        assertEquals("updated-existing@example.com", updated.getEmail());
        assertEquals(expectedAutomaticValidUntil(), updated.getValidUntil());
    }

    @Test
    @WithMockUser(authorities = {"validation"})
    void isEligibleReturnsTrueForMatchingValidEntryAndFalseOtherwise() {
        Validation eligible = new Validation();
        eligible.setStudentId("7000001");
        eligible.setEmail("eligible@example.com");
        eligible.setType(ValidationType.MANUAL);
        eligible.setValidUntil(dateAtStartOfDay(LocalDate.now().plusDays(7)));
        validationService.save(eligible);

        Validation expired = new Validation();
        expired.setStudentId("7000002");
        expired.setEmail("expired@example.com");
        expired.setType(ValidationType.MANUAL);
        expired.setValidUntil(dateAtStartOfDay(LocalDate.now().minusDays(1)));
        validationService.save(expired);

        assertTrue(validationService.isEligible("7000001", "eligible@example.com"));
        assertFalse(validationService.isEligible("7000001", "wrong@example.com"));
        assertFalse(validationService.isEligible("7000002", "expired@example.com"));
    }

    private static ValidationDto validationDto(String studentId, String email, Date validUntil) {
        ValidationDto dto = new ValidationDto();
        dto.setStudentId(studentId);
        dto.setEmail(email);
        dto.setValidUntil(validUntil);
        return dto;
    }

    private static Student student(String studentId, LocalDate matriculationDate, List<ExamRecord> records) {
        return new Student("Test  Student,", studentId, matriculationDate, records);
    }

    private static LocalDate firstYearMatriculationDate() {
        return Util.getCurrentAcademicYear().getFirst().plusDays(1);
    }

    private static LocalDate olderMatriculationDate() {
        return Util.getPreviousAcademicYear().getFirst().minusDays(1);
    }

    private static LocalDate previousAcademicYearDate(int offsetDays) {
        Pair<LocalDate, LocalDate> previousAcademicYear = Util.getPreviousAcademicYear();
        return previousAcademicYear.getFirst().plusDays(offsetDays);
    }

    private static LocalDate currentAcademicYearDate(int offsetDays) {
        Pair<LocalDate, LocalDate> currentAcademicYear = Util.getCurrentAcademicYear();
        return currentAcademicYear.getFirst().plusDays(offsetDays);
    }

    private static Date expectedAutomaticValidUntil() {
        LocalDate validUntil = Util.getNextAcademicYear().getSecond().plusMonths(2);
        return dateAtStartOfDay(validUntil);
    }

    private static Date dateAtStartOfDay(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
