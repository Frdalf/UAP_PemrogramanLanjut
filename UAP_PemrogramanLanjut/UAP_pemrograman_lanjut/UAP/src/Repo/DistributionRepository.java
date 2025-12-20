package Repo;

import Model.Distribution;
import Util.CsvUtil;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DistributionRepository {
    private final Path filePath;

    public DistributionRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Path parent = filePath.getParent();
            if (parent != null && Files.notExists(parent)) Files.createDirectories(parent);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, "salurId,tanggal,penerima,jenis,kategori,nominal,namaBarang,jumlahBarang,catatan\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyiapkan distribution.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Distribution> loadAll() {
        List<Distribution> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                if (p.length < 9) continue;

                String id = p[0];
                LocalDate tgl = LocalDate.parse(p[1]);
                String penerima = p[2];
                String jenis = p[3];
                String kategori = p[4];

                double nominal = 0;
                try { nominal = Double.parseDouble(p[5].isEmpty() ? "0" : p[5]); } catch (Exception ignored) {}

                String namaBarang = p[6];

                int jumlahBarang = 0;
                try { jumlahBarang = Integer.parseInt(p[7].isEmpty() ? "0" : p[7]); } catch (Exception ignored) {}

                String catatan = p[8];

                list.add(new Distribution(id, tgl, penerima, jenis, kategori, nominal, namaBarang, jumlahBarang, catatan));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membaca distribution.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    public void saveAll(List<Distribution> list) {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write("salurId,tanggal,penerima,jenis,kategori,nominal,namaBarang,jumlahBarang,catatan\n");
            for (Distribution d : list) {
                bw.write(String.join(",",
                        CsvUtil.safe(d.getSalurId()),
                        d.getTanggal().toString(),
                        CsvUtil.safe(d.getPenerima()),
                        CsvUtil.safe(d.getJenis()),
                        CsvUtil.safe(d.getKategori()),
                        String.valueOf(d.getNominal()),
                        CsvUtil.safe(d.getNamaBarang()),
                        String.valueOf(d.getJumlahBarang()),
                        CsvUtil.safe(d.getCatatan())
                ));
                bw.write("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan distribution.csv:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
