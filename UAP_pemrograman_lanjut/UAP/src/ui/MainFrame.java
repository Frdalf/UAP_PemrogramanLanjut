package ui;

import Model.Donor;
import Repo.DonorRepository;

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

    public void showScreen(String screen) {
        cardLayout.show(content, screen);
    }

    // ====== Akses data ======
    public List<Donor> getDonors() { return donors; }
    public DonorRepository getDonorRepo() { return donorRepo; }

    public void refreshAll() {
        dashboardPanel.refresh();
        listPanel.refreshTable();
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
}
