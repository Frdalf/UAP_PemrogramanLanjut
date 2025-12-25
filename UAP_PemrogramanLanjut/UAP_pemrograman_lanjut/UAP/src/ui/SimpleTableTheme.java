package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Tema tabel yang mendukung mode gelap dan terang
 * melalui ThemeManager.
 */
public final class SimpleTableTheme {

    private SimpleTableTheme() {}

    private static Color getBaseBg() {
        return ThemeManager.isDarkMode() ? new Color(18, 20, 28) : new Color(245, 248, 252);
    }
    
    private static Color getRowOdd() {
        return ThemeManager.isDarkMode() ? new Color(25, 28, 38) : new Color(255, 255, 255);
    }
    
    private static Color getRowEven() {
        return ThemeManager.isDarkMode() ? new Color(30, 34, 46) : new Color(248, 250, 255);
    }
    
    private static Color getFg() {
        return ThemeManager.isDarkMode() ? new Color(235, 241, 255) : new Color(30, 40, 60);
    }
    
    private static Color getSelBg() {
        return ThemeManager.isDarkMode() ? new Color(55, 100, 160) : new Color(40, 125, 235);
    }
    
    private static Color getSelFg() {
        return Color.WHITE;
    }
    
    private static Color getHeaderBg() {
        return ThemeManager.isDarkMode() ? new Color(20, 24, 35) : new Color(235, 240, 248);
    }
    
    private static Color getHeaderFg() {
        return ThemeManager.isDarkMode() ? new Color(220, 233, 255) : new Color(40, 50, 70);
    }

    public static void applyBlue(JTable table, JScrollPane sp) {
        applyBlueInternal(table);

        if (sp != null) {
            // ✅ padding untuk header + body (bukan viewportBorder)
            sp.setBorder(new EmptyBorder(10, 10, 10, 10));
            sp.setOpaque(false);

            sp.getViewport().setOpaque(true);
            sp.getViewport().setBackground(getBaseBg());
            
            // listener tema agar warna tabel ikut berubah saat tema diganti
            ThemeManager.addThemeChangeListener(() -> {
                table.setBackground(getBaseBg());
                sp.getViewport().setBackground(getBaseBg());
                table.repaint();
                table.getTableHeader().repaint();
            });
        }
    }

    public static void applyBlue(JTable table) {
        applyBlue(table, null);
    }


    private static void applyBlueInternal(JTable table) {
        table.setRowHeight(38);

        table.setIntercellSpacing(new Dimension(0, 6)); // jarak antar row
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //gap antar row pakai background table
        table.setOpaque(true);
        table.setBackground(getBaseBg());
        table.setForeground(getFg());

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
            setBackground(getHeaderBg());
            setForeground(getHeaderFg());
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
                setBackground(getSelBg());
                setForeground(getSelFg());
            } else {
                setBackground((row % 2 == 0) ? getRowEven() : getRowOdd());
                setForeground(getFg());
            }

            setFont(getFont().deriveFont(Font.PLAIN, 12.5f));

            // angka rata kanan, teks rata kiri
            if (value instanceof Number) setHorizontalAlignment(RIGHT);
            else setHorizontalAlignment(LEFT);

            return this;
        }
    }
}
