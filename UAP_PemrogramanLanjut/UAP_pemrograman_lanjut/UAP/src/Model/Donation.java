package Model;

import java.time.LocalDate;

public class Donation {
    private final String donasiId;
    private LocalDate tanggal;
    private String donorId;
    private String jenis;      // "UANG" / "BARANG"
    private String kategori;
    private double nominal;    // dipakai kalau UANG
    private String namaBarang; // dipakai kalau BARANG
    private int jumlahBarang;  // dipakai kalau BARANG
    private String catatan;

    public Donation(String donasiId, LocalDate tanggal, String donorId, String jenis, String kategori,
                    double nominal, String namaBarang, int jumlahBarang, String catatan) {
        this.donasiId = donasiId;
        this.tanggal = tanggal;
        this.donorId = donorId;
        this.jenis = jenis;
        this.kategori = kategori;
        this.nominal = nominal;
        this.namaBarang = namaBarang;
        this.jumlahBarang = jumlahBarang;
        this.catatan = catatan;
    }

    public String getDonasiId() { return donasiId; }
    public LocalDate getTanggal() { return tanggal; }
    public String getDonorId() { return donorId; }
    public String getJenis() { return jenis; }
    public String getKategori() { return kategori; }
    public double getNominal() { return nominal; }
    public String getNamaBarang() { return namaBarang; }
    public int getJumlahBarang() { return jumlahBarang; }
    public String getCatatan() { return catatan; }

    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setNominal(double nominal) { this.nominal = nominal; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }
    public void setJumlahBarang(int jumlahBarang) { this.jumlahBarang = jumlahBarang; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
