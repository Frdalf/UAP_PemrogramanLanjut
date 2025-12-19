package ui;

import Model.Donor;
import Repo.DonorRepository;

import Model.Donation;
import Repo.DonationRepository;

import Model.Distribution;
import Repo.DistributionRepository;

import java.util.Map;
import java.util.HashMap;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    public static final String SCREEN_DASHBOARD = "dashboard";
    public static final String SCREEN_LIST = "list";
    public static final String SCREEN_FORM = "form";
    public static final String SCREEN_REPORT = "report";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    // DATA & REPO (persisten)
    private final DonorRepository donorRepo = new DonorRepository("src/Data/donors.csv");
    private final List<Donor> donors = new ArrayList<>();

    private final DonationRepository donationRepo = new DonationRepository("src/Data/donation.csv");
    private final List<Donation> donations = new ArrayList<>();

    private final DistributionRepository distributionRepo = new DistributionRepository("src/Data/distribution.csv");
    private final List<Distribution> distributions = new ArrayList<>();


    private final DashboardPanel dashboardPanel;
    private final ListPanel listPanel;
    private final FormPanel formPanel;
    private final ReportPanel reportPanel;

    public MainFrame() {
        setTitle("Sistem Manajemen Donasi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        // load data awal dari CSV
        donors.addAll(donorRepo.loadAll());
        donations.addAll(donationRepo.loadAll());
        distributions.addAll(distributionRepo.loadAll());


        // Navbar
        JPanel nav = new JPanel(new GridLayout(0, 1, 10, 10));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnList = new JButton("List Donatur");
        JButton btnForm = new JButton("Input Donatur");
        JButton btnReport = new JButton("Laporan/History");

        nav.add(btnDashboard);
        nav.add(btnList);
        nav.add(btnForm);
        nav.add(btnReport);

        JButton btnDonationForm = new JButton("Input Donasi");
        nav.add(btnDonationForm);
        btnDonationForm.addActionListener(e -> openNewDonationForm());

        JButton btnDistributionForm = new JButton("Input Penyaluran");
        nav.add(btnDistributionForm);
        btnDistributionForm.addActionListener(e -> openNewDistributionForm());

        // Panels
        dashboardPanel = new DashboardPanel(this);
        listPanel = new ListPanel(this);
        formPanel = new FormPanel(this);
        reportPanel = new ReportPanel(this);

        content.add(dashboardPanel, SCREEN_DASHBOARD);
        content.add(listPanel, SCREEN_LIST);
        content.add(formPanel, SCREEN_FORM);
        content.add(reportPanel, SCREEN_REPORT);

        btnDashboard.addActionListener(e -> showScreen(SCREEN_DASHBOARD));
        btnList.addActionListener(e -> showScreen(SCREEN_LIST));
        btnForm.addActionListener(e -> openNewDonorForm());
        btnReport.addActionListener(e -> showScreen(SCREEN_REPORT));

        setLayout(new BorderLayout());
        add(nav, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        refreshAll();
        showScreen(SCREEN_DASHBOARD);
    }

    public void openNewDonationForm() {
        formPanel.setModeCreateDonation();
        showScreen(SCREEN_FORM);
    }

    public void openEditDonationForm(Donation donation) {
        formPanel.setModeEditDonation(donation);
        showScreen(SCREEN_FORM);
    }

    public List<Donation> getDonations() { return donations; }

    public void persistDonations() {
        donationRepo.saveAll(donations);
    }

    public void showScreen(String screen) {
        cardLayout.show(content, screen);
    }

    // ====== Akses data ======
    public List<Donor> getDonors() { return donors; }
    public DonorRepository getDonorRepo() { return donorRepo; }

    public List<Distribution> getDistributions() { return distributions; }

    public void persistDistributions() {
        distributionRepo.saveAll(distributions);
    }

    // ====== Navigasi Form ======
    public void openNewDonorForm() {
        formPanel.setModeCreate();
        showScreen(SCREEN_FORM);
    }

    public void openEditDonorForm(Donor donor) {
        formPanel.setModeEdit(donor);
        showScreen(SCREEN_FORM);
    }

    // ====== Save to CSV ======
    public void persistDonors() {
        donorRepo.saveAll(donors);
    }

    public void openNewDistributionForm() {
        formPanel.setModeCreateDistribution();
        showScreen(SCREEN_FORM);
    }

    public void openEditDistributionForm(Distribution d) {
        formPanel.setModeEditDistribution(d);
        showScreen(SCREEN_FORM);
    }

    public void refreshAll() {
        dashboardPanel.refresh();
        listPanel.refreshAllTables();
        reportPanel.refresh();
    }

    public double totalUangMasuk() {
        double sum = 0;
        for (Model.Donation d : donations) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) sum += d.getNominal();
        }
        return sum;
    }

    public double totalUangKeluar() {
        double sum = 0;
        for (Distribution d : distributions) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) sum += d.getNominal();
        }
        return sum;
    }

    public double saldoUang() {
        return totalUangMasuk() - totalUangKeluar();
    }

    public Map<java.time.YearMonth, Double> getMonthlyUangMasukLast6Months() {
        Map<java.time.YearMonth, Double> map = new HashMap<>();
        for (Model.Donation d : getDonations()) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) {
                java.time.YearMonth ym = java.time.YearMonth.from(d.getTanggal());
                map.put(ym, map.getOrDefault(ym, 0.0) + d.getNominal());
            }
        }
        // filter hanya 6 bulan terakhir (biar rapi)
        java.time.YearMonth now = java.time.YearMonth.now();
        Map<java.time.YearMonth, Double> out = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            java.time.YearMonth k = now.minusMonths(i);
            out.put(k, map.getOrDefault(k, 0.0));
        }
        return out;
    }

}
