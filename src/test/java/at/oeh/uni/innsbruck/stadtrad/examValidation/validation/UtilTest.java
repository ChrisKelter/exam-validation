package at.oeh.uni.innsbruck.stadtrad.examValidation.validation;

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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
public class UtilTest {
    static Stream<Arguments> academicYearProvider() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2025, 7, 10),
                        LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30)
                ),

                Arguments.of(
                        LocalDate.of(2025, 10, 2),
                        LocalDate.of(2025, 10, 1),
                        LocalDate.of(2026, 9, 30)
                ),

                Arguments.of(
                        LocalDate.of(2025, 9, 30),
                        LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30)
                ),

                Arguments.of(
                        LocalDate.of(2025, 1, 10),
                        LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30)
                )
        );
    }


    @ParameterizedTest
    @MethodSource("academicYearProvider")
    public void testCurrentAcademicYearProvider(LocalDate fakeToday,
                                                LocalDate expectedStart,
                                                LocalDate expectedEnd) {
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fakeToday);
            mocked.when(() -> LocalDate.of(anyInt(), anyInt(), anyInt())).thenCallRealMethod();

            Pair<LocalDate, LocalDate> year = Util.getCurrentAcademicYear();

            Assertions.assertEquals(expectedStart, year.getFirst());
            Assertions.assertEquals(expectedEnd, year.getSecond());
        }

        catch (Exception e) {
            Assertions.fail();
        }
    }


    @ParameterizedTest
    @MethodSource("academicYearProvider")
    public void testNextAcademicYearProvider(LocalDate fakeToday,
                                                LocalDate expectedStart,
                                                LocalDate expectedEnd) {
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fakeToday);
            mocked.when(() -> LocalDate.of(anyInt(), anyInt(), anyInt())).thenCallRealMethod();

            Pair<LocalDate, LocalDate> year = Util.getNextAcademicYear();

            Assertions.assertEquals(expectedStart.plusYears(1), year.getFirst());
            Assertions.assertEquals(expectedEnd.plusYears(1), year.getSecond());
        }

        catch (Exception e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @MethodSource("academicYearProvider")
    public void testPreviousAcademicYearProvider(LocalDate fakeToday,
                                             LocalDate expectedStart,
                                             LocalDate expectedEnd) {
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fakeToday);
            mocked.when(() -> LocalDate.of(anyInt(), anyInt(), anyInt())).thenCallRealMethod();

            Pair<LocalDate, LocalDate> year = Util.getPreviousAcademicYear();

            Assertions.assertEquals(expectedStart.minusYears(1), year.getFirst());
            Assertions.assertEquals(expectedEnd.minusYears(1), year.getSecond());
        }

        catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testIsDateInRange_insideRange() {
        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        LocalDate inside = LocalDate.of(2025, 1, 10);

        assertTrue(Util.isDateInRange(range, inside));
    }

    @Test
    void testIsDateInRange_onStartBoundary_true() {

        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        assertTrue(Util.isDateInRange(range, LocalDate.of(2024, 10, 1)));
    }

    @Test
    void testIsDateInRange_onStartBoundary_false() {

        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        assertFalse(Util.isDateInRange(range, LocalDate.of(2024, 9, 30)));
    }

    @Test
    void testIsDateInRange_onEndBoundary_true() {
        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        assertTrue(Util.isDateInRange(range, LocalDate.of(2025, 9, 30)));
    }

    @Test
    void testIsDateInRange_onEndBoundary_false() {
        Pair<LocalDate, LocalDate> range =
                Pair.of(LocalDate.of(2024, 10, 1),
                        LocalDate.of(2025, 9, 30));

        assertFalse(Util.isDateInRange(range, LocalDate.of(2025, 10, 1)));
    }

}
