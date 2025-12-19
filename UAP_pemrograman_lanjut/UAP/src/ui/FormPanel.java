package ui;

import Model.Donor;
import Util.IdGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class FormPanel extends JPanel {
    private final MainFrame app;

    private boolean editMode = false;
    private Donor editing = null;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JTextField txtKontak = new JTextField();
    private final JTextArea txtAlamat = new JTextArea(4, 20);

    private final JLabel title = new JLabel();

    public FormPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        txtId.setEditable(false);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        form.add(new JLabel("Donor ID"));
        form.add(txtId);

        form.add(new JLabel("Nama *"));
        form.add(txtNama);

        form.add(new JLabel("Kontak"));
        form.add(txtKontak);

        form.add(new JLabel("Alamat"));
        form.add(new JScrollPane(txtAlamat));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");

        actions.add(btnSave);
        actions.add(btnCancel);

        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> {
            clear();
            app.showScreen(MainFrame.SCREEN_LIST);
        });
    }

    public void setModeCreate() {
        editMode = false;
        editing = null;
        title.setText("Tambah Donatur");
        clear();
        txtId.setText(IdGenerator.nextDonorId(app.getDonors()));
    }

    public void setModeEdit(Donor donor) {
        editMode = true;
        editing = donor;
        title.setText("Edit Donatur");
        txtId.setText(donor.getDonorId());
        txtNama.setText(donor.getNama());
        txtKontak.setText(donor.getKontak());
        txtAlamat.setText(donor.getAlamat());
    }

    private void onSave() {
        try {
            String id = txtId.getText().trim();
            String nama = txtNama.getText().trim();
            String kontak = txtKontak.getText().trim();
            String alamat = txtAlamat.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!editMode) {
                Donor d = new Donor(id, nama, kontak, alamat, LocalDate.now());
                app.getDonors().add(d);
            } else {
                editing.setNama(nama);
                editing.setKontak(kontak);
                editing.setAlamat(alamat);
            }

            app.persistDonors();
            app.refreshAll();
            clear();
            app.showScreen(MainFrame.SCREEN_LIST);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clear() {
        txtNama.setText("");
        txtKontak.setText("");
        txtAlamat.setText("");
    }
}
