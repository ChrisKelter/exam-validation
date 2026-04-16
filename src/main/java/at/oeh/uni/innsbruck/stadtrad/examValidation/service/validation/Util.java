package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import org.springframework.data.util.Pair;

import java.time.LocalDate;

public class Util {
    public static Pair<LocalDate, LocalDate> getCurrentAcademicYear() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();

        if (currentDate.getMonthValue() >= 10) {
            LocalDate startDay = LocalDate.of(year, 10, 1);
            LocalDate endDay = LocalDate.of(year + 1, 9, 30);
            return Pair.of(startDay, endDay);
        } else {
            LocalDate startDay = LocalDate.of(year - 1, 10, 1);
            LocalDate endDay = LocalDate.of(year, 9, 30);
            return Pair.of(startDay, endDay);
        }
    }

    public static Pair<LocalDate, LocalDate> getNextAcademicYear() {
        Pair<LocalDate, LocalDate> currentAcademicYear = getCurrentAcademicYear();
        LocalDate startDay = currentAcademicYear.getFirst().withYear(currentAcademicYear.getFirst().getYear() + 1);
        LocalDate endDay = currentAcademicYear.getSecond().withYear(currentAcademicYear.getSecond().getYear() + 1);

        return Pair.of(startDay, endDay);
    }

    public static Pair<LocalDate, LocalDate> getPreviousAcademicYear() {
        Pair<LocalDate, LocalDate> currentAcademicYear = getCurrentAcademicYear();
        LocalDate startDay = currentAcademicYear.getFirst().withYear(currentAcademicYear.getFirst().getYear() - 1);
        LocalDate endDay = currentAcademicYear.getSecond().withYear(currentAcademicYear.getSecond().getYear() - 1);

        return Pair.of(startDay, endDay);
    }

    public static boolean isDateInRange(Pair<LocalDate, LocalDate> range, LocalDate date) {
        return date.isAfter(range.getFirst().minusDays(1)) && date.isBefore(range.getSecond().plusDays(1));
    }

}
