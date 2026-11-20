# Kartu Profil

Aplikasi Android (Kotlin) untuk **Tugas 2 — Logika Interaktif & Navigasi**, mata kuliah Pemrograman Mobile (Pertemuan 7, Sub-CPMK 3.2).

Pengguna mengisi nama, email, dan tahun lahir. Aplikasi memeriksa input, menghitung umur dan kategori, lalu mengirim hasilnya ke layar kedua yang menampilkan kartu profil.

## Alur: Input > Proses > Output

| Tahap | Isi |
|-------|-----|
| Input | Nama, email, tahun lahir (`MainActivity`) |
| Proses | Validasi berlapis > konversi `toIntOrNull()` > hitung umur > kategori `when` |
| Output | `Intent.putExtra` ke `SecondActivity` yang menampilkan kartu |

## Fitur

- **Validasi berlapis**, berhenti di kesalahan pertama (`return@setOnClickListener`), error muncul di kolom yang salah dan fokus berpindah ke sana:
  1. Nama tidak boleh kosong
  2. Email tidak kosong dan berformat valid (`Patterns.EMAIL_ADDRESS`)
  3. Tahun lahir tidak kosong
  4. Tahun lahir berupa angka (konversi aman `toIntOrNull()`, tidak crash)
  5. Tahun lahir dalam rentang 1950 sampai tahun berjalan (tahun diambil dari `Calendar`, tidak di-hardcode)
- **Perhitungan**: `umur = tahunSekarang - tahunLahir`
- **Kategori umur** dengan `when` rentang: Anak, Remaja, Dewasa, Lansia
- **Kreativitas**: kategori generasi (Alpha, Z, Milenial, X, Boomer) dan avatar inisial yang dibuat di layar kedua (`"Budi Santoso Pratama"` menjadi `BS`)
- **Navigasi**: `MainActivity` ke `SecondActivity` membawa 5 extra (kunci memakai konstanta `companion object`), tombol kembali dan tombol "Buat Kartu Lain"

## Cara menjalankan

1. Buka folder ini di **Android Studio** (Koala atau lebih baru). Gradle wrapper akan dibuat otomatis saat sync.
2. Pilih emulator atau perangkat (minSdk 24), lalu klik **Run**.

## Struktur

```
app/src/main/
├── java/com/example/kartuprofil/
│   ├── MainActivity.kt      # form, validasi, logika
│   └── SecondActivity.kt    # kartu hasil, inisial
├── res/layout/
│   ├── activity_main.xml
│   └── activity_second.xml
└── AndroidManifest.xml
```

## Skenario uji (untuk video demo)

| Kasus | Input | Hasil yang diharapkan |
|-------|-------|-----------------------|
| Valid | Budi Santoso, budi@mail.com, 2004 | Pindah layar: umur 22, Dewasa, Generasi Z, inisial BS |
| Nama kosong | (kosong) | Error di kolom nama, tidak pindah layar |
| Email tanpa @ | budi.mail.com | Error di kolom email |
| Tahun bukan angka / kosong | (kosong) | Error di kolom tahun lahir |
| Tahun masa depan | 2030 | Error rentang, menyebut tahun berjalan |

## Naskah video demo (1–2 menit)

1. **(0:00–0:20)** Tunjukkan layar utama dan jelaskan alurnya: input, proses, output.
2. **(0:20–1:00)** Kasus valid: isi data, tekan tombol, tunjukkan kartu di layar kedua (umur, kategori, generasi, inisial).
3. **(1:00–1:40)** Kasus tidak valid: kosongkan nama atau isi tahun 2030. Tunjukkan pesan error, fokus pindah ke kolom, dan aplikasi tidak pindah layar serta tidak crash.
4. **(1:40–2:00)** Tunjukkan sekilas kode `MainActivity.kt` bagian validasi dan `when`, lalu tutup.
