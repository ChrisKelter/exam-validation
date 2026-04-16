package at.oeh.uni.innsbruck.stadtrad.examValidation.dto;

import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.ValidationType;

import java.util.Date;

public class ValidationDto {
    private String studentId;
    private String email;
    private ValidationType type;
    private Date validUntil;
    private Date lastUpdate;
    private boolean status;
    private String statusValue;

    public static ValidationDto from(Validation validation) {
        ValidationDto dto = new ValidationDto();
        dto.setStudentId(validation.getStudentId());
        dto.setEmail(validation.getEmail());
        dto.setType(validation.getType());
        dto.setValidUntil(validation.getValidUntil());
        dto.setLastUpdate(validation.getLastUpdate());
        dto.setStatus(validation.getValidUntil().after(new Date()));
        dto.statusValue = dto.isStatus() ? "VALID" : "INVALID";
        return dto;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ValidationType getType() {
        return type;
    }

    public void setType(ValidationType type) {
        this.type = type;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }
}
