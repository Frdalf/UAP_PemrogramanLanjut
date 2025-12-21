package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * Halaman List (Donatur/Donasi Masuk/Penyaluran) dengan style "soft blue"
 * yang konsisten dengan FormPanel dan ReportPanel.
 */
public class ListPanel extends JPanel {
    private final MainFrame app;

    private final DonorTab donorTab;
    private final DonationTab donationTab;
    private final DistributionTab distributionTab;

    public ListPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Root background
        SoftFormUI.FormBackground root = new SoftFormUI.FormBackground();
        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(26, 26, 26, 26));
        add(root, BorderLayout.CENTER);

        JLabel title = new JLabel("List Donatur");
        title.setFont(new Font("SansSerif", Font.BOLD, 56));
        title.setForeground(new Color(35, 120, 235));
        root.add(title, BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        SoftFormUI.CardPanel card = new SoftFormUI.CardPanel(26);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        // Supaya tab tidak menampilkan focus rectangle (dotted) bawaan OS/LAF
        tabs.setFocusable(false);
        tabs.setUI(new SoftTabsUI());

        donorTab = new DonorTab(app);
        donationTab = new DonationTab(app);
        distributionTab = new DistributionTab(app);

        tabs.addTab("Donatur", donorTab);
        tabs.addTab("Donasi Masuk", donationTab);
        tabs.addTab("Penyaluran", distributionTab);

        // Auto refresh saat pindah tab (biar data selalu update)
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) donorTab.refreshTable();
            else if (idx == 1) donationTab.refreshTable();
            else if (idx == 2) distributionTab.refreshTable();

            // update judul besar sesuai tab
            String t = switch (idx) {
                case 1 -> "List Donasi Masuk";
                case 2 -> "List Penyaluran";
                default -> "List Donatur";
            };
            title.setText(t);
        });

        card.add(tabs, BorderLayout.CENTER);
        outer.add(card, BorderLayout.CENTER);
        root.add(outer, BorderLayout.CENTER);
    }

    public void refreshAllTables() {
        donorTab.refreshTable();
        donationTab.refreshTable();
        distributionTab.refreshTable();
    }

    /**
     * UI tab yang lebih "pills" dan soft, agar selaras dengan tema form.
     */
    private static class SoftTabsUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(8, 14, 8, 14);
            selectedTabPadInsets = new Insets(2, 2, 2, 2);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // transparan (nggak gambar bar default)
            g2.dispose();
            super.paintTabArea(g, tabPlacement, selectedIndex);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 22;
            int px = x + 2;
            int py = y + 2;
            int pw = w - 4;
            int ph = h - 4;

            if (isSelected) {
                g2.setColor(SoftFormUI.PILL_BLUE);
                g2.fillRoundRect(px, py, pw, ph, arc, arc);
                g2.setColor(new Color(25, 90, 170));
                g2.drawRoundRect(px, py, pw, ph, arc, arc);
            } else {
                g2.setColor(new Color(235, 241, 249));
                g2.fillRoundRect(px, py, pw, ph, arc, arc);
                g2.setColor(new Color(215, 225, 238));
                g2.drawRoundRect(px, py, pw, ph, arc, arc);
            }
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            // Hilangkan border default BasicTabbedPaneUI (yang bikin tab kelihatan "kotak"/ada garis pemisah)
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                           int tabIndex, Rectangle iconRect, Rectangle textRect,
                                           boolean isSelected) {
            // Hilangkan focus indicator (dotted rectangle) pada tab
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            g.setFont(font);
            g.setColor(isSelected ? Color.WHITE : new Color(25, 35, 52));
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // no border
        }
    }
}
