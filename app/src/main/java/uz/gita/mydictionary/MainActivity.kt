package uz.gita.mydictionary

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import uz.gita.mydictionaryKS.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val db: DictionaryDao by lazy { DBHelper.getInstance() }
    private lateinit var adapter: DictionaryAdapter
    private var isSwapClicked = false
    private var isSearchClicked = false
    private var isStarClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = DictionaryAdapter(db.getAllEnglishWords())

        binding.apply {

            recyclerView.adapter = adapter

            btnSearch.setOnClickListener {
                isSearchClicked = !isSearchClicked
                if (isSearchClicked) searchBar.visibility = View.VISIBLE
                else {
                    searchBar.visibility = View.GONE
                    inputSearch.text.clear()
                    placeholder.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            btnQuit.setOnClickListener { finish() }

            swapLang.setOnClickListener {
                adapter.isUzbek = !adapter.isUzbek
                if (adapter.isUzbek) {
                    adapter.updateCursor(db.getAllUzbekWords())
                    if (inputSearch.text.isNotBlank())
                        adapter.updateCursor(db.searchUzbek(inputSearch.text.toString()))
                    langOne.text = "Uzbek"
                    langTwo.text = "English"
                } else {
                    adapter.updateCursor(db.getAllEnglishWords())
                    if (inputSearch.text.isNotBlank())
                        adapter.updateCursor(db.searchEnglish(inputSearch.text.toString()))
                    langOne.text = "English"
                    langTwo.text = "Uzbek"
                }
            }

            inputSearch.doAfterTextChanged {
                if (inputSearch.text.isBlank()) {
                    if (isSwapClicked) {
                        adapter.updateCursor(db.getAllUzbekWords())
                    } else adapter.updateCursor(db.getAllEnglishWords())
                } else {
                    if (!(inputSearch.text.contains("\""))) {
                        if (adapter.isUzbek) {
                            val cursor = db.searchUzbek(it.toString())
                            if (cursor.count == 0) {
                                placeholder.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                adapter.updateCursor(cursor)
                                placeholder.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                            }
                        } else {
                            val cursor = db.searchEnglish(it.toString())
                            if (cursor.count == 0) {
                                placeholder.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                adapter.updateCursor(cursor)
                                placeholder.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

            starredScreen.setOnClickListener {
                startActivity(Intent(this@MainActivity, Favourites::class.java))
            }

            adapter.setStarListener { fav, id ->
                if (fav == 0) {
                    db.addFavourite(id.toInt())
                } else db.removeFavourite(id.toInt())
                if (isStarClicked) adapter.updateCursor(db.getAllStarredWords())
                else if (inputSearch.text.isNotBlank()) {
                    if (adapter.isUzbek)
                        adapter.updateCursor(db.searchUzbek(binding.inputSearch.text.toString()))
                    else adapter.updateCursor(db.searchEnglish(binding.inputSearch.text.toString()))
                } else if (adapter.isUzbek) adapter.updateCursor(db.getAllUzbekWords())
                else adapter.updateCursor(db.getAllEnglishWords())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.isUzbek) {
            adapter.updateCursor(db.getAllUzbekWords())
            if (binding.inputSearch.text.isNotBlank())
                if (adapter.isUzbek)
                    adapter.updateCursor(db.searchUzbek(binding.inputSearch.text.toString()))
                else adapter.updateCursor(db.searchEnglish(binding.inputSearch.text.toString()))
        } else {
            adapter.updateCursor(db.getAllEnglishWords())
            if (binding.inputSearch.text.isNotBlank())
                if (adapter.isUzbek)
                    adapter.updateCursor(db.searchUzbek(binding.inputSearch.text.toString()))
                else adapter.updateCursor(db.searchEnglish(binding.inputSearch.text.toString()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
        _binding = null
    }
}