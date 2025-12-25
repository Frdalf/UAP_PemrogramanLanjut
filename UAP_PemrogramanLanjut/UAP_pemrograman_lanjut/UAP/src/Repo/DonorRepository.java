package Repo;

import Model.Donor;
import Util.CsvUtil;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonorRepository {
    private final Path filePath;

    public DonorRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Path parent = filePath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, "donorId,nama,kontak,alamat,createdAt\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyiapkan donors.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Donor> loadAll() {
        List<Donor> donors = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                if (p.length < 5) continue;

                donors.add(new Donor(
                        p[0], p[1], p[2], p[3], LocalDate.parse(p[4])
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membaca donors.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return donors;
    }

    public void saveAll(List<Donor> donors) {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write("donorId,nama,kontak,alamat,createdAt\n");
            for (Donor d : donors) {
                bw.write(String.join(",",
                        CsvUtil.safe(d.getDonorId()),
                        CsvUtil.safe(d.getNama()),
                        CsvUtil.safe(d.getKontak()),
                        CsvUtil.safe(d.getAlamat()),
                        d.getCreatedAt().toString()
                ));
                bw.write("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan donors.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
