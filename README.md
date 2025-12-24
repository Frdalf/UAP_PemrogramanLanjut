## **APLIKASI SISTEM DONASI**

>**Ujian Akhir Praktikum - Pemrograman Lanjut**  
>🏛️ Universitas Muhammadiyah Malang - 2025

## 👥 Nama Anggota
| Nama                     | NIM             |
|--------------------------|-----------------|
| Syahrial Nur Faturrahman | 202410370110009 |
| Farid Al Farizi          | 202410370110017 |

## Deskripsi Aplikasi
Aplikasi Sistem Donasi merupakan aplikasi desktop berbasis Java (Swing) yang digunakan untuk mengelola data donasi secara terstruktur.
Aplikasi ini dirancang untuk membantu proses:  
- Pendataan donatur  
- Pencatatan donasi masuk  
- Pengelolaan penyaluran donasi  
- Penyajian dashboard & laporan

Data disimpan menggunakan file CSV sehingga mudah dibaca, diuji, dan dikelola tanpa database eksternal.

## ⚙️ Teknologi yang Digunakan  
- Java
- GUI: Java Swing
- IDE: IntelliJ IDEA
- Penyimpanan Data: CSV File

## 🗯️ Tujuan Pengembangan
- Mengimplementasikan konsep pemrograman java/GUI
- Menerapkan CRUD (Create, Read, Update, Delete)
- Menggunakan file CSV sebagai media penyimpanan data
- Mengembangkan aplikasi desktop berbasis Java Swing
- Melakukan pengujian manual terhadap seluruh fitur aplikasi

## 📶 Stuktur Aplikasi
Struktu aplikasi dibagi berdasarkan tanggung jawab masing-masing komponen  
src/  
├── App/              → Entry point aplikasi
├── Model/            → Class entitas (Donor, Donation, Distribution)
├── Repo/             → CRUD & file handling (CSV)
├── ui/               → Tampilan GUI Java Swing
├── Util/             → Class helper (CSV, ID, format uang)
└── Data/             → File CSV penyimpanan data

## 💻 Fitur Aplikasi
### 💠 Dashboard
- Menampilkan halaman utama aplikasi
- Navigasi ke seluruh menu utama
### 💠 Managemen Donor
- Tambah data donor
- Lihat daftar donor
- Edit data donor
- Hapus data donor
- Pencarian dan pengurutan data donor
### 💠 Manajemen Donasi
- Tambah data donasi
- Edit data donasi
- Hapus data donasi
- Validasi input nominal donasi
### 💠 Manajemen Distribusi Donasi
- Tambah data distribusi donasi
- Edit data distribusi
- Hapus data distribusi
- Pencatatan keterangan dan tanggal distribusi
### 💠 Laporan
- Menampilkan ringkasan dan histori transaksi donasi dan distribusi
### 💠 Penyimpanan Data
- Penyimpanan data menggunakan file CSV
- Data tidak hilang setelah aplikasi ditutup
### 💠 Tampilan & Validasi
- Validasi input angka dan nominal
- Notifikasi sistem
- Dark mode
- Tampilan antarmuka user-friendly

## 🔁 Alur Kerja Aplikasi
1. User melakukan input melalui UI
2. Data dibentuk menjadi objek Model
3. Operasi CRUD dilakukan oleh Repository
4. Data disimpan atau dibaca dari file CSV
5. Data ditampilkan kembali pada tabel UI

## 🧬 Pengujian Aplikasi
Pengujian dilakukan menggunakan testing manual berbasis skenario.  
Setiap fitur diuji dengan mencatat:
- Skenario pengujian
- Langkah pengujian
- Hasil yang diharapkan
- Hasil aktual
- Status pengujian
Hasil pengujian dicatat dalam bentuk tabel testing manual dan menunjukkan bahwa seluruh fitur utama aplikasi berjalan dengan baik.

## ▶️ Cara Menjalankan Aplikasi
1. Buka project menggunakan IntelliJ IDEA
2. Pastikan JDK telah terkonfigurasi
3. Jalankan file: _App.java_
4. Aplikasi akan terbuka dalam bentuk GUI desktop


## 📌 Catatan
Aplikasi ini dikembangkan untuk keperluan akademik dan sebagai implementasi materi Pemrograman Lanjut.

## 👥 Pembagian Tugas Tim
- Pengembang Program: Implementasi fitur dan logika aplikasi
- Dokumentasi & Testing: Penyusunan README, testing manual, code review, dan laporan
