package ui;

import Model.Donor;
import Model.Donation;
import Model.Distribution;
import Util.IdGenerator;
import Util.MoneyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
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

    // Root background (soft gradient)
    private final SoftFormUI.FormBackground root = new SoftFormUI.FormBackground();

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
    private final JTextField txtSalurJumlahBarang = new JTextField();
    private final JTextArea txtSalurCatatan = new JTextArea(3, 20);

    // wrapper field (untuk repaint saat enable/disable)
    private SoftFormUI.IconField fSalurNominal;
    private SoftFormUI.IconField fSalurNamaBarang;
    private SoftFormUI.IconField fSalurJumlahBarang;


    public FormPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Auto-format input nominal: tiap 3 digit pakai titik (.)
        MoneyDocumentFilter.install(txtNominal);
        MoneyDocumentFilter.install(txtSalurNominal);
        
        // Jumlah barang hanya menerima angka
        NumericDocumentFilter.install(txtJumlahBarang);
        NumericDocumentFilter.install(txtSalurJumlahBarang);

        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(26, 26, 26, 26));
        add(root, BorderLayout.CENTER);

        title.setFont(new Font("SansSerif", Font.BOLD, 56));
        // Title uses theme color
        title.setForeground(ThemeManager.getTitleColor());
        ThemeManager.addThemeChangeListener(() -> title.setForeground(ThemeManager.getTitleColor()));

        // build panel tab sekali saja
        donorTabPanel = buildDonorForm();
        donationTabPanel = buildDonationForm();
        distributionTabPanel = buildDistributionForm();

        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setFocusable(false);

        // Hide tab header strip + hilangkan content border/background bawaan JTabbedPane
        // supaya area kosong di bawah form tetap menyatu dengan background (tidak jadi kotak abu-abu).
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }

            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
                // no-op
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // no-op (hilangkan kotak/border konten)
            }

            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(0, 0, 0, 0);
            }
        });

        // pastikan benar-benar transparan setelah UI dipasang
        tabs.setOpaque(false);
        tabs.setBackground(new Color(0, 0, 0, 0));

        root.add(title, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

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
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        SoftFormUI.CardPanel card = new SoftFormUI.CardPanel(26);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        txtId.setEditable(false);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        addRow(form, 0, label("Donor ID"), new SoftFormUI.IconField(SoftFormUI.IconType.ID, txtId));
        addRow(form, 1, labelReq("Nama"), new SoftFormUI.IconField(SoftFormUI.IconType.USER, txtNama));
        addRow(form, 2, label("Kontak"), new SoftFormUI.IconField(SoftFormUI.IconType.PHONE, txtKontak));

        JScrollPane spAlamat = new JScrollPane(txtAlamat);
        spAlamat.setOpaque(false);
        spAlamat.getViewport().setOpaque(false);
        spAlamat.setBorder(null);
        spAlamat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtAlamat.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtAlamat.setBorder(null);
        txtAlamat.setOpaque(false);
        txtAlamat.setForeground(ThemeManager.getTextPrimary());
        txtAlamat.setCaretColor(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> {
            txtAlamat.setForeground(ThemeManager.getTextPrimary());
            txtAlamat.setCaretColor(ThemeManager.getTextPrimary());
        });
        SoftFormUI.IconField alamatField = new SoftFormUI.IconField(SoftFormUI.IconType.PIN, spAlamat);
        alamatField.setPreferredSize(new Dimension(0, 120));
        addRow(form, 3, label("Alamat"), alamatField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        actions.setOpaque(false);

        PillButton btnSave = new PillButton("\u2713  Simpan Donatur");
        PillButton btnCancel = new PillButton("Batal")
                .setColors(new Color(125, 132, 145), new Color(145, 152, 165), new Color(105, 112, 125), new Color(100, 106, 118));

        actions.add(btnSave);
        actions.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> {
            clear();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        // Form Donatur relatif pendek. Kalau CardPanel dipasang di CENTER,
        // BorderLayout akan memaksa card mengisi tinggi yang tersisa sehingga area atas
        // terlihat kosong (seperti yang kamu lingkari merah). Pasang card di NORTH agar
        // tinggi card mengikuti konten, dan sisa ruang jadi background (lebih "clean").
        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private JPanel buildDonationForm() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        SoftFormUI.CardPanel card = new SoftFormUI.CardPanel(26);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        txtDonasiId.setEditable(false);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);

        SoftFormUI.IconField fId = new SoftFormUI.IconField(SoftFormUI.IconType.ID, txtDonasiId);
        SoftFormUI.IconField fTanggal = new SoftFormUI.IconField(SoftFormUI.IconType.CALENDAR, txtTanggal);
        SoftFormUI.IconField fDonor = new SoftFormUI.IconField(SoftFormUI.IconType.USER, cmbDonor);
        SoftFormUI.IconField fJenis = new SoftFormUI.IconField(SoftFormUI.IconType.LIST, cmbJenis);
        SoftFormUI.IconField fKategori = new SoftFormUI.IconField(SoftFormUI.IconType.TAG, txtKategori);
        SoftFormUI.IconField fNominal = new SoftFormUI.IconField(SoftFormUI.IconType.MONEY, txtNominal);
        SoftFormUI.IconField fNamaBarang = new SoftFormUI.IconField(SoftFormUI.IconType.BOX, txtNamaBarang);
        SoftFormUI.IconField fJumlahBarang = new SoftFormUI.IconField(SoftFormUI.IconType.NUMBER, txtJumlahBarang);

        JScrollPane spCatatan = new JScrollPane(txtCatatan);
        spCatatan.setOpaque(false);
        spCatatan.getViewport().setOpaque(false);
        spCatatan.setBorder(null);
        spCatatan.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtCatatan.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtCatatan.setBorder(null);
        txtCatatan.setOpaque(false);
        txtCatatan.setForeground(ThemeManager.getTextPrimary());
        txtCatatan.setCaretColor(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> {
            txtCatatan.setForeground(ThemeManager.getTextPrimary());
            txtCatatan.setCaretColor(ThemeManager.getTextPrimary());
        });
        SoftFormUI.IconField fCatatan = new SoftFormUI.IconField(SoftFormUI.IconType.NOTE, spCatatan);
        fCatatan.setPreferredSize(new Dimension(0, 90));

        addRow(form, 0, label("Donasi ID"), fId);
        addRow(form, 1, labelReq("Tanggal (yyyy-MM-dd)"), fTanggal);
        addRow(form, 2, labelReq("Donor"), fDonor);
        addRow(form, 3, labelReq("Jenis"), fJenis);
        addRow(form, 4, label("Kategori"), fKategori);
        addRow(form, 5, label("Nominal (UANG)"), fNominal);
        addRow(form, 6, label("Nama Barang (BARANG)"), fNamaBarang);
        addRow(form, 7, label("Jumlah Barang (BARANG)"), fJumlahBarang);
        addRow(form, 8, label("Catatan"), fCatatan);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        actions.setOpaque(false);
        PillButton btnSave = new PillButton("\u2713  Simpan Donasi");
        PillButton btnCancel = new PillButton("Batal")
                .setColors(new Color(125, 132, 145), new Color(145, 152, 165), new Color(105, 112, 125), new Color(100, 106, 118));
        actions.add(btnSave);
        actions.add(btnCancel);

        cmbJenis.addActionListener(e -> {
            toggleDonationFields();
            // repaint wrapper supaya state enabled kelihatan
            fNominal.repaint();
            fNamaBarang.repaint();
            fJumlahBarang.repaint();
        });

        btnSave.addActionListener(e -> onSaveDonation());
        btnCancel.addActionListener(e -> {
            clearDonation();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        // Form Donasi & Penyaluran cukup panjang. Pada ukuran window default (1100x650),
        // GridBagLayout akan "memampatkan" row sehingga field paling atas bisa terlihat
        // seperti kepotong. Solusinya: beri scroll yang tetap menjaga style.
        JPanel scrollWrap = new JPanel(new BorderLayout());
        scrollWrap.setOpaque(false);
        scrollWrap.add(card, BorderLayout.NORTH); // NORTH => card mengikuti lebar, tinggi mengikuti konten

        JScrollPane scroll = new JScrollPane(scrollWrap);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        outer.add(scroll, BorderLayout.CENTER);

        return outer;
    }

    // ===== helpers UI (label + row) =====
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 16));
        l.setForeground(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> l.setForeground(ThemeManager.getTextPrimary()));
        return l;
    }

    private JLabel labelReq(String text) {
        JLabel l = new JLabel("<html>" + text + " <span style='color:#e74c3c'>*</span></html>");
        l.setFont(new Font("SansSerif", Font.PLAIN, 16));
        l.setForeground(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> l.setForeground(ThemeManager.getTextPrimary()));
        return l;
    }

    private void addRow(JPanel form, int row, JComponent label, JComponent field) {
        GridBagConstraints gcL = new GridBagConstraints();
        gcL.gridx = 0;
        gcL.gridy = row;
        gcL.weightx = 0.35;
        gcL.anchor = GridBagConstraints.WEST;
        gcL.insets = new Insets(10, 6, 10, 18);
        form.add(label, gcL);

        GridBagConstraints gcF = new GridBagConstraints();
        gcF.gridx = 1;
        gcF.gridy = row;
        gcF.weightx = 0.65;
        gcF.fill = GridBagConstraints.HORIZONTAL;
        gcF.insets = new Insets(10, 0, 10, 6);
        form.add(field, gcF);
    }

    private JPanel buildDistributionForm() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        SoftFormUI.CardPanel card = new SoftFormUI.CardPanel(26);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        txtSalurId.setEditable(false);
        txtSalurCatatan.setLineWrap(true);
        txtSalurCatatan.setWrapStyleWord(true);

        SoftFormUI.IconField fId = new SoftFormUI.IconField(SoftFormUI.IconType.ID, txtSalurId);
        SoftFormUI.IconField fTanggal = new SoftFormUI.IconField(SoftFormUI.IconType.CALENDAR, txtSalurTanggal);
        SoftFormUI.IconField fPenerima = new SoftFormUI.IconField(SoftFormUI.IconType.USER, txtPenerima);
        SoftFormUI.IconField fJenis = new SoftFormUI.IconField(SoftFormUI.IconType.LIST, cmbSalurJenis);
        SoftFormUI.IconField fKategori = new SoftFormUI.IconField(SoftFormUI.IconType.TAG, txtSalurKategori);

        fSalurNominal = new SoftFormUI.IconField(SoftFormUI.IconType.MONEY, txtSalurNominal);
        fSalurNamaBarang = new SoftFormUI.IconField(SoftFormUI.IconType.BOX, txtSalurNamaBarang);
        fSalurJumlahBarang = new SoftFormUI.IconField(SoftFormUI.IconType.NUMBER, txtSalurJumlahBarang);

        JScrollPane spCatatan = new JScrollPane(txtSalurCatatan);
        spCatatan.setOpaque(false);
        spCatatan.getViewport().setOpaque(false);
        spCatatan.setBorder(null);
        spCatatan.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtSalurCatatan.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSalurCatatan.setBorder(null);
        txtSalurCatatan.setOpaque(false);
        txtSalurCatatan.setForeground(ThemeManager.getTextPrimary());
        txtSalurCatatan.setCaretColor(ThemeManager.getTextPrimary());
        ThemeManager.addThemeChangeListener(() -> {
            txtSalurCatatan.setForeground(ThemeManager.getTextPrimary());
            txtSalurCatatan.setCaretColor(ThemeManager.getTextPrimary());
        });
        SoftFormUI.IconField fCatatan = new SoftFormUI.IconField(SoftFormUI.IconType.NOTE, spCatatan);
        fCatatan.setPreferredSize(new Dimension(0, 90));

        addRow(form, 0, label("Salur ID"), fId);
        addRow(form, 1, labelReq("Tanggal (yyyy-MM-dd)"), fTanggal);
        addRow(form, 2, labelReq("Penerima"), fPenerima);
        addRow(form, 3, labelReq("Jenis"), fJenis);
        addRow(form, 4, label("Kategori"), fKategori);
        addRow(form, 5, label("Nominal (UANG)"), fSalurNominal);
        addRow(form, 6, label("Nama Barang (BARANG)"), fSalurNamaBarang);
        addRow(form, 7, label("Jumlah Barang (BARANG)"), fSalurJumlahBarang);
        addRow(form, 8, label("Catatan"), fCatatan);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        actions.setOpaque(false);

        PillButton btnSave = new PillButton("\u2713  Simpan Penyaluran");
        PillButton btnCancel = new PillButton("Batal")
                .setColors(new Color(125, 132, 145), new Color(145, 152, 165), new Color(105, 112, 125), new Color(100, 106, 118));
        actions.add(btnSave);
        actions.add(btnCancel);

        cmbSalurJenis.addActionListener(e -> {
            toggleDistributionFields();
            if (fSalurNominal != null) fSalurNominal.repaint();
            if (fSalurNamaBarang != null) fSalurNamaBarang.repaint();
            if (fSalurJumlahBarang != null) fSalurJumlahBarang.repaint();
        });

        btnSave.addActionListener(e -> onSaveDistribution());
        btnCancel.addActionListener(e -> {
            clearDistribution();
            app.showScreen(MainFrame.SCREEN_LIST);
        });

        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        // Form Penyaluran juga cukup panjang (mirip Donasi). Agar tidak "kepotong"
        // saat window default (1100x650), bungkus dengan scrollpane yang tetap transparan.
        JPanel scrollWrap = new JPanel(new BorderLayout());
        scrollWrap.setOpaque(false);
        scrollWrap.add(card, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(scrollWrap);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        outer.add(scroll, BorderLayout.CENTER);

        toggleDistributionFields();
        return outer;
    }

    private void toggleDistributionFields() {
        String jenis = (String) cmbSalurJenis.getSelectedItem();
        boolean uang = "UANG".equalsIgnoreCase(jenis);

        txtSalurNominal.setEnabled(uang);

        txtSalurNamaBarang.setEnabled(!uang);
        txtSalurJumlahBarang.setEnabled(!uang);

        // repaint wrapper supaya state enabled kelihatan
        if (fSalurNominal != null) fSalurNominal.repaint();
        if (fSalurNamaBarang != null) fSalurNamaBarang.repaint();
        if (fSalurJumlahBarang != null) fSalurJumlahBarang.repaint();
    }


    private void toggleDonationFields() {
        String jenis = (String) cmbJenis.getSelectedItem();
        boolean uang = "UANG".equalsIgnoreCase(jenis);

        txtNominal.setEnabled(uang);

        txtNamaBarang.setEnabled(!uang);
        txtJumlahBarang.setEnabled(!uang);
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
        txtJumlahBarang.setText("1");
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
        txtNominal.setText(MoneyUtil.format(donation.getNominal()));
        txtNamaBarang.setText(donation.getNamaBarang());
        txtJumlahBarang.setText(String.valueOf(Math.max(1, donation.getJumlahBarang())));
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
                    // txtNominal bisa berisi "1.000.000" -> ambil digit saja
                    nominal = (double) MoneyUtil.parseToLong(n);
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
                String jumlahStr = txtJumlahBarang.getText().trim();
                if (jumlahStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    jumlahBarang = Integer.parseInt(jumlahStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang harus angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
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
            Toast.success(this, editModeDonation ? "Donasi diperbarui" : "Donasi tersimpan");
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
        txtJumlahBarang.setText("1");
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
            Toast.success(this, editMode ? "Donatur diperbarui" : "Donatur tersimpan");
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
    private final JTextField txtJumlahBarang = new JTextField();
    private final JTextArea txtCatatan = new JTextArea(3, 20);

    public void setModeCreateDistribution() {
        // Validasi: cek apakah ada saldo uang atau barang yang tersedia
        double saldoUang = app.saldoUang();
        boolean adaBarang = app.adaBarangTersedia();
        
        if (saldoUang <= 0 && !adaBarang) {
            JOptionPane.showMessageDialog(this,
                    "Tidak dapat membuat penyaluran!\n\n" +
                    "• Saldo uang: Rp 0\n" +
                    "• Barang tersedia: Tidak ada\n\n" +
                    "Silakan tambahkan donasi terlebih dahulu.",
                    "Tidak Ada Stok", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        applyTabMode(TabMode.DISTRIBUTION);

        editModeDistribution = false;
        editingDistribution = null;
        title.setText("Tambah Penyaluran");

        txtSalurId.setText(Util.IdGenerator.nextDistributionId(app.getDistributions()));
        txtSalurTanggal.setText(java.time.LocalDate.now().toString());
        txtPenerima.setText("");
        
        // Auto-select jenis berdasarkan ketersediaan
        if (saldoUang > 0) {
            cmbSalurJenis.setSelectedItem("UANG");
        } else {
            cmbSalurJenis.setSelectedItem("BARANG");
        }
        
        txtSalurKategori.setText("");
        txtSalurNominal.setText("");
        txtSalurNamaBarang.setText("");
        txtSalurJumlahBarang.setText("1");
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
        txtSalurNominal.setText(MoneyUtil.format(d.getNominal()));
        txtSalurNamaBarang.setText(d.getNamaBarang());
        txtSalurJumlahBarang.setText(String.valueOf(Math.max(1, d.getJumlahBarang())));
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
                try {
                    nominal = (double) MoneyUtil.parseToLong(n);
                } catch (Exception e) {
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
                String jumlahStr = txtSalurJumlahBarang.getText().trim();
                if (jumlahStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    jumlahBarang = Integer.parseInt(jumlahStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang harus angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (namaBarang.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama barang wajib diisi untuk penyaluran BARANG!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (jumlahBarang <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah barang harus > 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validasi stok barang
                int stokSaatIni = app.getStokBarangByNama(namaBarang);
                // Jika edit, stok harus dihitung dengan "mengembalikan" jumlah lama
                if (editModeDistribution && editingDistribution != null 
                        && "BARANG".equalsIgnoreCase(editingDistribution.getJenis())
                        && namaBarang.equalsIgnoreCase(editingDistribution.getNamaBarang())) {
                    stokSaatIni += editingDistribution.getJumlahBarang();
                }
                if (stokSaatIni <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Barang '" + namaBarang + "' tidak tersedia!\nStok saat ini: 0",
                            "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (jumlahBarang > stokSaatIni) {
                    JOptionPane.showMessageDialog(this,
                            "Stok barang '" + namaBarang + "' tidak mencukupi!\nStok saat ini: " + stokSaatIni,
                            "Validasi", JOptionPane.WARNING_MESSAGE);
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
            Toast.success(this, editModeDistribution ? "Penyaluran diperbarui" : "Penyaluran tersimpan");
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
        txtSalurJumlahBarang.setText("1");
        txtSalurCatatan.setText("");
    }
}
