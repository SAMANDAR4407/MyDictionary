package uz.gita.mydictionary

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import uz.gita.mydictionaryKS.R
import java.util.*

class DictionaryAdapter(private var cursor: Cursor) :
    RecyclerView.Adapter<DictionaryAdapter.ViewHolder>(), TextToSpeech.OnInitListener {
    var isUzbek = false
    private var isValid = false
    private var listenerStar: ((Int, Long) -> Unit?)? = null
    private var itemClickListener: ((Context) -> Unit?)? = null

    private var tt: TextToSpeech? = null

    fun setStarListener(block: (Int, Long) -> Unit) {
        listenerStar = block
    }

    private val notifyingDataSetObserver = object : DataSetObserver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
        }
    }

    init {
        cursor.registerDataSetObserver(notifyingDataSetObserver)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCursor(newCursor: Cursor) {
        isValid = false
        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
        cursor.close()

        newCursor.registerDataSetObserver(notifyingDataSetObserver)
        cursor = newCursor
        isValid = true
        notifyDataSetChanged()
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textWord: TextView = view.findViewById(R.id.text_word)
        val textTranslate: TextView = view.findViewById(R.id.text_translate)
        val textTranscript: TextView = view.findViewById(R.id.text_transcript)
        val btnStar: ImageButton = view.findViewById(R.id.btn_star)
        val btnSpeak: ImageButton = view.findViewById(R.id.btn_speak)

        init {
            btnStar.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                val fav = cursor.getInt(cursor.getColumnIndex("favourite"))
                val id = cursor.getLong(cursor.getColumnIndex("id"))
                if (fav == 1) btnStar.setImageResource(R.drawable.icon_unstarred)
                else btnStar.setImageResource(R.drawable.icon_starred)
                listenerStar?.invoke(fav, id)
            }
            btnSpeak.setOnClickListener {
                itemClickListener?.invoke(view.context)
                tt?.speak(textWord.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }
            view.setOnClickListener {
                showDialog(view.context, textWord.text.toString(), textTranscript.text.toString(), textTranslate.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        tt = TextToSpeech(parent.context, this)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        if (!isUzbek) {
            holder.textWord.text = cursor.getString(cursor.getColumnIndex("english"))
            holder.textTranslate.text = cursor.getString(cursor.getColumnIndex("uzbek"))
            holder.textTranscript.text = cursor.getString(cursor.getColumnIndex("transcript"))
        } else {
            holder.textWord.text = cursor.getString(cursor.getColumnIndex("uzbek"))
            holder.textTranslate.text = cursor.getString(cursor.getColumnIndex("english"))
        }
        holder.btnStar.setImageResource(
            if (cursor.getInt(cursor.getColumnIndex("favourite")) == 0) R.drawable.icon_unstarred
            else R.drawable.icon_starred
        )
    }

    override fun getItemCount(): Int = cursor.count

    fun onDestroy() {
        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
        if (tt != null) {
            tt!!.stop()
            tt!!.shutdown()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tt!!.language = Locale.US
        }
    }

    fun showDialog(context: Context, word:String, transcript:String, description:String) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_description,null)
        val builder = AlertDialog.Builder(context).setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.findViewById<TextView>(R.id.word).text = word
        view.findViewById<TextView>(R.id.transcript).text = transcript
        view.findViewById<TextView>(R.id.description).text = description
        dialog.show()
    }
}