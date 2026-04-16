package at.oeh.uni.innsbruck.stadtrad.examValidation.validation;

import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.ExamRecord;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Student;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class StudentTest {
    @Test
    void testStudentInitialization() {
        LocalDate fakeToday = LocalDate.of(2020, 1, 1);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fakeToday);

            Student s = new Student(
                    "John  Doe, something",
                    "123",
                    LocalDate.now(),
                    List.of()
            );

            assertEquals("John", s.getFirstName());
            assertEquals("Doe", s.getLastName());
            assertEquals(fakeToday, s.getMatriculationDate());
            assertEquals("123", s.getMatrikelNr());
            assertEquals(0, s.getRecords().size());
        }

        catch (Exception e) {
            Assertions.fail();
        }
    }

    static Stream<Arguments> firstYearProvider() {
        return Stream.of(
                // CURRENT YEAR (2025/2026)

                // outside of previous academic year (2024/2025) (FALSE - not first year)
                Arguments.of(LocalDate.of(2024, 7, 4), false),
                Arguments.of(LocalDate.of(2024, 8, 13), false),
                Arguments.of(LocalDate.of(2024, 9, 30), false),

                // inside of previous academic year (2024/2025) (TRUE - is first year)
                Arguments.of(LocalDate.of(2024, 10, 1), true),
                Arguments.of(LocalDate.of(2025, 1, 20), true),
                Arguments.of(LocalDate.of(2025, 2, 23), true),
                Arguments.of(LocalDate.of(2025, 7, 4), true),
                Arguments.of(LocalDate.of(2025, 8, 13), true),
                Arguments.of(LocalDate.of(2025, 9, 30), true),

                // inside current academic year 2025/2026 (TRUE - is first year)
                Arguments.of(LocalDate.of(2025, 10, 1), true),
                Arguments.of(LocalDate.of(2025, 10, 30), true),
                Arguments.of(LocalDate.of(2026, 3, 15), true),
                Arguments.of(LocalDate.of(2026, 7, 4), true),
                Arguments.of(LocalDate.of(2026, 8, 13), true),
                Arguments.of(LocalDate.of(2026, 9, 30), true),

                // outside current academic year 2025/2026 (FALSE - is next year)
                Arguments.of(LocalDate.of(2026, 10, 1), false),
                Arguments.of(LocalDate.of(2026, 10, 30), false)
        );
    }

    @ParameterizedTest
    @MethodSource("firstYearProvider")
    public void testIsFirstYear_multipleCases(LocalDate matriculationDate, boolean expected) {

        try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class)) {
            mocked.when(Util::getCurrentAcademicYear).thenReturn(Pair.of(LocalDate.of(2025, 10, 1), LocalDate.of(2026, 9, 30)));
            mocked.when(Util::getPreviousAcademicYear).thenReturn(Pair.of(LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30)));
            mocked.when(() -> Util.isDateInRange(any(), any())).thenCallRealMethod();


            Student s = new Student(
                    "John  Doe, x",
                    "1",
                    matriculationDate,
                    List.of()
            );

            assertEquals(expected, s.isFirstYear());
        }

        catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetRecordsInRange() {
        ExamRecord r1 = Mockito.mock(ExamRecord.class); // valid in range
        ExamRecord r2 = Mockito.mock(ExamRecord.class); // null date
        ExamRecord r3 = Mockito.mock(ExamRecord.class); // out of range

        LocalDate d1 = LocalDate.of(2025, 1, 10);
        LocalDate d3 = LocalDate.of(2020, 1, 1);

        Mockito.when(r1.getExamDate()).thenReturn(d1);
        Mockito.when(r2.getExamDate()).thenReturn(null);
        Mockito.when(r3.getExamDate()).thenReturn(d3);

        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class)) {

            // keep logic simple and deterministic
            mocked.when(() -> Util.isDateInRange(range, d1)).thenReturn(true);
            mocked.when(() -> Util.isDateInRange(range, d3)).thenReturn(false);

            Student s = new Student(
                    "John  Doe, x",
                    "1",
                    LocalDate.now(),
                    List.of(r1, r2, r3)
            );

            List<ExamRecord> result = s.getRecordsInRange(range);

            assertEquals(1, result.size());
            assertTrue(result.contains(r1));
            assertFalse(result.contains(r2)); // null branch
            assertFalse(result.contains(r3)); // out-of-range branch
        }
    }

}
