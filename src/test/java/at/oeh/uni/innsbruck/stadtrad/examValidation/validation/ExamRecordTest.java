package at.oeh.uni.innsbruck.stadtrad.examValidation.validation;

import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.ExamRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExamRecordTest {
    private ExamRecord createRecord() {
        return new ExamRecord(
                "Math",
                LocalDate.of(2025, 1, 10),
                1,
                2.0,
                5.0
        );
    }

    // ---------- EQUALS: null ----------
    @Test
    void testEquals_null() {
        ExamRecord r = createRecord();
        assertNotEquals(r, null);
    }

    // ---------- EQUALS: different class ----------
    @Test
    void testEquals_differentClass() {
        ExamRecord r = createRecord();
        assertNotEquals(r, "not a record");
    }

    // ---------- EQUALS: fully equal ----------
    @Test
    void testEquals_true() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = createRecord();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testEquals_differentCourse() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = new ExamRecord(
                "Physics",
                LocalDate.of(2025, 1, 10),
                1,
                2.0,
                5.0
        );

        assertNotEquals(r1, r2);
    }

    @Test
    void testEquals_differentDate() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = new ExamRecord(
                "Math",
                LocalDate.of(2024, 1, 10),
                1,
                2.0,
                5.0
        );

        assertNotEquals(r1, r2);
    }

    @Test
    void testEquals_differentGrade() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = new ExamRecord(
                "Math",
                LocalDate.of(2025, 1, 10),
                2,
                2.0,
                5.0
        );

        assertNotEquals(r1, r2);
    }

    @Test
    void testEquals_differentSws() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = new ExamRecord(
                "Math",
                LocalDate.of(2025, 1, 10),
                1,
                3.0,
                5.0
        );

        assertNotEquals(r1, r2);
    }

    @Test
    void testEquals_differentCredits() {
        ExamRecord r1 = createRecord();
        ExamRecord r2 = new ExamRecord(
                "Math",
                LocalDate.of(2025, 1, 10),
                1,
                2.0,
                6.0
        );

        assertNotEquals(r1, r2);
    }

    @Test
    void testGetters() {
        ExamRecord r = createRecord();

        assertEquals("Math", r.getCourse());
        assertEquals(LocalDate.of(2025, 1, 10), r.getExamDate());
        assertEquals(1, r.getGrade());
        assertEquals(2.0, r.getSws());
        assertEquals(5.0, r.getCredits());
    }

    // ---------- toString ----------
    @Test
    void testToString_notNull() {
        ExamRecord r = createRecord();

        String result = r.toString();

        assertNotNull(result);
        assertTrue(result.contains("Math"));
        assertTrue(result.contains("2025-01-10"));
    }

    // ---------- hashCode consistency ----------
    @Test
    void testHashCode_consistency() {
        ExamRecord r = createRecord();

        int hash1 = r.hashCode();
        int hash2 = r.hashCode();

        assertEquals(hash1, hash2);
    }
}
