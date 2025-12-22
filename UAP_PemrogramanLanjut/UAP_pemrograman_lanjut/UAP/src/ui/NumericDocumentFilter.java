package ui;

import javax.swing.*;
import javax.swing.text.*;

/**
 * DocumentFilter untuk field angka: hanya menerima digit (0-9).
 * Tidak ada format ribuan, hanya angka murni.
 */
public final class NumericDocumentFilter extends DocumentFilter {

    public static void install(JTextField field) {
        if (field == null) return;
        Document d = field.getDocument();
        if (d instanceof AbstractDocument ad) {
            ad.setDocumentFilter(new NumericDocumentFilter());
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) return;
        // Filter hanya digit
        String filtered = string.replaceAll("[^0-9]", "");
        super.insertString(fb, offset, filtered, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) {
            super.replace(fb, offset, length, null, attrs);
            return;
        }
        // Filter hanya digit
        String filtered = text.replaceAll("[^0-9]", "");
        super.replace(fb, offset, length, filtered, attrs);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }
}
