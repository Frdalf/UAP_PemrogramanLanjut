package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public final class SimpleTableTheme {

    private SimpleTableTheme() {}

    // ===== Theme Colors =====
    private static final Color BASE_BG   = new Color(11, 18, 30);  // background table kosong + gap antar row
    private static final Color ROW_ODD   = new Color(18, 29, 46);  // dark navy
    private static final Color ROW_EVEN  = new Color(23, 38, 59);  // slightly lighter
    private static final Color FG        = new Color(235, 241, 255);
    private static final Color SEL_BG    = new Color(46, 117, 182);
    private static final Color SEL_FG    = Color.WHITE;

    private static final Color HEADER_BG = new Color(10, 20, 36);
    private static final Color HEADER_FG = new Color(220, 233, 255);

    // ===== Public API =====
    public static void applyBlue(JTable table, JScrollPane sp) {
        applyBlueInternal(table);

        if (sp != null) {
            // ✅ padding untuk header + body (bukan viewportBorder)
            sp.setBorder(new EmptyBorder(10, 10, 10, 10));
            sp.setOpaque(false);

            sp.getViewport().setOpaque(true);
            sp.getViewport().setBackground(BASE_BG);
        }
    }

    public static void applyBlue(JTable table) {
        applyBlue(table, null);
    }

    // ===== Implementation =====
    private static void applyBlueInternal(JTable table) {
        table.setRowHeight(38);

        // ✅ biar nggak kaku, tapi tetap rapi
        table.setIntercellSpacing(new Dimension(0, 6)); // jarak antar row
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ✅ penting: gap antar row pakai background table
        table.setOpaque(true);
        table.setBackground(BASE_BG);
        table.setForeground(FG);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new HeaderRenderer());

        // 1 renderer untuk semua cell (angka auto rata kanan)
        TableCellRenderer body = new BodyCellRenderer();
        table.setDefaultRenderer(Object.class, body);
        table.setDefaultRenderer(Number.class, body);
    }

    // ===== Renderers =====
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setBackground(HEADER_BG);
            setForeground(HEADER_FG);
            setFont(getFont().deriveFont(Font.BOLD, 12.5f));
            setHorizontalAlignment(LEFT);
            return this;
        }
    }

    private static class BodyCellRenderer extends DefaultTableCellRenderer {
        BodyCellRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                setBackground(SEL_BG);
                setForeground(SEL_FG);
            } else {
                setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);
                setForeground(FG);
            }

            setFont(getFont().deriveFont(Font.PLAIN, 12.5f));

            // angka rata kanan, teks rata kiri
            if (value instanceof Number) setHorizontalAlignment(RIGHT);
            else setHorizontalAlignment(LEFT);

            return this;
        }
    }
}
