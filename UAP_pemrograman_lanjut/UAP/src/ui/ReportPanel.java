package ui;

import Model.Donation;
import Model.Distribution;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReportPanel extends JPanel {
    private final MainFrame app;

    private final JLabel lblSummary = new JLabel();

    private final JComboBox<String> cmbFilter = new JComboBox<>(new String[]{"Semua", "Hari ini", "Bulan ini"});
    private final JTextField txtSearch = new JTextField();
    private final JTextArea txtInsights = new JTextArea(6, 20);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Tanggal", "Tipe", "Pihak", "Jenis", "Kategori", "Nominal", "Barang", "Qty", "Catatan"}, 0
    ) { public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    public ReportPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Laporan / History");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel top = new JPanel(new BorderLayout(10,10));
        JPanel filter = new JPanel(new GridLayout(1,0,10,10));
        filter.add(new JLabel("Filter:"));
        filter.add(cmbFilter);
        filter.add(new JLabel("Search:"));
        filter.add(txtSearch);

        JButton btnExport = new JButton("Export CSV");
        filter.add(btnExport);
        btnExport.addActionListener(e -> exportHistoryCsv());

        JButton btnExportSummary = new JButton("Export Summary CSV");
        filter.add(btnExportSummary);
        btnExportSummary.addActionListener(e -> exportSummaryCsv());


        top.add(title, BorderLayout.WEST);
        top.add(filter, BorderLayout.SOUTH);

        lblSummary.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        cmbFilter.addActionListener(e -> refresh());
        txtSearch.getDocument().addDocumentListener((DonorTab.SimpleDocumentListener) e -> refresh());

        txtInsights.setEditable(false);
        txtInsights.setLineWrap(true);
        txtInsights.setWrapStyleWord(true);

        JPanel bottom = new JPanel(new BorderLayout(10,10));
        bottom.add(lblSummary, BorderLayout.NORTH);
        bottom.add(new JScrollPane(txtInsights), BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);

    }

    public void refresh() {
        // summary
        double masuk = app.totalUangMasuk();
        double keluar = app.totalUangKeluar();
        double saldo = app.saldoUang();
        lblSummary.setText("Uang Masuk: " + masuk + " | Uang Keluar: " + keluar + " | Saldo: " + saldo);
        updateInsights();

        // build history
        List<Row> rows = new ArrayList<>();
        for (Donation d : app.getDonations()) {
            rows.add(Row.fromDonation(d));
        }
        for (Distribution d : app.getDistributions()) {
            rows.add(Row.fromDistribution(d));
        }

        // filter tanggal
        String f = (String) cmbFilter.getSelectedItem();
        LocalDate now = LocalDate.now();
        List<Row> filtered = new ArrayList<>();
        for (Row r : rows) {
            boolean ok = true;
            if ("Hari ini".equals(f)) ok = r.tanggal.equals(now);
            if ("Bulan ini".equals(f)) ok = r.tanggal.getYear() == now.getYear() && r.tanggal.getMonthValue() == now.getMonthValue();
            if (ok) filtered.add(r);
        }

        // search
        String q = txtSearch.getText().trim().toLowerCase();
        List<Row> finalRows = new ArrayList<>();
        for (Row r : filtered) {
            String blob = (r.tanggal+" "+r.tipe+" "+r.pihak+" "+r.jenis+" "+r.kategori+" "+r.barang+" "+r.catatan).toLowerCase();
            if (q.isEmpty() || blob.contains(q)) finalRows.add(r);
        }

        // sort tanggal desc
        finalRows.sort(Comparator.comparing((Row r) -> r.tanggal).reversed());

        // render
        model.setRowCount(0);
        for (Row r : finalRows) {
            model.addRow(new Object[]{
                    r.tanggal, r.tipe, r.pihak, r.jenis, r.kategori, r.nominal, r.barang, r.qty, r.catatan
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
                String blob = (r.tanggal+" "+r.tipe+" "+r.pihak+" "+r.jenis+" "+r.kategori+" "+r.barang+" "+r.catatan).toLowerCase();
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
                            Util.CsvUtil.safe(r.kategori),
                            String.valueOf(r.nominal),
                            Util.CsvUtil.safe(r.barang),
                            String.valueOf(r.qty),
                            Util.CsvUtil.safe(r.catatan)
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
            top.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));

            // Rekap kategori
            java.util.Map<String, Double> donasiPerKategori = new java.util.HashMap<>();
            for (Model.Donation d : app.getDonations()) {
                if ("UANG".equalsIgnoreCase(d.getJenis())) {
                    String k = (d.getKategori()==null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
                    donasiPerKategori.put(k, donasiPerKategori.getOrDefault(k, 0.0) + d.getNominal());
                }
            }

            java.util.Map<String, Double> salurPerKategori = new java.util.HashMap<>();
            for (Model.Distribution d : app.getDistributions()) {
                if ("UANG".equalsIgnoreCase(d.getJenis())) {
                    String k = (d.getKategori()==null || d.getKategori().isBlank()) ? "Tanpa Kategori" : d.getKategori();
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
                    bw.write("TOP_DONOR," + (i+1) + "," +
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
                sb.append(String.format("%d) %s - %s : %.0f\n", i+1, donorId, nama, total));
            }
        }

        sb.append("\nREKAP DONASI UANG PER KATEGORI\n");
        if (donasiPerKategori.isEmpty()) sb.append("- Kosong\n");
        else sb.append(formatMapSorted(donasiPerKategori));

        sb.append("\nREKAP PENYALURAN UANG PER KATEGORI\n");
        if (salurPerKategori.isEmpty()) sb.append("- Kosong\n");
        else sb.append(formatMapSorted(salurPerKategori));

        txtInsights.setText(sb.toString());
    }

    private String formatMapSorted(java.util.Map<String, Double> map) {
        java.util.List<java.util.Map.Entry<String, Double>> list = new java.util.ArrayList<>(map.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        StringBuilder sb = new StringBuilder();
        for (var e : list) {
            sb.append("- ").append(e.getKey()).append(" : ").append(String.format("%.0f", e.getValue())).append("\n");
        }
        return sb.toString();
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
