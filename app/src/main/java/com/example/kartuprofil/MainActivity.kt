package com.example.kartuprofil

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etTahunLahir = findViewById(R.id.etTahunLahir)
        btnBuat = findViewById(R.id.btnBuat)

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

    companion object {
        private const val TAHUN_MINIMAL = 1950
    }
}
