package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.List;


public class DashboardPanel extends JPanel {
    private final MainFrame app;

    private final JLabel lblTitle = new JLabel("Dashboard");

    private final StatCard cardDonor;
    private final StatCard cardMasuk;
    private final StatCard cardSaldo;

    private final SimpleLineChart chart = new SimpleLineChart();

    private final NumberFormat comma = NumberFormat.getNumberInstance(Locale.US);

    public DashboardPanel(MainFrame app) {
        this.app = app;

        setLayout(new BorderLayout());
        setOpaque(false);


        DashboardBackground root = new DashboardBackground();
        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(26, 26, 26, 26));
        add(root, BorderLayout.CENTER);


        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 56));
        lblTitle.setForeground(ThemeManager.getTitleColor());
        ThemeManager.addThemeChangeListener(() -> lblTitle.setForeground(ThemeManager.getTitleColor()));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(lblTitle, BorderLayout.WEST);


        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 18, 18));
        cardsRow.setOpaque(false);


        cardDonor = new StatCard("Total Donatur:", "0", "👤", ThemeManager.getStatCardDonor());
        cardMasuk = new StatCard("Uang Masuk:", "0 IDR", "💰", ThemeManager.getStatCardMasuk());
        cardSaldo = new StatCard("Saldo:", "0 IDR", "💳", ThemeManager.getStatCardSaldo());

        cardsRow.add(cardDonor);
        cardsRow.add(cardMasuk);
        cardsRow.add(cardSaldo);


        JPanel chartWrap = new RoundedPanel(22, ThemeManager.getCardBackground());
        chartWrap.setLayout(new BorderLayout(10, 10));
        chartWrap.setBorder(new EmptyBorder(16, 16, 16, 16));
        chartWrap.setOpaque(false);

        JLabel chartInfo = new JLabel("Total Donatur: 0 | Uang Masuk: 0 | Uang Keluar: 0 | Saldo: 0");
        chartInfo.setForeground(ThemeManager.getTextMuted());
        chartInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        chartWrap.add(chartInfo, BorderLayout.NORTH);
        chartWrap.add(chart, BorderLayout.CENTER);


        JPanel topArea = new JPanel();
        topArea.setOpaque(false);
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));

        topArea.add(header);
        topArea.add(Box.createVerticalStrut(16));
        topArea.add(cardsRow);
        topArea.add(Box.createVerticalStrut(18));

        root.add(topArea, BorderLayout.NORTH);
        root.add(chartWrap, BorderLayout.WEST); // kiri bawah seperti gambar (area kanan kosong biar clean)

        // simpan label chartInfo agar ikut update saat refresh
        this.chart.setInfoLabel(chartInfo);

        refresh();
    }

    public void refresh() {
        int totalDonor = app.getDonors().size();
        double uangMasuk = app.totalUangMasuk();
        double uangKeluar = app.totalUangKeluar();
        double saldo = app.saldoUang();

        cardDonor.setValue(String.valueOf(totalDonor));
        cardMasuk.setValue(formatIDR(uangMasuk));
        cardSaldo.setValue(formatIDR(saldo));

        // Update colors sesuai dengan tema
        lblTitle.setForeground(ThemeManager.getTitleColor());
        cardDonor.updateTheme(ThemeManager.getStatCardDonor());
        cardMasuk.updateTheme(ThemeManager.getStatCardMasuk());
        cardSaldo.updateTheme(ThemeManager.getStatCardSaldo());

        chart.setInfoText("Total Donatur: " + totalDonor +
                " | Uang Masuk: " + formatPlain(uangMasuk) +
                " | Uang Keluar: " + formatPlain(uangKeluar) +
                " | Saldo: " + formatPlain(saldo));


        Map<YearMonth, Double> masukPerBulan = app.getMonthlyUangMasukLast6Months();
        chart.setSeries(buildSeriesLast6Months(masukPerBulan));

        repaint();
        revalidate();
    }

    private String formatIDR(double v) {
        return comma.format(Math.round(v)) + " IDR";
    }

    private String formatPlain(double v) {
        return comma.format(Math.round(v));
    }

    private List<Double> buildSeriesLast6Months(Map<YearMonth, Double> map) {
        List<Double> series = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            series.add(map.getOrDefault(ym, 0.0));
        }
        return series;
    }

    /* ===================== UI Components ===================== */

    // Background lembut (gradient + blob)
    private static class DashboardBackground extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Gradient based on theme
            GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getBackgroundPrimary(),
                    w, h, ThemeManager.getBackgroundSecondary());
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // "Blob" lembut
            g2.setColor(ThemeManager.getBlobLight());
            g2.fillOval((int)(w * 0.55), (int)(h * 0.05), (int)(w * 0.6), (int)(w * 0.6));

            g2.setColor(ThemeManager.getBlobAccent());
            g2.fillOval((int)(w * 0.40), (int)(h * 0.50), (int)(w * 0.55), (int)(w * 0.55));

            g2.dispose();
        }
    }

    // Panel rounded putih + shadow halus
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        public void updateBg(Color newBg) {
            this.bg = newBg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // shadow
            g2.setColor(new Color(0, 0, 0, ThemeManager.isDarkMode() ? 30 : 18));
            g2.fillRoundRect(4, 6, w - 8, h - 10, radius, radius);

            // card - use theme background
            g2.setColor(ThemeManager.getCardBackground());
            g2.fillRoundRect(0, 0, w - 8, h - 10, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Card statistik (ikon + label + value)
    private static class StatCard extends RoundedPanel {
        private final JLabel lblTitle = new JLabel();
        private final JLabel lblValue = new JLabel();
        private final IconPill iconPill;

        public StatCard(String title, String value, String emojiIcon, Color iconBg) {
            super(22, ThemeManager.getCardBackground());
            setLayout(new BorderLayout(12, 12));
            setBorder(new EmptyBorder(14, 14, 14, 14));

            lblTitle.setText(title);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblTitle.setForeground(ThemeManager.getTextSecondary());

            lblValue.setText(value);
            lblValue.setFont(new Font("SansSerif", Font.BOLD, 28));
            lblValue.setForeground(ThemeManager.getTextPrimary());

            // Icon box
            JPanel iconBox = new JPanel(new BorderLayout());
            iconBox.setOpaque(false);
            iconPill = new IconPill(emojiIcon, iconBg);
            iconBox.add(iconPill, BorderLayout.CENTER);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(lblTitle);
            text.add(Box.createVerticalStrut(4));
            text.add(lblValue);

            add(iconBox, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }

        public void updateTheme(Color iconBg) {
            lblTitle.setForeground(ThemeManager.getTextSecondary());
            lblValue.setForeground(ThemeManager.getTextPrimary());
            iconPill.updateBg(iconBg);
            repaint();
        }
    }

    // Kotak ikon rounded
    private static class IconPill extends JPanel {
        private final String emoji;
        private Color bg;

        public IconPill(String emoji, Color bg) {
            this.emoji = emoji;
            this.bg = bg;
            setPreferredSize(new Dimension(56, 56));
            setOpaque(false);
        }

        public void updateBg(Color newBg) {
            this.bg = newBg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, ThemeManager.isDarkMode() ? 30 : 18));
            g2.fillRoundRect(3, 5, w - 6, h - 8, 16, 16);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 6, h - 8, 16, 16);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 24));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(emoji);
            int th = fm.getAscent();

            g2.setColor(Color.WHITE);
            g2.drawString(emoji, (w - 6 - tw) / 2, (h - 8 + th) / 2 - 2);

            g2.dispose();
            super.paintComponent(g);
        }
    }


    private static class SimpleLineChart extends JPanel {
        private List<Double> series = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        private JLabel infoLabel;

        public SimpleLineChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(520, 240));
        }

        public void setSeries(List<Double> series) {
            this.series = series;
            repaint();
        }

        public void setInfoLabel(JLabel infoLabel) {
            this.infoLabel = infoLabel;
        }

        public void setInfoText(String text) {
            if (infoLabel != null) {
                infoLabel.setText(text);
                infoLabel.setForeground(ThemeManager.getTextMuted());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padL = 36, padR = 18, padT = 18, padB = 30;
            int cw = w - padL - padR;
            int ch = h - padT - padB;

            // grid
            g2.setColor(ThemeManager.getChartGrid());
            for (int i = 0; i <= 5; i++) {
                int y = padT + (ch * i / 5);
                g2.drawLine(padL, y, padL + cw, y);
            }

            double max = 1;
            for (double v : series) max = Math.max(max, v);

            // line
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ThemeManager.getChartLine());

            int n = series.size();
            int prevX = -1, prevY = -1;

            for (int i = 0; i < n; i++) {
                double v = series.get(i);
                int x = padL + (int) (cw * (i / (double) (n - 1)));
                int y = padT + (int) (ch - (v / max) * ch);

                if (prevX != -1) g2.drawLine(prevX, prevY, x, y);

                // titik
                g2.setColor(ThemeManager.getChartPoint());
                g2.fillOval(x - 6, y - 6, 12, 12);
                g2.setColor(ThemeManager.getChartLine());
                g2.fillOval(x - 4, y - 4, 8, 8);

                prevX = x;
                prevY = y;
            }

            // axis label bawah (6 bulan)
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(ThemeManager.getTextMuted());
            String[] months = {"M1", "M2", "M3", "M4", "M5", "M6"};
            for (int i = 0; i < months.length; i++) {
                int x = padL + (int) (cw * (i / (double) (months.length - 1)));
                g2.drawString(months[i], x - 10, padT + ch + 22);
            }

            g2.dispose();
        }
    }
}
