package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Student {
    private String firstName;
    private String lastName;
    private String matrikelNr;
    private LocalDate matriculationDate;
    private List<Record> records;

    public Student(String name, String matrikelNr, LocalDate matriculationDate, List<Record> records) {
        this.setName(name);
        this.matrikelNr = matrikelNr;
        this.matriculationDate = matriculationDate;
        this.records = records;
    }

    private void setName(String name) {
        String[] nameParts = name.split("  ");
        this.firstName = nameParts[0];
        this.lastName = nameParts[1].split(",")[0];
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMatrikelNr() {
        return matrikelNr;
    }

    public List<Record> getRecords() {
        return records;
    }

    public LocalDate getMatriculationDate() {
        return matriculationDate;
    }

    public boolean isFirstYear() {
        LocalDate mariculationDate = getMatriculationDate();

        Pair<LocalDate, LocalDate> currentYear = Util.getCurrentAcademicYear();
        Pair<LocalDate, LocalDate> lastYear = Util.getCurrentAcademicYear();

        return Util.isDateInRange(currentYear, mariculationDate) || Util.isDateInRange(lastYear, mariculationDate);
    }

    public List<Record> getRecordsInRange(Pair<LocalDate, LocalDate> range) {
        return this.records.stream()
                .filter(s  -> s.getExamDate() != null && Util.isDateInRange(range, s.getExamDate()))
                .collect(Collectors.toList());
    }
}
