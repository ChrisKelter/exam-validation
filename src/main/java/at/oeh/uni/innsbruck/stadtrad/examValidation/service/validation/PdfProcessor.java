package at.oeh.uni.innsbruck.stadtrad.examValidation.service.validation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfProcessor {
    private static Map<String, String> deGrades() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("sehr gut", "1");
        map.put("gut", "2");
        map.put("befriedigend", "3");
        map.put("nicht genügend", "5");
        map.put("genügend", "4");
        map.put("mit Erfolg teilgenommen", "0");
        map.put("ohne Erfolg teilgenommen", "0");

        return map;
    }

    private static Map<String, String> enGrades() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("excellent", "1");
        map.put("good", "2");
        map.put("satisfactory", "3");
        map.put("insufficient", "5");
        map.put("sufficient", "4");
        map.put("successfully completed", "0");
        map.put("unsuccessfully completed", "0");

        return map;
    }

    private static Map<String, String> deKeywords = Map.of(
            "end", "Ende der Auflistung",
            "name","Familienname",
            "matrikelNr", "Matrikelnummer",
            "dateFormat", "dd.MM.yyyy",
            "startDate", "Aufnahmedatum"
    );

    private static Map<String, String> enKeywords = Map.of(
            "end", "End of records",
            "name","last name",
            "matrikelNr", "Registration no.",
            "dateFormat", "yyyy-MM-dd",
            "startDate", "Matriculation date"
    );


    private Map<String, String> keyWords = new HashMap<>();
    private Map<String, String> grades = new LinkedHashMap<>();

    public static String detectLanguage(String text) throws Exception {
        LanguageDetector detector = LanguageDetector.getDefaultLanguageDetector();
        detector.loadModels();
        LanguageResult result = detector.detect(text);
        return result.getLanguage(); // e.g. "en", "fr", "de"
    }

    private boolean startsWithLectureType(String str) {
        List<String> lectureTypes = Arrays.asList("VO", "PS", "SE", "VU", "EX", "PJ", "EX", "UE", "SL", "PR", "AG", "RE");
        return lectureTypes.stream().anyMatch(lectureType -> str.contains(lectureType+ " "));
    }

    private boolean lectureIsExamResult(String str) {
        String regex = "^\\d{6} \\(\\d{4}[SW]\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private boolean examResultEndReached(String str) {
        return str.contains(this.keyWords.get("end"));
    }

    private boolean isValidExamResult(String str) {
        String regex = "[A-Za-z]{2}\\s\\d+[,.]\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private String getLectureName(List<String> entries) {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("No lecture found");
        }

        String lectureName = entries.getFirst();
        if (lectureIsExamResult(lectureName) || startsWithLectureType(lectureName)) {
            return lectureName;
        }

        throw new IllegalArgumentException("Invalid lecture name: " + lectureName);
    }

    private String getExamInfo(List<String> entries) {
        // exam results starts always with lecutre type
        List<String> nextIsGrade = getNextGrade(entries);
        if (nextIsGrade.isEmpty()) {
            throw new IllegalArgumentException("No exam results found");
        }

        String examInfo = nextIsGrade.getFirst();
        if (isValidExamResult(examInfo)) {
            return processGrades(examInfo).replace(",", ".");
        }

        throw new IllegalArgumentException("Invalid exam result: " + examInfo);
    }

    private String processGrades(String str) {
        for (String grade : this.grades.keySet()) {
            str = str.replace(grade, this.grades.get(grade));
        }
        return str;
    }

    private List<String> getNextGrade(List<String> entries) {
        return entries.stream().skip(1).dropWhile(s -> !startsWithLectureType(s)).toList();
    }

    private List<String> getNextLecture(List<String> entries) {
        List<String> nextIsGrade = getNextGrade(entries);
        if (nextIsGrade.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> entriesToCheck = nextIsGrade.stream().skip(1).toList();

        boolean newExamResult = false;
        for (String str : entriesToCheck) {
            if (lectureIsExamResult(str) || startsWithLectureType(str)) {
                // is exam result
                newExamResult = true;
                break;
            }

            else if (examResultEndReached(str)) {
                // no exam results
                break;
            }
        }

        if (newExamResult) {
            return getListWithNextIsLecture(entriesToCheck);
        }

        // no new results
        return new ArrayList<>();
    }

    private List<String> getListWithNextIsLecture(List<String> entriesToCheck) {
        return entriesToCheck.stream().dropWhile(s -> !(startsWithLectureType(s) || lectureIsExamResult(s))).toList();
    }

    private static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private LocalDate parseDate(String str) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.keyWords.get("dateFormat"));
            return str.equals("--") ? null : LocalDate.parse(str, formatter);
        }

        catch (Exception e) {
            return null;
        }
    }

    private Record parseRecord(String lectureName, String examInfoRaw) {
        List<String> examInfo = Arrays.stream(examInfoRaw.split(" ")).toList();


        if (examInfo.size() < 5) {
            throw new IllegalArgumentException("Invalid exam result: " + examInfoRaw);
        }

        double sws = parseDouble(examInfo.get(1));
        double ectsCredits = parseDouble(examInfo.get(2));
        LocalDate examDate = parseDate(examInfo.get(3));
        int grade = parseInt(examInfo.get(4));


        return new Record(lectureName, examDate, grade, sws, ectsCredits);
    }

    public Student extractStudent(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String rawText = pdfStripper.getText(document);

            String language = detectLanguage(rawText);

            if (language.equals("en")) {
                this.keyWords = new HashMap<>(enKeywords);
                this.grades = new LinkedHashMap<>(enGrades());
            } else {
                // default german
                this.keyWords = new HashMap<>(deKeywords);
                this.grades = new LinkedHashMap<>(deGrades());
            }


            List<Record> records = new ArrayList<>();
            List<String> text = Arrays.stream(rawText.split("\\R")).toList();


            String matrikelNummer = extractInformation(text, this.keyWords.get("matrikelNr"));
            String name = extractInformation(text, this.keyWords.get("name"));
            String startDate = extractInformation(text, this.keyWords.get("startDate"));
            LocalDate parsedStartDate = parseDate(startDate);

            List<String> nextIsLecture = getListWithNextIsLecture(text);
            while (!nextIsLecture.isEmpty()) {
                String lectureName = getLectureName(nextIsLecture);
                String examInfo = getExamInfo(nextIsLecture);
                records.add(parseRecord(lectureName, examInfo));
                nextIsLecture = getNextLecture(nextIsLecture);
            }

            return new Student(name, matrikelNummer, parsedStartDate, records);
        } catch (Exception e) {
            return null;
        }
    }


    private static String extractInformation(List<String> text, String pattern) {
        List<String> dropped = text.stream().dropWhile(s -> !s.contains(pattern)).toList();
        if (dropped.size() > 2) {
            return dropped.get(1);
        }

        return "";
    }


}
