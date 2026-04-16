package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import java.time.LocalDate;
import java.util.Objects;

public class ExamRecord {
    private String course;
    private LocalDate examDate;
    private int grade;
    private double sws;
    private double credits;

    public ExamRecord(String course, LocalDate examDate, int grade, double sws, double credits) {
        this.course = course;
        this.examDate = examDate;
        this.grade = grade;
        this.sws = sws;
        this.credits = credits;
    }

    public String getCourse() {
        return course;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public int getGrade() {
        return grade;
    }

    public double getSws() {
        return sws;
    }

    public double getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return "Record{" +
                "course='" + course + '\'' +
                ", examDate=" + examDate +
                ", grade=" + grade +
                ", sws=" + sws +
                ", credits=" + credits +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExamRecord record = (ExamRecord) o;
        return grade == record.grade &&
                Double.compare(sws, record.sws) == 0 &&
                Double.compare(credits, record.credits) == 0 &&
                Objects.equals(course, record.course) &&
                Objects.equals(examDate, record.examDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, examDate, grade, sws, credits);
    }
}
