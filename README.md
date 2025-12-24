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
- Bahasa: Java
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

