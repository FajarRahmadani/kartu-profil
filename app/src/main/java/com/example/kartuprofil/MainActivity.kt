package com.example.kartuprofil

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

/**
 * Layar 1: form input.
 * Alur: INPUT (nama, email, tahun lahir) -> PROSES (validasi, konversi, hitung umur, kategori)
 *       -> OUTPUT (dikirim lewat Intent ke SecondActivity).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTahunLahir: EditText
    private lateinit var btnBuat: Button
    private lateinit var btnHapusSimpan: Button
    private lateinit var tvStatus: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etTahunLahir = findViewById(R.id.etTahunLahir)
        btnBuat = findViewById(R.id.btnBuat)
        btnHapusSimpan = findViewById(R.id.btnHapusSimpan)
        tvStatus = findViewById(R.id.tvStatus)

        // "Lemari" lokal: berkas "profil", MODE_PRIVATE = hanya aplikasi ini yang bisa membaca
        prefs = getSharedPreferences(PREFS_PROFIL, MODE_PRIVATE)

        // ---------- BACA KEMBALI: isi otomatis kolom nama dengan nama terakhir ----------
        // Nilai bawaan "" + isNotEmpty() menjaga aplikasi tetap aman bila data
        // belum ada atau sudah dihapus (mis. "Hapus data" di Pengaturan HP).
        val namaTersimpan = prefs.getString(KEY_NAMA_TERAKHIR, "") ?: ""
        if (namaTersimpan.isNotEmpty()) {
            etNama.setText(namaTersimpan)
        }
        // Umur & kategori terakhir ditampilkan di tvStatus
        tampilkanStatusTersimpan()

        // ---------- HAPUS: benar-benar mengosongkan penyimpanan ----------
        // clear() menghapus SEMUA kunci di berkas "profil" (remove(kunci) hanya satu kunci).
        btnHapusSimpan.setOnClickListener {
            prefs.edit().clear().apply()
            etNama.text.clear()
            tvStatus.text = getString(R.string.status_dihapus)
        }

        btnBuat.setOnClickListener {
            // Bersihkan tanda error dari percobaan sebelumnya
            etNama.error = null
            etEmail.error = null
            etTahunLahir.error = null

            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val tahunText = etTahunLahir.text.toString().trim()

            // ---------- VALIDASI BERLAPIS (berhenti di kesalahan pertama) ----------

            // 1. Nama tidak boleh kosong
            if (nama.isEmpty()) {
                etNama.error = "Nama tidak boleh kosong"
                etNama.requestFocus()
                return@setOnClickListener
            }

            // 2. Email: tidak kosong + format valid
            if (email.isEmpty()) {
                etEmail.error = "Email tidak boleh kosong"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Format email tidak valid (contoh: nama@domain.com)"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            // 3. Tahun lahir: tidak kosong
            if (tahunText.isEmpty()) {
                etTahunLahir.error = "Tahun lahir tidak boleh kosong"
                etTahunLahir.requestFocus()
                return@setOnClickListener
            }

            // 4. Konversi aman: toIntOrNull() menghindari crash NumberFormatException
            val tahunLahir = tahunText.toIntOrNull()
            if (tahunLahir == null) {
                etTahunLahir.error = "Tahun lahir harus berupa angka"
                etTahunLahir.requestFocus()
                return@setOnClickListener
            }

            // 5. Rentang: tahun berjalan diambil dari Calendar (tidak di-hardcode)
            val tahunSekarang = Calendar.getInstance().get(Calendar.YEAR)
            if (tahunLahir < TAHUN_MINIMAL || tahunLahir > tahunSekarang) {
                etTahunLahir.error = "Tahun lahir harus antara $TAHUN_MINIMAL dan $tahunSekarang"
                etTahunLahir.requestFocus()
                return@setOnClickListener
            }

            // ---------- PROSES: semua data sudah valid ----------
            val umur = tahunSekarang - tahunLahir

            val kategori = when (umur) {
                in 0..12 -> "Anak"
                in 13..17 -> "Remaja"
                in 18..59 -> "Dewasa"
                else -> "Lansia"
            }

            // Kreativitas: kategori generasi berdasarkan tahun lahir
            val generasi = when (tahunLahir) {
                in 2013..2100 -> "Generasi Alpha"
                in 1997..2012 -> "Generasi Z"
                in 1981..1996 -> "Generasi Milenial"
                in 1965..1980 -> "Generasi X"
                else -> "Baby Boomer"
            }

            // ---------- SIMPAN: hanya data yang SUDAH lolos validasi ----------
            // apply() = simpan asinkron di latar belakang (bukan commit()),
            // dan diletakkan SEBELUM startActivity(intent).
            prefs.edit()
                .putString(KEY_NAMA_TERAKHIR, nama)
                .putInt(KEY_UMUR_TERAKHIR, umur)
                .putString(KEY_KATEGORI_TERAKHIR, kategori)
                .apply()
            tampilkanStatusTersimpan()

            // ---------- OUTPUT: kirim hasil olahan ke layar kedua ----------
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra(SecondActivity.EXTRA_NAMA, nama)
                putExtra(SecondActivity.EXTRA_EMAIL, email)
                putExtra(SecondActivity.EXTRA_UMUR, umur)
                putExtra(SecondActivity.EXTRA_KATEGORI, kategori)
                putExtra(SecondActivity.EXTRA_GENERASI, generasi)
            }
            startActivity(intent)
        }
    }

    // Membaca nama, umur, dan kategori terakhir. Teks awal di tvStatus dibiarkan
    // bila data belum lengkap (belum pernah disimpan, atau sudah dihapus).
    private fun tampilkanStatusTersimpan() {
        val nama = prefs.getString(KEY_NAMA_TERAKHIR, "") ?: ""
        val umur = prefs.getInt(KEY_UMUR_TERAKHIR, -1)
        val kategori = prefs.getString(KEY_KATEGORI_TERAKHIR, "") ?: ""

        if (nama.isNotEmpty() && umur >= 0 && kategori.isNotEmpty()) {
            tvStatus.text = getString(R.string.status_format, nama, umur, kategori)
        }
    }

    // Kunci simpan dan kunci baca harus sama persis, jadi ditulis sebagai konstanta
    companion object {
        private const val TAHUN_MINIMAL = 1950

        const val PREFS_PROFIL = "profil"
        const val KEY_NAMA_TERAKHIR = "nama_terakhir"
        const val KEY_UMUR_TERAKHIR = "umur_terakhir"
        const val KEY_KATEGORI_TERAKHIR = "kategori_terakhir"
    }
}
