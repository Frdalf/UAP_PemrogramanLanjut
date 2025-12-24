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

## Stuktur Aplikasi
Struktu aplikasi dibagi berdasarkan tanggung jawab masing-masing komponen  
src/
├── App/              → Entry point aplikasi
├── Model/            → Class entitas (Donor, Donation, Distribution)
├── Repo/             → CRUD & file handling (CSV)
├── ui/               → Tampilan GUI Java Swing
├── Util/             → Class helper (CSV, ID, format uang)
└── Data/             → File CSV penyimpanan data
