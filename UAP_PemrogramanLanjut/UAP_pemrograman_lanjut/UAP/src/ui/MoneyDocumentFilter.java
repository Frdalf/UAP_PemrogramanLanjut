package ui;

import Util.MoneyUtil;

import javax.swing.*;
import javax.swing.text.*;

/**
 * DocumentFilter untuk field nominal: hanya digit dan otomatis memberi pemisah ribuan (.)
 * Contoh: 1000000 -> 1.000.000
 */
public final class MoneyDocumentFilter extends DocumentFilter {

    private final JTextField field;
    private boolean internalChange = false;

    public MoneyDocumentFilter(JTextField field) {
        this.field = field;
    }

    public static void install(JTextField field) {
        if (field == null) return;
        Document d = field.getDocument();
        if (d instanceof AbstractDocument ad) {
            ad.setDocumentFilter(new MoneyDocumentFilter(field));
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (internalChange) {
            super.insertString(fb, offset, string, attr);
            return;
        }
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (internalChange) {
            super.remove(fb, offset, length);
            return;
        }
        replace(fb, offset, length, "", null);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (internalChange) {
            super.replace(fb, offset, length, text, attrs);
            return;
        }

        Document doc = fb.getDocument();
        String oldText = doc.getText(0, doc.getLength());

        StringBuilder sb = new StringBuilder(oldText);
        sb.replace(offset, offset + length, text == null ? "" : text);
        String candidate = sb.toString();

        // Hitung jumlah digit sebelum caret (berdasarkan posisi offset)
        int digitsBefore = countDigits(candidate.substring(0, Math.min(candidate.length(), offset + (text == null ? 0 : text.length()))));

        // Ambil digit saja
        String digits = candidate.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            setTextSafely(fb, "", 0);
            return;
        }

        long value;
        try {
            value = Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return;
        }

        String formatted = MoneyUtil.format(value);
        int newCaret = caretFromDigitCount(formatted, digitsBefore);
        setTextSafely(fb, formatted, newCaret);
    }

    private void setTextSafely(FilterBypass fb, String text, int caretPos) throws BadLocationException {
        internalChange = true;
        try {
            fb.replace(0, fb.getDocument().getLength(), text, null);
        } finally {
            internalChange = false;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                field.setCaretPosition(Math.max(0, Math.min(caretPos, field.getText().length())));
            } catch (Exception ignored) {}
        });
    }

    private static int countDigits(String s) {
        int c = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) c++;
        }
        return c;
    }

    private static int caretFromDigitCount(String formatted, int digitsBefore) {
        if (digitsBefore <= 0) return 0;
        int digitsSeen = 0;
        for (int i = 0; i < formatted.length(); i++) {
            if (Character.isDigit(formatted.charAt(i))) {
                digitsSeen++;
                if (digitsSeen >= digitsBefore) {
                    return i + 1;
                }
            }
        }
        return formatted.length();
    }
}
