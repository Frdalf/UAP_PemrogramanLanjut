package ui;

import Model.Donation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DonationTab extends JPanel {
    private final MainFrame app;

    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cmbSort = new JComboBox<>(new String[]{
            "Tanggal Terbaru",
            "Nominal Terbesar (UANG)"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tanggal", "DonorId", "Jenis", "Kategori", "Nominal", "Barang", "Qty", "Catatan"}, 0
    ) { public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);
    private List<Donation> view = new ArrayList<>();

    public DonationTab(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new BorderLayout(10,10));
        JLabel title = new JLabel("List Donasi Masuk");
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

        btnAdd.addActionListener(e -> app.openNewDonationForm());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
    }

    public void refreshTable() {
        String q = txtSearch.getText().trim().toLowerCase();
        List<Donation> filtered = new ArrayList<>();
        for (Donation d : app.getDonations()) {
            String blob = (d.getDonasiId()+" "+d.getTanggal()+" "+d.getDonorId()+" "+d.getJenis()+" "+
                    d.getKategori()+" "+d.getNamaBarang()+" "+d.getCatatan()).toLowerCase();
            if (q.isEmpty() || blob.contains(q)) filtered.add(d);
        }

        String sort = (String) cmbSort.getSelectedItem();
        if ("Nominal Terbesar (UANG)".equals(sort)) {
            filtered.sort(Comparator.comparingDouble(Donation::getNominal).reversed());
        } else {
            filtered.sort(Comparator.comparing(Donation::getTanggal).reversed());
        }

        view = filtered;
        model.setRowCount(0);
        for (Donation d : view) {
            model.addRow(new Object[]{
                    d.getDonasiId(),
                    d.getTanggal(),
                    d.getDonorId(),
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
        app.openEditDonationForm(view.get(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih 1 data dulu."); return; }

        Donation selected = view.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus donasi " + selected.getDonasiId() + " (" + selected.getJenis() + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            app.getDonations().remove(selected);
            app.persistDonations();
            app.refreshAll();
        }
    }
}
