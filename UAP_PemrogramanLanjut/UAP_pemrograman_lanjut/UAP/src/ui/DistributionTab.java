package ui;

import Model.Distribution;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DistributionTab extends JPanel {
    private final MainFrame app;

    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cmbSort = new JComboBox<>(new String[]{
            "Tanggal Terbaru",
            "Nominal Terbesar (UANG)"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tanggal", "Penerima", "Jenis", "Kategori", "Nominal", "Barang", "Qty", "Catatan"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);
    private List<Distribution> view = new ArrayList<>();

    public DistributionTab(MainFrame app) {
        this.app = app;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setOpaque(false);

        // ===== TOP =====
        JLabel title = new JLabel("List Penyaluran");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel filter = new JPanel(new GridBagLayout());
        filter.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 10);
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        filter.add(new JLabel("Search:"), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        filter.add(txtSearch, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        filter.add(new JLabel("Sort:"), gc);

        gc.gridx = 3;
        gc.weightx = 0.35;
        filter.add(cmbSort, gc);

        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(filter, BorderLayout.CENTER);

        // ===== TABLE =====
        JScrollPane sp = new JScrollPane(table);
        SimpleTableTheme.applyBlue(table, sp);

        // IMPORTANT: rata kanan tapi tetap ikut theme (nggak putih)
        installRightAlignedColumns();

        // ===== ACTIONS =====
        PillButton btnAdd = new PillButton("+ Tambah");

        PillButton btnEdit = new PillButton("✎ Edit")
                .setColors(
                        new Color(90, 100, 120),
                        new Color(115, 125, 150),
                        new Color(70, 80, 100),
                        new Color(55, 60, 75)
                );

        PillButton btnDelete = new PillButton("🗑 Hapus")
                .setColors(
                        new Color(200, 55, 65),
                        new Color(225, 70, 80),
                        new Color(175, 45, 55),
                        new Color(140, 35, 45)
                );

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // ===== EVENTS =====
        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> refreshTable());
        cmbSort.addActionListener(e -> refreshTable());

        btnAdd.addActionListener(e -> app.openNewDistributionForm());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        refreshTable();
    }

    /**
     * Biar Nominal (col 5) & Qty (col 7) rata kanan
     * TANPA bikin background putih (ikut zebra dari SimpleTableTheme).
     */
    private void installRightAlignedColumns() {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
            ) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Ambil renderer default theme untuk dapat warna zebra & selection
                Component themed = table.getDefaultRenderer(Object.class)
                        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setBackground(themed.getBackground());
                c.setForeground(themed.getForeground());

                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        };

        try {
            table.getColumnModel().getColumn(5).setCellRenderer(right); // Nominal
            table.getColumnModel().getColumn(7).setCellRenderer(right); // Qty
        } catch (Exception ignored) {}
    }

    public void refreshTable() {
        String q = txtSearch.getText().trim().toLowerCase();

        List<Distribution> filtered = new ArrayList<>();
        for (Distribution d : app.getDistributions()) {
            String blob = (safe(d.getSalurId()) + " " +
                    safeDate(d.getTanggal()) + " " +
                    safe(d.getPenerima()) + " " +
                    safe(d.getJenis()) + " " +
                    safe(d.getKategori()) + " " +
                    safe(d.getNamaBarang()) + " " +
                    safe(d.getCatatan()))
                    .toLowerCase();

            if (q.isEmpty() || blob.contains(q)) filtered.add(d);
        }

        String sort = (String) cmbSort.getSelectedItem();
        if ("Nominal Terbesar (UANG)".equals(sort)) {
            filtered.sort(Comparator.comparingDouble(Distribution::getNominal).reversed()
                    .thenComparing(Distribution::getTanggal, Comparator.reverseOrder()));
        } else {
            filtered.sort(Comparator.comparing(Distribution::getTanggal).reversed());
        }

        view = filtered;
        model.setRowCount(0);

        for (Distribution d : view) {
            model.addRow(new Object[]{
                    safe(d.getSalurId()),
                    d.getTanggal(),
                    safe(d.getPenerima()),
                    safe(d.getJenis()),
                    safe(d.getKategori()),
                    d.getNominal(),
                    safe(d.getNamaBarang()),
                    d.getJumlahBarang(),
                    safe(d.getCatatan())
            });
        }

        // pastikan renderer tetap kepake setelah refresh
        installRightAlignedColumns();
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih 1 data dulu.");
            return;
        }
        app.openEditDistributionForm(view.get(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih 1 data dulu.");
            return;
        }

        Distribution selected = view.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus penyaluran " + safe(selected.getSalurId()) + " (" + safe(selected.getJenis()) + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            app.getDistributions().remove(selected);
            app.persistDistributions();
            app.refreshAll();
        }
    }

    private static String safe(String s) { return (s == null) ? "" : s; }
    private static String safeDate(java.time.LocalDate d) { return (d == null) ? "" : d.toString(); }
}
