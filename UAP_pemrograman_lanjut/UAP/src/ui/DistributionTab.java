package ui;

import Model.Distribution;

import javax.swing.*;
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
    ) { public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);
    private List<Distribution> view = new ArrayList<>();

    public DistributionTab(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new BorderLayout(10,10));
        JLabel title = new JLabel("List Penyaluran");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel filter = new JPanel(new GridLayout(1,0,10,10));
        filter.add(new JLabel("Search:"));
        filter.add(txtSearch);
        filter.add(new JLabel("Sort:"));
        filter.add(cmbSort);

        top.add(title, BorderLayout.WEST);
        top.add(filter, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        actions.add(btnAdd); actions.add(btnEdit); actions.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        txtSearch.getDocument().addDocumentListener((DonorTab.SimpleDocumentListener) e -> refreshTable());
        cmbSort.addActionListener(e -> refreshTable());

        btnAdd.addActionListener(e -> app.openNewDistributionForm());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
    }

    public void refreshTable() {
        String q = txtSearch.getText().trim().toLowerCase();
        List<Distribution> filtered = new ArrayList<>();
        for (Distribution d : app.getDistributions()) {
            String blob = (d.getSalurId()+" "+d.getTanggal()+" "+d.getPenerima()+" "+d.getJenis()+" "+
                    d.getKategori()+" "+d.getNamaBarang()+" "+d.getCatatan()).toLowerCase();
            if (q.isEmpty() || blob.contains(q)) filtered.add(d);
        }

        String sort = (String) cmbSort.getSelectedItem();
        if ("Nominal Terbesar (UANG)".equals(sort)) {
            filtered.sort(Comparator.comparingDouble(Distribution::getNominal).reversed());
        } else {
            filtered.sort(Comparator.comparing(Distribution::getTanggal).reversed());
        }

        view = filtered;
        model.setRowCount(0);
        for (Distribution d : view) {
            model.addRow(new Object[]{
                    d.getSalurId(),
                    d.getTanggal(),
                    d.getPenerima(),
                    d.getJenis(),
                    d.getKategori(),
                    d.getNominal(),
                    d.getNamaBarang(),
                    d.getJumlahBarang(),
                    d.getCatatan()
            });
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih 1 data dulu."); return; }
        app.openEditDistributionForm(view.get(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih 1 data dulu."); return; }

        Distribution selected = view.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus penyaluran " + selected.getSalurId() + " (" + selected.getJenis() + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            app.getDistributions().remove(selected);
            app.persistDistributions();
            app.refreshAll();
        }
    }
}
