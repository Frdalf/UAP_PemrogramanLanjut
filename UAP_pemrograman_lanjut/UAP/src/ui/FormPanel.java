package ui;

import Model.Donor;
import Model.Donation;
import Model.Distribution;
import Util.IdGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class FormPanel extends JPanel {
    private final MainFrame app;

    private boolean editMode = false;
    private Donor editing = null;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JTextField txtKontak = new JTextField();
    private final JTextArea txtAlamat = new JTextArea(4, 20);

    private final JLabel title = new JLabel();

    // tabs harus jadi field supaya bisa kita ubah-ubah
    private final JTabbedPane tabs = new JTabbedPane();

    // simpan panel tiap tab, biar bisa dipasang/lepas
    private JPanel donorTabPanel;
    private JPanel donationTabPanel;
    private JPanel distributionTabPanel;

    private enum TabMode { DONOR, DONATION, DISTRIBUTION, ALL }

    private boolean editModeDistribution = false;
    private Distribution editingDistribution = null;

    private final JTextField txtSalurId = new JTextField();
    private final JTextField txtSalurTanggal = new JTextField(); // yyyy-MM-dd
    private final JTextField txtPenerima = new JTextField();
    private final JComboBox<String> cmbSalurJenis = new JComboBox<>(new String[]{"UANG", "BARANG"});
    private final JTextField txtSalurKategori = new JTextField();
    private final JTextField txtSalurNominal = new JTextField();
    private final JTextField txtSalurNamaBarang = new JTextField();
    private final JSpinner spSalurJumlahBarang = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextArea txtSalurCatatan = new JTextArea(3, 20);


    public FormPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        // build panel tab sekali saja
        donorTabPanel = buildDonorForm();
        donationTabPanel = buildDonationForm();
        distributionTabPanel = buildDistributionForm();

        add(title, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // default
        setModeCreate(); // ini otomatis akan show hanya tab Donatur
    }

    private void applyTabMode(TabMode mode) {
        tabs.removeAll();

        if (mode == TabMode.DONOR || mode == TabMode.ALL) {
            tabs.addTab("Donatur", donorTabPanel);
        }
        if (mode == TabMode.DONATION || mode == TabMode.ALL) {
            tabs.addTab("Donasi Masuk", donationTabPanel);
        }
        if (mode == TabMode.DISTRIBUTION || mode == TabMode.ALL) {
            tabs.addTab("Penyaluran", distributionTabPanel);
        }

        // karena kalau mode single tab, index selalu 0
        if (tabs.getTabCount() > 0) tabs.setSelectedIndex(0);

        tabs.revalidate();
        tabs.repaint();
    }

    private JPanel buildDonorForm() {
        JPanel wrap = new JPanel(new BorderLayout(10,10));

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
        JButton btnSave = new JButton("Simpan Donatur");
        JButton btnCancel = new JButton("Batal");
        actions.add(btnSave);
        actions.add(btnCancel);

        btnSave.addActionListener(e -> onSave()); // method donor kamu
        btnCancel.addActionListener(e -> {
            clear();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildDonationForm() {
        JPanel wrap = new JPanel(new BorderLayout(10,10));
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        txtDonasiId.setEditable(false);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);

        form.add(new JLabel("Donasi ID"));
        form.add(txtDonasiId);

        form.add(new JLabel("Tanggal (yyyy-MM-dd) *"));
        form.add(txtTanggal);

        form.add(new JLabel("Donor *"));
        form.add(cmbDonor);

        form.add(new JLabel("Jenis *"));
        form.add(cmbJenis);

        form.add(new JLabel("Kategori"));
        form.add(txtKategori);

        form.add(new JLabel("Nominal (UANG)"));
        form.add(txtNominal);

        form.add(new JLabel("Nama Barang (BARANG)"));
        form.add(txtNamaBarang);

        form.add(new JLabel("Jumlah Barang (BARANG)"));
        form.add(spJumlahBarang);

        form.add(new JLabel("Catatan"));
        form.add(new JScrollPane(txtCatatan));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnSave = new JButton("Simpan Donasi");
        JButton btnCancel = new JButton("Batal");
        actions.add(btnSave);
        actions.add(btnCancel);

        cmbJenis.addActionListener(e -> toggleDonationFields());

        btnSave.addActionListener(e -> onSaveDonation());
        btnCancel.addActionListener(e -> {
            clearDonation();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildDistributionForm() {
        JPanel wrap = new JPanel(new BorderLayout(10,10));
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        txtSalurId.setEditable(false);
        txtSalurCatatan.setLineWrap(true);
        txtSalurCatatan.setWrapStyleWord(true);

        form.add(new JLabel("Salur ID"));
        form.add(txtSalurId);

        form.add(new JLabel("Tanggal (yyyy-MM-dd) *"));
        form.add(txtSalurTanggal);

        form.add(new JLabel("Penerima *"));
        form.add(txtPenerima);

        form.add(new JLabel("Jenis *"));
        form.add(cmbSalurJenis);

        form.add(new JLabel("Kategori"));
        form.add(txtSalurKategori);

        form.add(new JLabel("Nominal (UANG)"));
        form.add(txtSalurNominal);

        form.add(new JLabel("Nama Barang (BARANG)"));
        form.add(txtSalurNamaBarang);

        form.add(new JLabel("Jumlah Barang (BARANG)"));
        form.add(spSalurJumlahBarang);

        form.add(new JLabel("Catatan"));
        form.add(new JScrollPane(txtSalurCatatan));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnSave = new JButton("Simpan Penyaluran");
        JButton btnCancel = new JButton("Batal");
        actions.add(btnSave);
        actions.add(btnCancel);

        cmbSalurJenis.addActionListener(e -> toggleDistributionFields());

        btnSave.addActionListener(e -> onSaveDistribution());
        btnCancel.addActionListener(e -> {
            clearDistribution();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);

        toggleDistributionFields();
        return wrap;
    }

    private void toggleDistributionFields() {
        String jenis = (String) cmbSalurJenis.getSelectedItem();
        boolean uang = "UANG".equalsIgnoreCase(jenis);

        txtSalurNominal.setEnabled(uang);

        txtSalurNamaBarang.setEnabled(!uang);
        spSalurJumlahBarang.setEnabled(!uang);
    }


    private void toggleDonationFields() {
        String jenis = (String) cmbJenis.getSelectedItem();
        boolean uang = "UANG".equalsIgnoreCase(jenis);

        txtNominal.setEnabled(uang);

        txtNamaBarang.setEnabled(!uang);
        spJumlahBarang.setEnabled(!uang);
    }

    public void setModeCreateDonation() {
        applyTabMode(TabMode.DONATION);

        editModeDonation = false;
        editingDonation = null;
        title.setText("Tambah Donasi Masuk");

        // isi combo donor
        cmbDonor.removeAllItems();
        for (Donor d : app.getDonors()) {
            cmbDonor.addItem(d.getDonorId() + " - " + d.getNama());
        }

        txtDonasiId.setText(Util.IdGenerator.nextDonationId(app.getDonations()));
        txtTanggal.setText(java.time.LocalDate.now().toString());
        cmbJenis.setSelectedItem("UANG");
        txtKategori.setText("");
        txtNominal.setText("");
        txtNamaBarang.setText("");
        spJumlahBarang.setValue(1);
        txtCatatan.setText("");

        toggleDonationFields();
    }

    public void setModeEditDonation(Donation donation) {
        editModeDonation = true;
        editingDonation = donation;
        title.setText("Edit Donasi Masuk");

        cmbDonor.removeAllItems();
        int idxSelect = 0;
        int idx = 0;
        for (Donor d : app.getDonors()) {
            String item = d.getDonorId() + " - " + d.getNama();
            cmbDonor.addItem(item);
            if (d.getDonorId().equals(donation.getDonorId())) idxSelect = idx;
            idx++;
        }
        cmbDonor.setSelectedIndex(Math.max(0, idxSelect));

        txtDonasiId.setText(donation.getDonasiId());
        txtTanggal.setText(donation.getTanggal().toString());
        cmbJenis.setSelectedItem(donation.getJenis());
        txtKategori.setText(donation.getKategori());
        txtNominal.setText(String.valueOf(donation.getNominal()));
        txtNamaBarang.setText(donation.getNamaBarang());
        spJumlahBarang.setValue(Math.max(1, donation.getJumlahBarang()));
        txtCatatan.setText(donation.getCatatan());

        toggleDonationFields();
    }

    private void onSaveDonation() {
        try {
            String id = txtDonasiId.getText().trim();
            String tanggalStr = txtTanggal.getText().trim();
            String donorPick = (String) cmbDonor.getSelectedItem();
            String jenis = (String) cmbJenis.getSelectedItem();
            String kategori = txtKategori.getText().trim();

            if (tanggalStr.isEmpty() || donorPick == null || donorPick.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal dan Donor wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.time.LocalDate tanggal;
            try {
                tanggal = java.time.LocalDate.parse(tanggalStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String donorId = donorPick.split(" - ")[0].trim();

            double nominal = 0;
            String namaBarang = "";
            int jumlahBarang = 0;
            String catatan = txtCatatan.getText().trim();

            if ("UANG".equalsIgnoreCase(jenis)) {
                String n = txtNominal.getText().trim();
                if (n.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nominal wajib diisi untuk donasi UANG!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    nominal = Double.parseDouble(n);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Nominal harus angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (nominal <= 0) {
                    JOptionPane.showMessageDialog(this, "Nominal harus > 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                namaBarang = txtNamaBarang.getText().trim();
                jumlahBarang = (int) spJumlahBarang.getValue();
                if (namaBarang.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama barang wajib diisi untuk donasi BARANG!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (jumlahBarang <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang harus > 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (!editModeDonation) {
                Model.Donation d = new Model.Donation(id, tanggal, donorId, jenis, kategori, nominal, namaBarang, jumlahBarang, catatan);
                app.getDonations().add(d);
            } else {
                editingDonation.setTanggal(tanggal);
                editingDonation.setDonorId(donorId);
                editingDonation.setJenis(jenis);
                editingDonation.setKategori(kategori);
                editingDonation.setNominal(nominal);
                editingDonation.setNamaBarang(namaBarang);
                editingDonation.setJumlahBarang(jumlahBarang);
                editingDonation.setCatatan(catatan);
            }

            app.persistDonations();
            app.refreshAll();
            clearDonation();
            app.showScreen(MainFrame.SCREEN_LIST);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDonation() {
        txtDonasiId.setText("");
        txtTanggal.setText("");
        txtKategori.setText("");
        txtNominal.setText("");
        txtNamaBarang.setText("");
        spJumlahBarang.setValue(1);
        txtCatatan.setText("");
    }


    public void setModeCreate() {
        applyTabMode(TabMode.DONOR);

        editMode = false;
        editing = null;
        title.setText("Tambah Donatur");
        clear();
        txtId.setText(IdGenerator.nextDonorId(app.getDonors()));
    }

    public void setModeEdit(Donor donor) {
        applyTabMode(TabMode.DONOR);

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

    private boolean editModeDonation = false;
    private Donation editingDonation = null;

    // komponen form donasi
    private final JTextField txtDonasiId = new JTextField();
    private final JTextField txtTanggal = new JTextField(); // yyyy-MM-dd
    private final JComboBox<String> cmbDonor = new JComboBox<>();
    private final JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"UANG", "BARANG"});
    private final JTextField txtKategori = new JTextField();
    private final JTextField txtNominal = new JTextField();
    private final JTextField txtNamaBarang = new JTextField();
    private final JSpinner spJumlahBarang = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextArea txtCatatan = new JTextArea(3, 20);

    public void setModeCreateDistribution() {
        applyTabMode(TabMode.DISTRIBUTION);

        editModeDistribution = false;
        editingDistribution = null;
        title.setText("Tambah Penyaluran");

        txtSalurId.setText(Util.IdGenerator.nextDistributionId(app.getDistributions()));
        txtSalurTanggal.setText(java.time.LocalDate.now().toString());
        txtPenerima.setText("");
        cmbSalurJenis.setSelectedItem("UANG");
        txtSalurKategori.setText("");
        txtSalurNominal.setText("");
        txtSalurNamaBarang.setText("");
        spSalurJumlahBarang.setValue(1);
        txtSalurCatatan.setText("");

        toggleDistributionFields();
    }

    public void setModeEditDistribution(Distribution d) {
        applyTabMode(TabMode.DISTRIBUTION);

        editModeDistribution = true;
        editingDistribution = d;
        title.setText("Edit Penyaluran");

        txtSalurId.setText(d.getSalurId());
        txtSalurTanggal.setText(d.getTanggal().toString());
        txtPenerima.setText(d.getPenerima());
        cmbSalurJenis.setSelectedItem(d.getJenis());
        txtSalurKategori.setText(d.getKategori());
        txtSalurNominal.setText(String.valueOf(d.getNominal()));
        txtSalurNamaBarang.setText(d.getNamaBarang());
        spSalurJumlahBarang.setValue(Math.max(1, d.getJumlahBarang()));
        txtSalurCatatan.setText(d.getCatatan());

        toggleDistributionFields();
    }

    private void onSaveDistribution() {
        try {
            String id = txtSalurId.getText().trim();
            String tanggalStr = txtSalurTanggal.getText().trim();
            String penerima = txtPenerima.getText().trim();
            String jenis = (String) cmbSalurJenis.getSelectedItem();
            String kategori = txtSalurKategori.getText().trim();
            String catatan = txtSalurCatatan.getText().trim();

            if (tanggalStr.isEmpty() || penerima.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal dan Penerima wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.time.LocalDate tanggal;
            try {
                tanggal = java.time.LocalDate.parse(tanggalStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double nominal = 0;
            String namaBarang = "";
            int jumlahBarang = 0;

            if ("UANG".equalsIgnoreCase(jenis)) {
                String n = txtSalurNominal.getText().trim();
                if (n.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nominal wajib diisi untuk penyaluran UANG!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try { nominal = Double.parseDouble(n); }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Nominal harus angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (nominal <= 0) {
                    JOptionPane.showMessageDialog(this, "Nominal harus > 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validasi saldo (nilai plus)
                double saldoSaatIni = app.saldoUang();
                // Jika edit, saldo harus dihitung dengan "mengembalikan" nominal lama
                if (editModeDistribution && editingDistribution != null && "UANG".equalsIgnoreCase(editingDistribution.getJenis())) {
                    saldoSaatIni += editingDistribution.getNominal();
                }
                if (nominal > saldoSaatIni) {
                    JOptionPane.showMessageDialog(this,
                            "Saldo tidak cukup!\nSaldo saat ini: " + saldoSaatIni,
                            "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

            } else {
                namaBarang = txtSalurNamaBarang.getText().trim();
                jumlahBarang = (int) spSalurJumlahBarang.getValue();

                if (namaBarang.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama barang wajib diisi untuk penyaluran BARANG!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (jumlahBarang <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang harus > 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (!editModeDistribution) {
                Model.Distribution d = new Model.Distribution(id, tanggal, penerima, jenis, kategori, nominal, namaBarang, jumlahBarang, catatan);
                app.getDistributions().add(d);
            } else {
                editingDistribution.setTanggal(tanggal);
                editingDistribution.setPenerima(penerima);
                editingDistribution.setJenis(jenis);
                editingDistribution.setKategori(kategori);
                editingDistribution.setNominal(nominal);
                editingDistribution.setNamaBarang(namaBarang);
                editingDistribution.setJumlahBarang(jumlahBarang);
                editingDistribution.setCatatan(catatan);
            }

            app.persistDistributions();
            app.refreshAll();
            clearDistribution();
            app.showScreen(MainFrame.SCREEN_LIST);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDistribution() {
        txtSalurId.setText("");
        txtSalurTanggal.setText("");
        txtPenerima.setText("");
        txtSalurKategori.setText("");
        txtSalurNominal.setText("");
        txtSalurNamaBarang.setText("");
        spSalurJumlahBarang.setValue(1);
        txtSalurCatatan.setText("");
    }
}
