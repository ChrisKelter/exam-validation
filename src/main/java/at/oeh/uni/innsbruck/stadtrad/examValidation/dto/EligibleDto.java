package at.oeh.uni.innsbruck.stadtrad.examValidation.dto;

public class EligibleDto {
    private boolean eligible;

    public EligibleDto() {

    }

    public EligibleDto(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
