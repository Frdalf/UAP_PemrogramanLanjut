package ui;

import Model.Donation;
import Model.Distribution;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Laporan/History dengan style "soft blue" seperti form input.
 * Berisi filter + search, tabel history, ringkasan, dan insight.
 */
public class ReportPanel extends JPanel {
    private final MainFrame app;

    // ===== Controls =====
    private final JComboBox<String> cmbFilter = new JComboBox<>(new String[]{"Semua", "Hari ini", "Bulan ini"});
    private final JTextField txtSearch = new JTextField();

    // ===== Summary labels =====
    private final JLabel lblMasukVal = new JLabel("-");
    private final JLabel lblKeluarVal = new JLabel("-");
    private final JLabel lblSaldoVal = new JLabel("-");

    // ===== Insights =====
    private final JTextArea txtInsights = new JTextArea(8, 20);

    // ===== Table =====
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Tanggal", "Tipe", "Pihak", "Jenis", "Kategori", "Nominal", "Barang", "Qty", "Catatan"}, 0
    ) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public ReportPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Root background
        SoftFormUI.FormBackground root = new SoftFormUI.FormBackground();
        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(26, 26, 26, 26));
        add(root, BorderLayout.CENTER);

        JLabel title = new JLabel("Laporan / History");
        title.setFont(new Font("SansSerif", Font.BOLD, 56));
        title.setForeground(new Color(35, 120, 235));
        root.add(title, BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pillRow.setOpaque(false);
        pillRow.add(new SoftFormUI.PillLabel("Laporan/History"));
        outer.add(pillRow, BorderLayout.NORTH);

        SoftFormUI.CardPanel card = new SoftFormUI.CardPanel(26);
        card.setLayout(new BorderLayout(14, 14));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        card.add(buildControls(), BorderLayout.NORTH);
        card.add(buildTableBlock(), BorderLayout.CENTER);
        card.add(buildBottomBlock(), BorderLayout.SOUTH);

        outer.add(card, BorderLayout.CENTER);
        root.add(outer, BorderLayout.CENTER);

        // listeners
        cmbFilter.addActionListener(e -> refresh());
        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> refresh());

        // static config
        txtInsights.setEditable(false);
        txtInsights.setLineWrap(true);
        txtInsights.setWrapStyleWord(true);
        txtInsights.setOpaque(false);
        txtInsights.setBorder(null);
        txtInsights.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtInsights.setForeground(new Color(20, 28, 44));
    }

    private JPanel buildControls() {
        // Responsive layout: di window kecil tombol export jangan ketutup.
        // Kita bikin 2 baris:
        //  - Baris 1: Filter + Search
        //  - Baris 2: Export buttons (align kanan)
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);

        JLabel lblFilter = labelSmall("Filter");
        JLabel lblSearch = labelSmall("Search");

        SoftFormUI.IconField fFilter = new SoftFormUI.IconField(SoftFormUI.IconType.LIST, cmbFilter);
        fFilter.setPreferredSize(new Dimension(210, 46));

        SoftFormUI.IconField fSearch = new SoftFormUI.IconField(SoftFormUI.IconType.SEARCH, txtSearch);
        // Search field fleksibel: akan melebar/mengecil mengikuti lebar window.
        fSearch.setPreferredSize(new Dimension(360, 46));

        // Row 1: grid supaya fSearch bisa shrink/expand
        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.insets = new Insets(0, 0, 0, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        row1.add(lblFilter, g);

        g.gridx = 1;
        g.insets = new Insets(0, 0, 0, 16);
        row1.add(fFilter, g);

        g.gridx = 2;
        g.insets = new Insets(0, 0, 0, 10);
        row1.add(lblSearch, g);

        g.gridx = 3;
        g.weightx = 1;
        g.insets = new Insets(0, 0, 0, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        row1.add(fSearch, g);

        // Row 2: export buttons (selalu terlihat)
        PillButton btnExportHistory = new PillButton("Export History CSV");
        PillButton btnExportSummary = new PillButton("Export Summary CSV")
                .setColors(new Color(90, 98, 112), new Color(110, 118, 132), new Color(76, 84, 98), new Color(72, 80, 94));

        btnExportHistory.addActionListener(e -> exportHistoryCsv());
        btnExportSummary.addActionListener(e -> exportSummaryCsv());

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row2.setOpaque(false);
        row2.add(btnExportHistory);
        row2.add(btnExportSummary);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.weightx = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.WEST;
        controls.add(row1, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.weightx = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.EAST;
        c2.insets = new Insets(10, 0, 0, 0);
        controls.add(row2, c2);

        return controls;
    }

    private JComponent buildTableBlock() {
        JScrollPane sp = new JScrollPane(table);
        SimpleTableTheme.applyBlue(table, sp);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        RoundedPanel wrap = new RoundedPanel(22).setBackgroundColor(new Color(11, 18, 30));
        wrap.setLayout(new BorderLayout());
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildBottomBlock() {
        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 0));
        bottom.setOpaque(false);

        bottom.add(buildSummaryCard());
        bottom.add(buildInsightsCard());
        return bottom;
    }

    private JComponent buildSummaryCard() {
        SoftFormUI.CardPanel summary = new SoftFormUI.CardPanel(20);
        summary.setLayout(new BorderLayout(10, 10));
        summary.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel h = new JLabel("Ringkasan Keuangan");
        h.setFont(new Font("SansSerif", Font.BOLD, 14));
        h.setForeground(new Color(20, 28, 44));
        summary.add(h, BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        addMetricRow(rows, 0, "Uang Masuk", lblMasukVal);
        addMetricRow(rows, 1, "Uang Keluar", lblKeluarVal);
        addMetricRow(rows, 2, "Saldo", lblSaldoVal);
        summary.add(rows, BorderLayout.CENTER);

        return summary;
    }

    private void addMetricRow(JPanel p, int row, String label, JLabel value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(new Color(55, 65, 85));

        value.setFont(new Font("SansSerif", Font.BOLD, 18));
        value.setForeground(new Color(20, 28, 44));

        GridBagConstraints gcL = new GridBagConstraints();
        gcL.gridx = 0;
        gcL.gridy = row;
        gcL.weightx = 0.6;
        gcL.anchor = GridBagConstraints.WEST;
        gcL.insets = new Insets(6, 0, 6, 10);
        p.add(l, gcL);

        GridBagConstraints gcV = new GridBagConstraints();
        gcV.gridx = 1;
        gcV.gridy = row;
        gcV.weightx = 0.4;
        gcV.anchor = GridBagConstraints.EAST;
        gcV.insets = new Insets(6, 0, 6, 0);
        p.add(value, gcV);
    }

    private JComponent buildInsightsCard() {
        SoftFormUI.CardPanel insights = new SoftFormUI.CardPanel(20);
        insights.setLayout(new BorderLayout(10, 10));
        insights.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel h = new JLabel("Insight Cepat");
        h.setFont(new Font("SansSerif", Font.BOLD, 14));
        h.setForeground(new Color(20, 28, 44));
        insights.add(h, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(txtInsights);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        insights.add(sp, BorderLayout.CENTER);
        return insights;
    }

    private JLabel labelSmall(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(new Color(20, 28, 44));
        return l;
    }

    // ===== Public API =====
    public void refresh() {
        // ===== Summary =====
        double masuk = app.totalUangMasuk();
        double keluar = app.totalUangKeluar();
        double saldo = app.saldoUang();
        lblMasukVal.setText(idr(masuk));
        lblKeluarVal.setText(idr(keluar));
        lblSaldoVal.setText(idr(saldo));

        updateInsights();

        // ===== Build history rows =====
        java.util.List<Row> rows = new ArrayList<>();
        for (Donation d : app.getDonations()) rows.add(Row.fromDonation(d));
        for (Distribution d : app.getDistributions()) rows.add(Row.fromDistribution(d));

        // Filter tanggal
        String f = (String) cmbFilter.getSelectedItem();
        LocalDate now = LocalDate.now();
        java.util.List<Row> filtered = new ArrayList<>();
        for (Row r : rows) {
            boolean ok = true;
            if ("Hari ini".equals(f)) ok = r.tanggal.equals(now);
            if ("Bulan ini".equals(f)) ok = r.tanggal.getYear() == now.getYear() && r.tanggal.getMonthValue() == now.getMonthValue();
            if (ok) filtered.add(r);
        }

        // Search
        String q = txtSearch.getText().trim().toLowerCase();
        java.util.List<Row> finalRows = new ArrayList<>();
        for (Row r : filtered) {
            String blob = (r.tanggal + " " + r.tipe + " " + r.pihak + " " + r.jenis + " " + nvl(r.kategori) + " " + nvl(r.barang) + " " + nvl(r.catatan)).toLowerCase();
            if (q.isEmpty() || blob.contains(q)) finalRows.add(r);
        }

        // Sort tanggal desc
        finalRows.sort(Comparator.comparing((Row r) -> r.tanggal).reversed());

        // Render
        model.setRowCount(0);
        for (Row r : finalRows) {
            Object nominalDisp = "UANG".equalsIgnoreCase(r.jenis) ? idr(r.nominal) : "";
            Object qtyDisp = "BARANG".equalsIgnoreCase(r.jenis) ? r.qty : "";
            model.addRow(new Object[]{
                    r.tanggal,
                    r.tipe,
                    r.pihak,
                    r.jenis,
                    nvl(r.kategori),
                    nominalDisp,
                    nvl(r.barang),
                    qtyDisp,
                    nvl(r.catatan)
            });
        }
    }

    private void exportHistoryCsv() {
        try {
            String path = "src/Data/export_history.csv";

            java.util.List<Row> rows = new java.util.ArrayList<>();
            for (Model.Donation d : app.getDonations()) rows.add(Row.fromDonation(d));
            for (Model.Distribution d : app.getDistributions()) rows.add(Row.fromDistribution(d));

            // filter & search mengikuti UI ReportPanel
            String f = (String) cmbFilter.getSelectedItem();
            java.time.LocalDate now = java.time.LocalDate.now();

            java.util.List<Row> filtered = new java.util.ArrayList<>();
            for (Row r : rows) {
                boolean ok = true;
                if ("Hari ini".equals(f)) ok = r.tanggal.equals(now);
                if ("Bulan ini".equals(f)) ok = r.tanggal.getYear() == now.getYear()
                        && r.tanggal.getMonthValue() == now.getMonthValue();
                if (ok) filtered.add(r);
            }

            String q = txtSearch.getText().trim().toLowerCase();
            java.util.List<Row> finalRows = new java.util.ArrayList<>();
            for (Row r : filtered) {
                String blob = (r.tanggal + " " + r.tipe + " " + r.pihak + " " + r.jenis + " " + nvl(r.kategori) + " " + nvl(r.barang) + " " + nvl(r.catatan)).toLowerCase();
                if (q.isEmpty() || blob.contains(q)) finalRows.add(r);
            }

            // sort tanggal desc
            finalRows.sort(java.util.Comparator.comparing((Row r) -> r.tanggal).reversed());

            // write csv
            java.nio.file.Path p = java.nio.file.Paths.get(path);
            if (p.getParent() != null && java.nio.file.Files.notExists(p.getParent())) {
                java.nio.file.Files.createDirectories(p.getParent());
            }

            try (java.io.BufferedWriter bw = java.nio.file.Files.newBufferedWriter(p)) {
                bw.write("tanggal,tipe,pihak,jenis,kategori,nominal,barang,qty,catatan\n");
                for (Row r : finalRows) {
                    bw.write(String.join(",",
                            Util.CsvUtil.safe(r.tanggal.toString()),
                            Util.CsvUtil.safe(r.tipe),
                            Util.CsvUtil.safe(r.pihak),
                            Util.CsvUtil.safe(r.jenis),
                            Util.CsvUtil.safe(nvl(r.kategori)),
                            String.valueOf(r.nominal),
                            Util.CsvUtil.safe(nvl(r.barang)),
                            String.valueOf(r.qty),
                            Util.CsvUtil.safe(nvl(r.catatan))
                    ));
                    bw.write("\n");
                }
            }

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Export berhasil!\nFile: " + path,
                    "Sukses", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Export gagal:\n" + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportSummaryCsv() {
        try {
            String path = "src/Data/export_summary.csv";

            double masuk = app.totalUangMasuk();
            double keluar = app.totalUangKeluar();
            double saldo = app.saldoUang();

            // Top donor uang
            java.util.Map<String, Double> donorTotal = new java.util.HashMap<>();
            for (Model.Donation d : app.getDonations()) {
                if ("UANG".equalsIgnoreCase(d.getJenis())) {
                    donorTotal.put(d.getDonorId(), donorTotal.getOrDefault(d.getDonorId(), 0.0) + d.getNominal());
                }
            }

            java.util.Map<String, String> donorName = new java.util.HashMap<>();
            for (Model.Donor dn : app.getDonors()) donorName.put(dn.getDonorId(), dn.getNama());

            java.util.List<java.util.Map.Entry<String, Double>> top = new java.util.ArrayList<>(donorTotal.entrySet());
            top.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            // Rekap kategori
            java.util.Map<String, Double> donasiPerKategori = new java.util.HashMap<>();
            for (Model.Donation d : app.getDonations()) {
                if ("UANG".equalsIgnoreCase(d.getJenis())) {
                    String k = (d.getKategori() == null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
                    donasiPerKategori.put(k, donasiPerKategori.getOrDefault(k, 0.0) + d.getNominal());
                }
            }

            java.util.Map<String, Double> salurPerKategori = new java.util.HashMap<>();
            for (Model.Distribution d : app.getDistributions()) {
                if ("UANG".equalsIgnoreCase(d.getJenis())) {
                    String k = (d.getKategori() == null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
                    salurPerKategori.put(k, salurPerKategori.getOrDefault(k, 0.0) + d.getNominal());
                }
            }

            java.nio.file.Path p = java.nio.file.Paths.get(path);
            if (p.getParent() != null && java.nio.file.Files.notExists(p.getParent())) {
                java.nio.file.Files.createDirectories(p.getParent());
            }

            try (java.io.BufferedWriter bw = java.nio.file.Files.newBufferedWriter(p)) {
                bw.write("section,key,value\n");
                bw.write("SUMMARY,UangMasuk," + masuk + "\n");
                bw.write("SUMMARY,UangKeluar," + keluar + "\n");
                bw.write("SUMMARY,Saldo," + saldo + "\n");

                bw.write("TOP_DONOR,Rank,DonorId|Nama|Total\n");
                int limit = Math.min(3, top.size());
                for (int i = 0; i < limit; i++) {
                    String id = top.get(i).getKey();
                    String nama = donorName.getOrDefault(id, "(Tidak ditemukan)");
                    double total = top.get(i).getValue();
                    bw.write("TOP_DONOR," + (i + 1) + "," +
                            Util.CsvUtil.safe(id + "|" + nama + "|" + String.format("%.0f", total)) + "\n");
                }

                bw.write("DONASI_KATEGORI,Kategori,Total\n");
                for (var e : donasiPerKategori.entrySet()) {
                    bw.write("DONASI_KATEGORI," + Util.CsvUtil.safe(e.getKey()) + "," + e.getValue() + "\n");
                }

                bw.write("SALUR_KATEGORI,Kategori,Total\n");
                for (var e : salurPerKategori.entrySet()) {
                    bw.write("SALUR_KATEGORI," + Util.CsvUtil.safe(e.getKey()) + "," + e.getValue() + "\n");
                }
            }

            JOptionPane.showMessageDialog(this, "Export summary berhasil!\nFile: " + path,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export summary gagal:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInsights() {
        // ---- Top Donor (berdasarkan total donasi uang) ----
        java.util.Map<String, Double> donorTotal = new java.util.HashMap<>();
        for (Model.Donation d : app.getDonations()) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) {
                donorTotal.put(d.getDonorId(),
                        donorTotal.getOrDefault(d.getDonorId(), 0.0) + d.getNominal());
            }
        }

        // Map donorId -> nama (biar tampil nama)
        java.util.Map<String, String> donorName = new java.util.HashMap<>();
        for (Model.Donor dn : app.getDonors()) {
            donorName.put(dn.getDonorId(), dn.getNama());
        }

        java.util.List<java.util.Map.Entry<String, Double>> top = new java.util.ArrayList<>(donorTotal.entrySet());
        top.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // ---- Rekap kategori donasi uang & penyaluran uang ----
        java.util.Map<String, Double> donasiPerKategori = new java.util.HashMap<>();
        for (Model.Donation d : app.getDonations()) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) {
                String k = (d.getKategori() == null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
                donasiPerKategori.put(k, donasiPerKategori.getOrDefault(k, 0.0) + d.getNominal());
            }
        }

        java.util.Map<String, Double> salurPerKategori = new java.util.HashMap<>();
        for (Model.Distribution d : app.getDistributions()) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) {
                String k = (d.getKategori() == null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
                salurPerKategori.put(k, salurPerKategori.getOrDefault(k, 0.0) + d.getNominal());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("TOP DONOR (Donasi Uang)\n");
        if (top.isEmpty()) {
            sb.append("- Belum ada donasi uang.\n");
        } else {
            int limit = Math.min(3, top.size());
            for (int i = 0; i < limit; i++) {
                String donorId = top.get(i).getKey();
                double total = top.get(i).getValue();
                String nama = donorName.getOrDefault(donorId, "(Tidak ditemukan)");
                sb.append(String.format("%d) %s - %s : %s\n", i + 1, donorId, nama, idr(total)));
            }
        }

        sb.append("\nREKAP DONASI UANG PER KATEGORI\n");
        if (donasiPerKategori.isEmpty()) sb.append("- Kosong\n");
        else sb.append(formatMapSortedIdr(donasiPerKategori));

        sb.append("\nREKAP PENYALURAN UANG PER KATEGORI\n");
        if (salurPerKategori.isEmpty()) sb.append("- Kosong\n");
        else sb.append(formatMapSortedIdr(salurPerKategori));

        txtInsights.setText(sb.toString());
        txtInsights.setCaretPosition(0);
    }

    private String formatMapSortedIdr(java.util.Map<String, Double> map) {
        java.util.List<java.util.Map.Entry<String, Double>> list = new java.util.ArrayList<>(map.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        StringBuilder sb = new StringBuilder();
        for (var e : list) {
            sb.append("- ").append(e.getKey()).append(" : ").append(idr(e.getValue())).append("\n");
        }
        return sb.toString();
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private static String idr(double v) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return "Rp " + nf.format(v);
    }

    private static class Row {
        LocalDate tanggal;
        String tipe;   // DONASI / PENYALURAN
        String pihak;  // donorId / penerima
        String jenis;
        String kategori;
        double nominal;
        String barang;
        int qty;
        String catatan;

        static Row fromDonation(Donation d) {
            Row r = new Row();
            r.tanggal = d.getTanggal();
            r.tipe = "DONASI";
            r.pihak = d.getDonorId();
            r.jenis = d.getJenis();
            r.kategori = d.getKategori();
            r.nominal = d.getNominal();
            r.barang = d.getNamaBarang();
            r.qty = d.getJumlahBarang();
            r.catatan = d.getCatatan();
            return r;
        }

        static Row fromDistribution(Distribution d) {
            Row r = new Row();
            r.tanggal = d.getTanggal();
            r.tipe = "PENYALURAN";
            r.pihak = d.getPenerima();
            r.jenis = d.getJenis();
            r.kategori = d.getKategori();
            r.nominal = d.getNominal();
            r.barang = d.getNamaBarang();
            r.qty = d.getJumlahBarang();
            r.catatan = d.getCatatan();
            return r;
        }
    }
}
