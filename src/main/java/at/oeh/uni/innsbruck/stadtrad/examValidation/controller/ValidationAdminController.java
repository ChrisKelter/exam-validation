package at.oeh.uni.innsbruck.stadtrad.examValidation.controller;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.PageDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.ValidationDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/validation")
public class ValidationAdminController {
    private final ValidationService validationService;

    public ValidationAdminController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/paged")
    public PageDto<ValidationDto> getValidations(@RequestParam int page, @RequestParam int size, @RequestParam String searchInput) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Validation> paged =  searchInput.isEmpty() ?
                this.validationService.getValidationPaged(pageable) :
                this.validationService.getValidationPaged(pageable, searchInput);
        PageDto<ValidationDto> pageDto = PageDto.fromPaged(paged);
        pageDto.setContent(paged.getContent().stream().map(ValidationDto::from).collect(Collectors.toList()));
        return pageDto;
    }

    @PostMapping
    public ValidationDto createValidation(@RequestBody ValidationDto validationDto) {
        return ValidationDto.from(this.validationService.createManualValidation(validationDto));
    }

    @PutMapping
    public ValidationDto updateValidation(@RequestBody ValidationDto validationDto) {
        return ValidationDto.from(this.validationService.updateManualValidation(validationDto));
    }

    @DeleteMapping
    public void deleteValidation(@RequestParam String studentId) {
        this.validationService.remove(studentId);
    }
}
