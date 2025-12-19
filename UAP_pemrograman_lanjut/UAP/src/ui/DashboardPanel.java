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

        double uangMasuk = app.totalUangMasuk();
        double uangKeluar = app.totalUangKeluar();
        double saldo = app.saldoUang();

        lblTotalDonor.setText("Total Donatur: " + totalDonor +
                " | Uang Masuk: " + uangMasuk +
                " | Uang Keluar: " + uangKeluar +
                " | Saldo: " + saldo);
    }
}
