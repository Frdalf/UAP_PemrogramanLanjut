package Util;

public class CsvUtil {
    public static String safe(String s) {
        if (s == null) return "";
        return s.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
