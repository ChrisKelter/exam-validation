package at.oeh.uni.innsbruck.stadtrad.examValidation.controller;

import at.oeh.uni.innsbruck.stadtrad.examValidation.service.ValidationService;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.exception.ValidationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/validate")
public class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @RequestMapping
    public void validate(@RequestParam MultipartFile file, @RequestParam String email) throws ValidationException {
        this.validationService.validateAndSaveRequest(file, email);
    }
}
