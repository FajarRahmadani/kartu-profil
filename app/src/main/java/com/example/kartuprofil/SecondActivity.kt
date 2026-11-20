package com.example.kartuprofil

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Layar 2: menampilkan hasil olahan dari MainActivity.
 * Logika juga hidup di sini: inisial dibuat dari nama.
 */
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Nilai cadangan wajib ada agar aman bila extra tidak terkirim
        val nama = intent.getStringExtra(EXTRA_NAMA) ?: "-"
        val email = intent.getStringExtra(EXTRA_EMAIL) ?: "-"
        val umur = intent.getIntExtra(EXTRA_UMUR, -1)
        val kategori = intent.getStringExtra(EXTRA_KATEGORI) ?: "-"
        val generasi = intent.getStringExtra(EXTRA_GENERASI) ?: "-"

        // "Budi Santoso Pratama" -> "BS"
        val inisial = nama.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

        findViewById<TextView>(R.id.tvInisial).text = inisial
        findViewById<TextView>(R.id.tvNama).text = nama
        findViewById<TextView>(R.id.tvEmail).text = email
        findViewById<TextView>(R.id.tvUmur).text =
            if (umur >= 0) "$umur tahun" else "-"
        findViewById<TextView>(R.id.tvKategori).text = kategori
        findViewById<TextView>(R.id.tvGenerasi).text = generasi

        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Kunci Intent memakai konstanta agar pengirim & penerima tidak salah ketik
    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_UMUR = "extra_umur"
        const val EXTRA_KATEGORI = "extra_kategori"
        const val EXTRA_GENERASI = "extra_generasi"
    }
}
