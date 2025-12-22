package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Kumpulan komponen UI ringan untuk membuat tampilan form "soft blue"
 * (mirip mockup yang kamu kirim) tanpa butuh aset icon eksternal.
 * Supports dark mode via ThemeManager.
 */
public final class SoftFormUI {
    private SoftFormUI() {}

    // ===== Dynamic Colors (from ThemeManager) =====
    public static Color getTitleBlue() { return ThemeManager.getTitleColor(); }
    public static Color getPillBlue() { return ThemeManager.getPillBlue(); }
    public static Color getFieldBorder() { return ThemeManager.getFieldBorder(); }
    public static Color getFieldFillTop() { return ThemeManager.getFieldFillTop(); }
    public static Color getFieldFillBottom() { return ThemeManager.getFieldFillBottom(); }
    public static Color getFieldBorderDisabled() { return ThemeManager.getFieldBorderDisabled(); }
    public static Color getFieldFillDisabled() { return ThemeManager.getFieldFillDisabled(); }
    public static Color getIconBlue() { return ThemeManager.getIconColor(); }

    // Legacy constants for backwards compatibility (will use dynamic values)
    @Deprecated public static final Color TITLE_BLUE = new Color(20, 20, 20);
    @Deprecated public static final Color PILL_BLUE = new Color(40, 125, 235);
    @Deprecated public static final Color FIELD_BORDER = new Color(120, 195, 235);
    @Deprecated public static final Color FIELD_FILL_TOP = new Color(232, 246, 255);
    @Deprecated public static final Color FIELD_FILL_BOTTOM = new Color(220, 242, 255);
    @Deprecated public static final Color FIELD_BORDER_DISABLED = new Color(185, 205, 220);
    @Deprecated public static final Color FIELD_FILL_DISABLED = new Color(242, 245, 248);
    @Deprecated public static final Color ICON_BLUE = new Color(45, 120, 215);

    // ===== Styling helper untuk JComboBox supaya menyatu dengan IconField =====
    private static void applySoftComboBoxStyle(JComboBox<?> cb) {
        cb.setBorder(null);
        cb.setOpaque(false);
        cb.setBackground(new Color(0, 0, 0, 0));
        cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cb.setForeground(ThemeManager.getTextPrimary());
        
        // Add theme listener to update foreground
        ThemeManager.addThemeChangeListener(() -> {
            cb.setForeground(ThemeManager.getTextPrimary());
            cb.repaint();
        });

        // Renderer: saat tampil di field (index == -1) dibuat transparan
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                // IMPORTANT: untuk tampilan di field (index < 0), jangan ikut "cellHasFocus"
                // karena DefaultListCellRenderer akan menggambar outline fokus (kotak abu-abu).
                JLabel l;
                if (index < 0) {
                    l = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);
                    l.setOpaque(false);
                    l.setForeground(ThemeManager.getTextPrimary());
                } else {
                    l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    l.setOpaque(true);
                    if (!isSelected) {
                        l.setForeground(ThemeManager.getTextPrimary());
                        l.setBackground(ThemeManager.getPopupBackground());
                    }
                }
                l.setBorder(new EmptyBorder(4, 8, 4, 8));
                return l;
            }
        });

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(ThemeManager.getIconColor());
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                        int w = getWidth();
                        int h = getHeight();
                        int cx = w / 2;
                        int cy = h / 2 + 1;
                        // chevron down
                        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);

                        g2.dispose();
                    }
                };
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setRolloverEnabled(false);
                b.setPreferredSize(new Dimension(30, 22));
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // jangan gambar background bawaan, biar menyatu dengan IconField
            }



            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Paksa renderer tidak "focus" supaya tidak menggambar focus-rect/outline.
                ListCellRenderer<Object> r = (ListCellRenderer<Object>) comboBox.getRenderer();
                Component c = r.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setFont(comboBox.getFont());
                if (c instanceof JComponent jc) {
                    jc.setOpaque(false);
                    jc.setBorder(new EmptyBorder(4, 8, 4, 8));
                }
                // Jangan gambar background bawaan; IconField sudah menggambar fill-nya.
                currentValuePane.paintComponent(g, c, comboBox,
                        bounds.x, bounds.y, bounds.width, bounds.height, true);
            }
            @Override
            protected ComboPopup createPopup() {
                // Jangan pakai popup default LAF (kadang memunculkan artefak/"ghost" teks)
                // Buat BasicComboPopup yang benar-benar opaque (background solid).
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void configureList() {
                        super.configureList();
                        list.setOpaque(true);
                        list.setBackground(ThemeManager.getPopupBackground());
                        list.setSelectionBackground(ThemeManager.getPopupSelectionBackground());
                        list.setSelectionForeground(ThemeManager.getPopupSelectionForeground());
                        list.setFixedCellHeight(32);
                    }

                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane sp = super.createScroller();
                        sp.setOpaque(true);
                        sp.getViewport().setOpaque(true);
                        sp.getViewport().setBackground(ThemeManager.getPopupBackground());
                        sp.setBorder(BorderFactory.createLineBorder(getFieldBorder(), 1));
                        return sp;
                    }
                };
            }
        });
        // Pastikan tidak ada border bawaan dari JComboBox (menghilangkan kotak outline di dalam IconField)
        cb.setBorder(new EmptyBorder(0, 0, 0, 0));
        cb.setFocusable(false);

    }

    // ===== Background =====
    public static class FormBackground extends JPanel {
        public FormBackground() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getBackgroundPrimary(),
                    w, h, ThemeManager.getBackgroundSecondary());
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // soft blobs
            g2.setColor(ThemeManager.getBlobLight());
            g2.fillOval((int) (w * 0.55), (int) (h * 0.02), (int) (w * 0.55), (int) (w * 0.55));

            g2.setColor(ThemeManager.getBlobAccent());
            g2.fillOval((int) (w * 0.38), (int) (h * 0.52), (int) (w * 0.60), (int) (w * 0.60));

            g2.dispose();
        }
    }

    // ===== Card (white rounded + soft shadow) =====
    public static class CardPanel extends JPanel {
        private final int radius;

        public CardPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Shadow (drawn inside bounds, so it never looks "kepotong")
            g2.setColor(new Color(0, 0, 0, ThemeManager.isDarkMode() ? 30 : 18));
            int shX = 5, shY = 7;
            g2.fillRoundRect(shX, shY, Math.max(1, w - shX - 1), Math.max(1, h - shY - 1), radius, radius);

            // Card
            g2.setColor(ThemeManager.getCardBackground());
            g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            g2.setColor(ThemeManager.getCardBorder());
            g2.drawRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== Pill label (tab-like) =====
    public static class PillLabel extends JLabel {
        public PillLabel(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(ThemeManager.getPillBlue());
            g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.setColor(ThemeManager.getPillBlueBorder());
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== Icon types =====
    public enum IconType {
        ID, USER, PHONE, PIN,
        CALENDAR, LIST, TAG, MONEY,
        BOX, NUMBER, NOTE,
        SEARCH
    }

    /** Icon sederhana (vector) supaya tidak butuh file gambar. */
    public static class SoftIcon implements Icon {
        private final IconType type;
        private final int size;

        public SoftIcon(IconType type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeManager.getIconColor());

            int s = size;
            int cx = x;
            int cy = y;

            Color iconBgLight = ThemeManager.getIconBgLight();

            switch (type) {
                case ID -> {
                    g2.drawRoundRect(cx + 1, cy + 3, s - 3, s - 6, 6, 6);
                    g2.fillOval(cx + 4, cy + 6, 6, 6);
                    g2.fillRoundRect(cx + 12, cy + 7, s - 16, 2, 2, 2);
                    g2.fillRoundRect(cx + 12, cy + 12, s - 18, 2, 2, 2);
                }
                case USER -> {
                    g2.fillOval(cx + 6, cy + 3, s - 12, s - 12);
                    g2.fillRoundRect(cx + 4, cy + (s / 2) + 2, s - 8, (s / 2) - 4, 10, 10);
                }
                case PHONE -> {
                    g2.drawRoundRect(cx + 7, cy + 2, s - 14, s - 4, 6, 6);
                    g2.fillOval(cx + (s / 2) - 1, cy + s - 5, 3, 3);
                }
                case PIN -> {
                    g2.fillOval(cx + 7, cy + 3, s - 14, s - 14);
                    Polygon p = new Polygon();
                    p.addPoint(cx + s / 2, cy + s - 2);
                    p.addPoint(cx + s / 2 - 6, cy + s / 2 + 4);
                    p.addPoint(cx + s / 2 + 6, cy + s / 2 + 4);
                    g2.fillPolygon(p);
                    g2.setColor(iconBgLight);
                    g2.fillOval(cx + s / 2 - 2, cy + s / 2 - 2, 4, 4);
                }
                case CALENDAR -> {
                    g2.drawRoundRect(cx + 2, cy + 4, s - 4, s - 6, 6, 6);
                    g2.fillRoundRect(cx + 2, cy + 4, s - 4, 6, 6, 6);
                    g2.setColor(iconBgLight);
                    g2.fillRect(cx + 4, cy + 12, s - 8, s - 16);
                    g2.setColor(ThemeManager.getIconColor());
                    g2.fillRect(cx + 6, cy + 14, 3, 3);
                    g2.fillRect(cx + 11, cy + 14, 3, 3);
                    g2.fillRect(cx + 16, cy + 14, 3, 3);
                }
                case LIST -> {
                    for (int i = 0; i < 3; i++) {
                        int yy = cy + 5 + i * 6;
                        g2.fillOval(cx + 3, yy, 3, 3);
                        g2.fillRoundRect(cx + 9, yy, s - 12, 3, 2, 2);
                    }
                }
                case TAG -> {
                    Polygon p = new Polygon();
                    p.addPoint(cx + 4, cy + 4);
                    p.addPoint(cx + s - 4, cy + s / 2);
                    p.addPoint(cx + 4, cy + s - 4);
                    g2.fillPolygon(p);
                    g2.setColor(iconBgLight);
                    g2.fillOval(cx + 6, cy + s / 2 - 2, 4, 4);
                }
                case MONEY -> {
                    g2.fillOval(cx + 4, cy + 6, s - 8, s - 8);
                    g2.setColor(iconBgLight);
                    g2.fillOval(cx + 7, cy + 9, s - 14, s - 14);
                    g2.setColor(ThemeManager.getIconColor());
                    g2.drawOval(cx + 7, cy + 9, s - 14, s - 14);
                    g2.fillRoundRect(cx + 9, cy + 12, s - 18, 2, 2, 2);
                }
                case BOX -> {
                    g2.drawRoundRect(cx + 4, cy + 6, s - 8, s - 10, 6, 6);
                    g2.drawLine(cx + 4, cy + 10, cx + s - 4, cy + 10);
                    g2.drawLine(cx + s / 2, cy + 6, cx + s / 2, cy + s - 4);
                }
                case NUMBER -> {
                    g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, s - 10)));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = "#";
                    int tx = cx + (s - fm.stringWidth(t)) / 2;
                    int ty = cy + (s + fm.getAscent() - fm.getDescent()) / 2 - 1;
                    g2.drawString(t, tx, ty);
                }
                case NOTE -> {
                    g2.drawRoundRect(cx + 5, cy + 3, s - 10, s - 6, 6, 6);
                    g2.drawLine(cx + 8, cy + 9, cx + s - 8, cy + 9);
                    g2.drawLine(cx + 8, cy + 13, cx + s - 10, cy + 13);
                    g2.drawLine(cx + 8, cy + 17, cx + s - 12, cy + 17);
                }
                case SEARCH -> {
                    // magnifier
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(cx + 4, cy + 4, s - 10, s - 10);
                    g2.drawLine(cx + s - 8, cy + s - 8, cx + s - 3, cy + s - 3);
                }
            }

            g2.dispose();
        }
    }

    // ===== Field wrapper (rounded input with icon) =====
    public static class IconField extends JPanel {
        private final JComponent inner;
        private boolean focused = false;
        private boolean hovering = false;
        private final int radius;

        public IconField(IconType iconType, JComponent inner) {
            this(iconType, inner, 18);
        }

        public IconField(IconType iconType, JComponent inner, int radius) {
            this.inner = inner;
            this.radius = radius;
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(10, 12, 10, 12));

            JLabel icon = new JLabel(new SoftIcon(iconType, 20));
            icon.setOpaque(false);
            add(icon, BorderLayout.WEST);

            prepareInner(inner);
            add(inner, BorderLayout.CENTER);

            // focus tracking
            installFocusTracker(inner);

            // hover tracking (micro-interaction)
            MouseAdapter hover = new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            };
            addMouseListener(hover);
            // also attach to inner so moving mouse over the actual input keeps the hover state
            installHoverTracker(inner, hover);
        }

        private void prepareInner(JComponent c) {
            c.setOpaque(false);
            c.setBorder(null);

            if (c instanceof JTextField tf) {
                tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
                tf.setForeground(ThemeManager.getTextPrimary());
                tf.setCaretColor(ThemeManager.getTextPrimary());
                ThemeManager.addThemeChangeListener(() -> {
                    tf.setForeground(ThemeManager.getTextPrimary());
                    tf.setCaretColor(ThemeManager.getTextPrimary());
                });
            }

            if (c instanceof JComboBox<?> cb) {
                applySoftComboBoxStyle(cb);
            }

            if (c instanceof JSpinner sp) {
                sp.setBorder(null);
                sp.setOpaque(false);
                JComponent editor = sp.getEditor();
                if (editor instanceof JSpinner.DefaultEditor de) {
                    JTextField spinnerTf = de.getTextField();
                    spinnerTf.setOpaque(false);
                    spinnerTf.setBorder(null);
                    spinnerTf.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    spinnerTf.setForeground(ThemeManager.getTextPrimary());
                    spinnerTf.setCaretColor(ThemeManager.getTextPrimary());
                    // Supaya angka tidak "nempel" di kanan (lebih rapi & konsisten dengan input lain)
                    spinnerTf.setHorizontalAlignment(SwingConstants.LEFT);
                    ThemeManager.addThemeChangeListener(() -> {
                        spinnerTf.setForeground(ThemeManager.getTextPrimary());
                        spinnerTf.setCaretColor(ThemeManager.getTextPrimary());
                    });
                }
            }
        }

        private void installFocusTracker(JComponent c) {
            // track focus on inner components (textfield, combobox editor, spinner editor, etc.)
            if (c instanceof JTextField tf) {
                tf.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JComboBox<?> cb) {
                cb.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JSpinner sp) {
                sp.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JScrollPane sp) {
                Component view = sp.getViewport().getView();
                if (view instanceof JComponent jc) installFocusTracker(jc);
            } else if (c instanceof JTextArea ta) {
                ta.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            }
        }

        private void installHoverTracker(JComponent c, MouseAdapter hover) {
            c.addMouseListener(hover);
            if (c instanceof JScrollPane sp) {
                Component view = sp.getViewport().getView();
                if (view instanceof JComponent jc) installHoverTracker(jc, hover);
            } else if (c instanceof JSpinner sp) {
                JComponent editor = sp.getEditor();
                if (editor instanceof JComponent jc) installHoverTracker(jc, hover);
            } else if (c instanceof JComboBox<?> cb) {
                Component ed = cb.getEditor() != null ? cb.getEditor().getEditorComponent() : null;
                if (ed instanceof JComponent jc) installHoverTracker(jc, hover);
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            inner.setEnabled(enabled);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            boolean enabled = inner.isEnabled();

            // shadow (offset but still inside bounds)
            g2.setColor(new Color(0, 0, 0, ThemeManager.isDarkMode() ? 20 : 12));
            int shX = 3, shY = 5;
            g2.fillRoundRect(shX, shY, Math.max(1, w - shX - 1), Math.max(1, h - shY - 1), radius, radius);

            // fill
            if (!enabled) {
                g2.setColor(ThemeManager.getFieldFillDisabled());
                g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getFieldFillTop(), 0, h, ThemeManager.getFieldFillBottom());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            }

            // border + subtle glow (micro-interaction)
            Color baseBorder;
            if (!enabled) baseBorder = ThemeManager.getFieldBorderDisabled();
            else if (focused) baseBorder = ThemeManager.getFieldFocusBorder();
            else if (hovering) baseBorder = ThemeManager.getFieldHoverBorder();
            else baseBorder = ThemeManager.getFieldBorder();

            // glow when focused
            if (enabled && focused) {
                int inset = 2;
                g2.setColor(ThemeManager.getFieldFocusGlow());
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(inset, inset, Math.max(1, w - inset * 2 - 1), Math.max(1, h - inset * 2 - 1), radius, radius);
            }

            // main border
            g2.setColor(baseBorder);
            g2.setStroke(new BasicStroke((enabled && (focused || hovering)) ? 2f : 1.2f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int bInset = 1;
            g2.drawRoundRect(bInset, bInset, Math.max(1, w - bInset * 2 - 1), Math.max(1, h - bInset * 2 - 1), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

}
