package ui;

import Model.Donor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DonorTab extends JPanel {
    private final MainFrame app;

    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cmbSort = new JComboBox<>(new String[]{
            "Terbaru (createdAt desc)",
            "Nama (A-Z)"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nama", "Kontak", "Alamat", "Created At"}, 0
    ) { public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private List<Donor> view = new ArrayList<>();

    public DonorTab(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new BorderLayout(10,10));
        JLabel title = new JLabel("List Donatur");
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

        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> refreshTable());
        cmbSort.addActionListener(e -> refreshTable());

        btnAdd.addActionListener(e -> app.openNewDonorForm());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
    }

    public void refreshTable() {
        String q = txtSearch.getText().trim().toLowerCase();
        List<Donor> filtered = new ArrayList<>();
        for (Donor d : app.getDonors()) {
            if (q.isEmpty()
                    || d.getDonorId().toLowerCase().contains(q)
                    || d.getNama().toLowerCase().contains(q)
                    || d.getKontak().toLowerCase().contains(q)
                    || d.getAlamat().toLowerCase().contains(q)) {
                filtered.add(d);
            }
        }

        String sort = (String) cmbSort.getSelectedItem();
        if ("Nama (A-Z)".equals(sort)) {
            filtered.sort(Comparator.comparing(Donor::getNama, String.CASE_INSENSITIVE_ORDER));
        } else {
            filtered.sort(Comparator.comparing(Donor::getCreatedAt).reversed());
        }

        view = filtered;
        model.setRowCount(0);
        for (Donor d : view) {
            model.addRow(new Object[]{ d.getDonorId(), d.getNama(), d.getKontak(), d.getAlamat(), d.getCreatedAt() });
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih 1 data dulu."); return; }
        app.openEditDonorForm(view.get(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih 1 data dulu."); return; }

        Donor selected = view.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus donor " + selected.getNama() + " (" + selected.getDonorId() + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            app.getDonors().remove(selected);
            app.persistDonors();
            app.refreshAll();
        }
    }

    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
    }
}
