package ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Biar addDocumentListener bisa pakai lambda:
 * txt.getDocument().addDocumentListener((SimpleDocumentListener) e -> refresh());
 */
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) { update(e); }

    @Override
    default void removeUpdate(DocumentEvent e) { update(e); }

    @Override
    default void changedUpdate(DocumentEvent e) { update(e); }
}
