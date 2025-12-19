package ui;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    private final MainFrame app;

    public ReportPanel(MainFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        add(new JLabel("Laporan/History (nanti kita isi setelah Donasi & Penyaluran)", SwingConstants.CENTER),
                BorderLayout.CENTER);
    }
}
