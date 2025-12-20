package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ListPanel extends JPanel {
    private final MainFrame app;

    private final DonorTab donorTab;
    private final DonationTab donationTab;
    private final DistributionTab distributionTab;

    public ListPanel(MainFrame app) {
        this.app = app;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setOpaque(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 12.5f));

        donorTab = new DonorTab(app);
        donationTab = new DonationTab(app);
        distributionTab = new DistributionTab(app);

        tabs.addTab("Donatur", donorTab);
        tabs.addTab("Donasi Masuk", donationTab);
        tabs.addTab("Penyaluran", distributionTab);

        // Auto refresh saat pindah tab (biar data selalu update)
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) donorTab.refreshTable();
            else if (idx == 1) donationTab.refreshTable();
            else if (idx == 2) distributionTab.refreshTable();
        });

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshAllTables() {
        donorTab.refreshTable();
        donationTab.refreshTable();
        distributionTab.refreshTable();
    }
}
