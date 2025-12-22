package ui;

import Model.Donor;
import Repo.DonorRepository;

import Model.Donation;
import Repo.DonationRepository;

import Model.Distribution;
import Repo.DistributionRepository;

import java.util.Map;
import java.util.HashMap;

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

    private NavButton navDashboard, navList, navInputDonor, navReport, navInputDonasi, navInputPenyaluran;
    private java.util.List<NavButton> navButtons;
    
    private JPanel nav;
    private DarkModeToggle darkModeToggle;


    public MainFrame() {
        setTitle("Sistem Manajemen Donasi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        // load data awal dari CSV
        donors.addAll(donorRepo.loadAll());
        donations.addAll(donationRepo.loadAll());
        distributions.addAll(distributionRepo.loadAll());

        // ===== Sidebar (Nav) =====
        nav = new SidebarPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));
        nav.setPreferredSize(new Dimension(240, 0));

        navDashboard = new NavButton("Dashboard", new NavButton.LineIcon(NavButton.LineIcon.Type.DASHBOARD));
        navList = new NavButton("List Donatur", new NavButton.LineIcon(NavButton.LineIcon.Type.LIST));
        navInputDonor = new NavButton("Input Donatur", new NavButton.LineIcon(NavButton.LineIcon.Type.USER));
        navReport = new NavButton("Laporan/History", new NavButton.LineIcon(NavButton.LineIcon.Type.HISTORY));
        navInputDonasi = new NavButton("Input Donasi", new NavButton.LineIcon(NavButton.LineIcon.Type.UP));
        navInputPenyaluran = new NavButton("Input Penyaluran", new NavButton.LineIcon(NavButton.LineIcon.Type.DOWN));

        navButtons = java.util.List.of(
                navDashboard, navList, navInputDonor, navReport, navInputDonasi, navInputPenyaluran
        );

        nav.add(navDashboard);
        nav.add(Box.createVerticalStrut(12));
        nav.add(navList);
        nav.add(Box.createVerticalStrut(12));
        nav.add(navInputDonor);
        nav.add(Box.createVerticalStrut(12));
        nav.add(navReport);
        nav.add(Box.createVerticalStrut(12));
        nav.add(navInputDonasi);
        nav.add(Box.createVerticalStrut(12));
        nav.add(navInputPenyaluran);
        nav.add(Box.createVerticalGlue());
        
        // Dark mode toggle at bottom
        darkModeToggle = new DarkModeToggle();
        darkModeToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        darkModeToggle.addActionListener(e -> {
            ThemeManager.toggleDarkMode();
        });
        nav.add(darkModeToggle);
        nav.add(Box.createVerticalStrut(10));

        navDashboard.setAlignmentX(Component.LEFT_ALIGNMENT);
        navList.setAlignmentX(Component.LEFT_ALIGNMENT);
        navInputDonor.setAlignmentX(Component.LEFT_ALIGNMENT);
        navReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        navInputDonasi.setAlignmentX(Component.LEFT_ALIGNMENT);
        navInputPenyaluran.setAlignmentX(Component.LEFT_ALIGNMENT);


        // ===== Panels =====
        dashboardPanel = new DashboardPanel(this);
        listPanel = new ListPanel(this);
        formPanel = new FormPanel(this);
        reportPanel = new ReportPanel(this);
        
        // Register theme change listener
        ThemeManager.addThemeChangeListener(this::onThemeChanged);

        content.add(dashboardPanel, SCREEN_DASHBOARD);
        content.add(listPanel, SCREEN_LIST);
        content.add(formPanel, SCREEN_FORM);
        content.add(reportPanel, SCREEN_REPORT);

        // ===== Action =====
        navDashboard.addActionListener(e -> showScreen(SCREEN_DASHBOARD));
        navList.addActionListener(e -> showScreen(SCREEN_LIST));
        navInputDonor.addActionListener(e -> {
            openNewDonorForm();
            for (NavButton b : navButtons) b.setActive(false);
            navInputDonor.setActive(true);
        });
        navReport.addActionListener(e -> showScreen(SCREEN_REPORT));
        navInputDonasi.addActionListener(e -> openNewDonationForm());
        navInputPenyaluran.addActionListener(e -> openNewDistributionForm());

        // ===== Layout frame =====
        setLayout(new BorderLayout());
        add(nav, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        // ===== Default state =====
        refreshAll();
        showScreen(SCREEN_DASHBOARD);
    }


    public void openNewDonationForm() {
        formPanel.setModeCreateDonation();
        showScreen(SCREEN_FORM);
        for (NavButton b : navButtons) b.setActive(false);
        navInputDonasi.setActive(true);
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
        setActiveNav(screen);
    }

    private void setActiveNav(String screen) {
        if (navButtons == null) return;
        for (NavButton b : navButtons) b.setActive(false);

        switch (screen) {
            case SCREEN_DASHBOARD -> navDashboard.setActive(true);
            case SCREEN_LIST -> navList.setActive(true);
            case SCREEN_REPORT -> navReport.setActive(true);
            case SCREEN_FORM -> {
                // kalau lagi di form, default aktifkan salah satu (opsional)
                // biar gak "mati semua". Kamu bisa atur lebih spesifik nanti.
                navInputDonor.setActive(true);
            }
        }
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
        for (NavButton b : navButtons) b.setActive(false);
        navInputDonor.setActive(true);
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
        for (NavButton b : navButtons) b.setActive(false);
        navInputPenyaluran.setActive(true);
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
    
    /**
     * Called when theme changes (dark/light mode toggle).
     * Repaints all components to apply new theme colors.
     */
    private void onThemeChanged() {
        // Repaint entire frame to apply new theme
        SwingUtilities.invokeLater(() -> {
            // Repaint navigation
            nav.repaint();
            darkModeToggle.repaint();
            for (NavButton btn : navButtons) {
                btn.repaint();
            }
            
            // Refresh all panels to update their colors
            dashboardPanel.refresh();
            listPanel.refreshAllTables();
            reportPanel.refresh();
            formPanel.repaint();
            
            // Repaint content panel
            content.repaint();
            
            // Force full repaint
            repaint();
            revalidate();
        });
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
