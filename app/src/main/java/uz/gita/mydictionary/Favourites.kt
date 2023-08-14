package uz.gita.mydictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import uz.gita.mydictionaryKS.R
import uz.gita.mydictionaryKS.databinding.ActivityFavouritesBinding
import uz.gita.mydictionaryKS.databinding.ActivityMainScreenBinding

class Favourites : AppCompatActivity() {

    private var _binding: ActivityFavouritesBinding? = null
    private val binding get() = _binding!!

    private val db: DictionaryDao by lazy { DBHelper.getInstance() }
    private val adapter by lazy { FavouritesAdapter(db.getAllStarredWords()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            if (db.getAllStarredWords().count != 0)
                adapter.updateCursor(db.getAllStarredWords())
            else {
                placeholder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            recyclerView.adapter = adapter
            adapter.setStarListener { id ->
                db.removeFavourite(id.toInt())
                val cursor = db.getAllStarredWords()
                if (cursor.count != 0)
                    adapter.updateCursor(cursor)
                else {
                    placeholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
            btnBack.setOnClickListener { finish() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}