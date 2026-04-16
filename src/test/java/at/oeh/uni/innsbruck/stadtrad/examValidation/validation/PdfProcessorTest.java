package at.oeh.uni.innsbruck.stadtrad.examValidation.validation;

import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.ExamRecord;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.PdfProcessor;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class PdfProcessorTest {
    @Test
    public void testEN() throws IOException {
        PdfProcessor pdfProcessor = new PdfProcessor();
        Student student = pdfProcessor.extractStudent(new FileInputStream("testdata/record-en.pdf"));
        assertStudent(student);

        List<ExamRecord> recordList = student.getRecords();
        Assertions.assertEquals(2, recordList.size());

        ExamRecord expectedRecord1 = new ExamRecord("VO Introduction to Programming*", null , 1,3, 4.5);
        ExamRecord expectedRecord2 = new ExamRecord("703078 (2020S) PS Parallel Programming*", LocalDate.of(2020, 6, 24), 1, 1, 2);

        Assertions.assertEquals(expectedRecord1, recordList.getFirst());
        Assertions.assertEquals(expectedRecord2, recordList.getLast());
    }

    @Test
    public void testDE() throws IOException {
        PdfProcessor pdfProcessor = new PdfProcessor();
        Student student = pdfProcessor.extractStudent(new FileInputStream("testdata/record-de.pdf"));
        assertStudent(student);

        List<ExamRecord> recordList = student.getRecords();
        Assertions.assertEquals(2, recordList.size());

        ExamRecord expectedRecord1 = new ExamRecord("VO Einführung in die Programmierung*", null , 1,3, 4.5);
        ExamRecord expectedRecord2 = new ExamRecord("703078 (2020S) PS Parallele Programmierung*", LocalDate.of(2020, 6, 24), 1, 1, 2);

        Assertions.assertEquals(expectedRecord1, recordList.getFirst());
        Assertions.assertEquals(expectedRecord2, recordList.getLast());
    }

    private static void assertStudent(Student student) {
        Assertions.assertEquals("Christopher", student.getFirstName());
        Assertions.assertEquals("Kelter", student.getLastName());
        Assertions.assertEquals("11722390", student.getMatrikelNr());
        Assertions.assertEquals(LocalDate.of(2019,9,19), student.getMatriculationDate());
    }
}
