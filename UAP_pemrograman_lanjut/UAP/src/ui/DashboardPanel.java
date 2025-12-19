package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final MainFrame app;
    private final JLabel lblTotalDonor = new JLabel();

    public DashboardPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel center = new JPanel(new GridLayout(0, 1, 10, 10));
        center.add(lblTotalDonor);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        int totalDonor = app.getDonors().size();

        double totalUang = 0;
        int totalBarang = 0;

        for (Model.Donation d : app.getDonations()) {
            if ("UANG".equalsIgnoreCase(d.getJenis())) totalUang += d.getNominal();
            if ("BARANG".equalsIgnoreCase(d.getJenis())) totalBarang += d.getJumlahBarang();
        }

        lblTotalDonor.setText("Total Donatur: " + totalDonor +
                " | Total Donasi Uang: " + totalUang +
                " | Total Barang Masuk: " + totalBarang);
    }

}
