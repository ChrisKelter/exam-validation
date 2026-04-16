package at.oeh.uni.innsbruck.stadtrad.examValidation.controller;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.EligibleDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.ValidationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligible")
public class EligibleController {
    private final ValidationService validationService;

    public EligibleController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    public EligibleDto isEligible(String studentId, String email) {
        return new EligibleDto(this.validationService.isEligible(studentId, email));
    }
}
