package ui;

import javax.swing.*;
import java.awt.*;

public class ListPanel extends JPanel {
    private final MainFrame app;

    private final DonorTab donorTab;
    private final DonationTab donationTab;
    private final DistributionTab distributionTab;

    public ListPanel(MainFrame app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();

        donorTab = new DonorTab(app);
        donationTab = new DonationTab(app);
        distributionTab = new DistributionTab(app);

        tabs.addTab("Donatur", donorTab);
        tabs.addTab("Donasi Masuk", donationTab);
        tabs.addTab("Penyaluran", distributionTab);

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshAllTables() {
        donorTab.refreshTable();
        donationTab.refreshTable();
        distributionTab.refreshTable();
    }
}
