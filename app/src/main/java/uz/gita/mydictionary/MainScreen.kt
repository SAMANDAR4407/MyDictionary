package uz.gita.mydictionary

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import uz.gita.mydictionaryKS.BuildConfig
import uz.gita.mydictionaryKS.R
import uz.gita.mydictionaryKS.databinding.ActivityMainScreenBinding

class MainScreen : AppCompatActivity() {

    private var _binding: ActivityMainScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnShare.setOnClickListener {
                shareApp()
            }
            btnInfo.setOnClickListener {
                val view = LayoutInflater.from(this@MainScreen).inflate(R.layout.dialog_info,null)
                val builder = AlertDialog.Builder(this@MainScreen)
                    .setView(view)
                view.findViewById<TextView>(R.id.pp).movementMethod = LinkMovementMethod.getInstance()
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }
            buttonStart.setOnClickListener {
                startActivity(Intent(this@MainScreen, MainActivity::class.java))
            }
        }
    }
    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            var shareMessage = "English Uzbek Dictionary. Enlarge your vocabulary".trim() + "\n"
            shareMessage = "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share using"))
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}