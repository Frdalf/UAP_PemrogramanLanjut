package ui;

import Model.Donor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tab donatur yang menampilkan daftar data donor
 * dengan fitur pencarian, pengurutan, dan tombol aksi.
 */

public class DonorTab extends JPanel {
    private final MainFrame app;

    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cmbSort = new JComboBox<>(new String[]{
            "Terbaru (createdAt desc)",
            "Nama (A-Z)"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nama", "Kontak", "Alamat", "Created At"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);
    private List<Donor> view = new ArrayList<>();

    public DonorTab(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setOpaque(false);

        // Kontrol bagian atas (responsif agar tombol aksi tidak tertutup)
        add(buildControls(), BorderLayout.NORTH);

        // Tabel (tema)
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        SimpleTableTheme.applyBlue(table, sp);

        RoundedPanel tableWrap = new RoundedPanel(18)
                .useTableBackground();
        tableWrap.setLayout(new BorderLayout());
        tableWrap.setBorder(new EmptyBorder(12, 12, 12, 12));
        tableWrap.add(sp, BorderLayout.CENTER);
        add(tableWrap, BorderLayout.CENTER);


        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> refreshTable());
        cmbSort.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private JComponent buildControls() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);

        JLabel lblSearch = labelSmall("Search");
        JLabel lblSort = labelSmall("Sort");

        SoftFormUI.IconField fSearch = new SoftFormUI.IconField(SoftFormUI.IconType.SEARCH, txtSearch);
        fSearch.setPreferredSize(new Dimension(360, 46));

        SoftFormUI.IconField fSort = new SoftFormUI.IconField(SoftFormUI.IconType.LIST, cmbSort);
        fSort.setPreferredSize(new Dimension(240, 46));

        PillButton btnAdd = new PillButton("+ Tambah");

        PillButton btnEdit = new PillButton("Edit");
        btnEdit.setColors(
                new Color(90, 100, 120),
                new Color(115, 125, 150),
                new Color(70, 80, 100),
                new Color(55, 60, 75)
        );

        PillButton btnDelete = new PillButton("Hapus");
        btnDelete.setColors(
                new Color(200, 55, 65),
                new Color(225, 70, 80),
                new Color(175, 45, 55),
                new Color(140, 35, 45)
        );

        btnAdd.addActionListener(e -> app.openNewDonorForm());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());


        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 0, 10);

        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        row1.add(lblSearch, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 18);
        row1.add(fSearch, g);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 0, 0, 10);
        row1.add(lblSort, g);

        g.gridx = 3;
        g.weightx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        row1.add(fSort, g);


        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row2.setOpaque(false);
        row2.add(btnAdd);
        row2.add(btnEdit);
        row2.add(btnDelete);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.weightx = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        controls.add(row1, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.weightx = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(10, 0, 0, 0);
        controls.add(row2, c2);

        return controls;
    }

    private JLabel labelSmall(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> l.setForeground(ThemeManager.getTextPrimary()));
        return l;
    }

    public void refreshTable() {
        String q = txtSearch.getText().trim().toLowerCase();

        List<Donor> filtered = new ArrayList<>();
        for (Donor d : app.getDonors()) {
            String id = safe(d.getDonorId());
            String nama = safe(d.getNama());
            String kontak = safe(d.getKontak());
            String alamat = safe(d.getAlamat());

            if (q.isEmpty()
                    || id.toLowerCase().contains(q)
                    || nama.toLowerCase().contains(q)
                    || kontak.toLowerCase().contains(q)
                    || alamat.toLowerCase().contains(q)) {
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
            model.addRow(new Object[]{
                    safe(d.getDonorId()),
                    safe(d.getNama()),
                    safe(d.getKontak()),
                    safe(d.getAlamat()),
                    d.getCreatedAt()
            });
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih 1 data dulu.");
            return;
        }
        app.openEditDonorForm(view.get(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih 1 data dulu.");
            return;
        }

        Donor selected = view.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus donor " + safe(selected.getNama()) + " (" + safe(selected.getDonorId()) + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            app.getDonors().remove(selected);
            app.persistDonors();
            app.refreshAll();
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s;
    }
}
