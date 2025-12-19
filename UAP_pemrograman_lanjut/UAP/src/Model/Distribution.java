package Model;

import java.time.LocalDate;

public class Distribution {
    private final String salurId;
    private LocalDate tanggal;
    private String penerima;
    private String jenis;      // UANG / BARANG
    private String kategori;
    private double nominal;
    private String namaBarang;
    private int jumlahBarang;
    private String catatan;

    public Distribution(String salurId, LocalDate tanggal, String penerima, String jenis, String kategori,
                        double nominal, String namaBarang, int jumlahBarang, String catatan) {
        this.salurId = salurId;
        this.tanggal = tanggal;
        this.penerima = penerima;
        this.jenis = jenis;
        this.kategori = kategori;
        this.nominal = nominal;
        this.namaBarang = namaBarang;
        this.jumlahBarang = jumlahBarang;
        this.catatan = catatan;
    }

    public String getSalurId() { return salurId; }
    public LocalDate getTanggal() { return tanggal; }
    public String getPenerima() { return penerima; }
    public String getJenis() { return jenis; }
    public String getKategori() { return kategori; }
    public double getNominal() { return nominal; }
    public String getNamaBarang() { return namaBarang; }
    public int getJumlahBarang() { return jumlahBarang; }
    public String getCatatan() { return catatan; }

    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setPenerima(String penerima) { this.penerima = penerima; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setNominal(double nominal) { this.nominal = nominal; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }
    public void setJumlahBarang(int jumlahBarang) { this.jumlahBarang = jumlahBarang; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
