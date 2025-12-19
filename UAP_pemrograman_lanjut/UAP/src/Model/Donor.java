package Model;

import java.time.LocalDate;

public class Donor {
    private final String donorId;
    private String nama;
    private String kontak;
    private String alamat;
    private final LocalDate createdAt;

    public Donor(String donorId, String nama, String kontak, String alamat, LocalDate createdAt) {
        this.donorId = donorId;
        this.nama = nama;
        this.kontak = kontak;
        this.alamat = alamat;
        this.createdAt = createdAt;
    }

    public String getDonorId() { return donorId; }
    public String getNama() { return nama; }
    public String getKontak() { return kontak; }
    public String getAlamat() { return alamat; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setNama(String nama) { this.nama = nama; }
    public void setKontak(String kontak) { this.kontak = kontak; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}
