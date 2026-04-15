package at.oeh.uni.innsbruck.stadtrad.examValidation.service;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.ValidationDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.ValidationType;
import at.oeh.uni.innsbruck.stadtrad.examValidation.repository.ValidationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        validation.setLastUpdate(new Date());
        return save(validation);
    }

    public Validation createAutomaticValidation(ValidationDto dto, Date validUntil) {
        Validation validation = new Validation();
        validation.setValidUntil(validUntil);
        validation.setEmail(dto.getEmail());
        validation.setType(ValidationType.AUTOMATIC);
        validation.setLastUpdate(new Date());
        return save(validation);
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
}
