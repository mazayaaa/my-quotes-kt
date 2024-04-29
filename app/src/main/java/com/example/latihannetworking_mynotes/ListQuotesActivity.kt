package com.example.latihannetworking_mynotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.latihannetworking_mynotes.databinding.ActivityListQuotesBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class ListQuotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuotesBinding
    private lateinit var listQuote: ArrayList<String>
    private lateinit var adapter: QuoteAdapter
    private var currentPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "List of Quotes"
        listQuote = ArrayList()
        adapter = QuoteAdapter(listQuote)
        binding.listQuotes.layoutManager = LinearLayoutManager(this)
        binding.listQuotes.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.listQuotes.adapter = adapter
        binding.listQuotes.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    loadNextPage()
                }
            }
        })
        getListQuotes()
    }

    private fun getListQuotes() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.quotable.io/quotes?page=$currentPage&limit=20"
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int, headers: Array<Header>,
                response: JSONObject
            ) {
                binding.progressBar.visibility = android.view.View.INVISIBLE
                try {
                    val totalCount = response.getInt("totalCount")
                    val results = response.getJSONArray("results")
                    for (i in 0 until results.length()) {
                        val jsonObject = results.getJSONObject(i)
                        val quote = jsonObject.getString("content")
                        val author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n — $author\n")
                    }
                    adapter.notifyDataSetChanged()
                    if (listQuote.size < totalCount) {
                        currentPage++
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@ListQuotesActivity, e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int, headers: Array<Header>,
                responseString: String, throwable: Throwable
            ) {
                binding.progressBar.visibility = android.view.View.INVISIBLE
                val errorMessage = "$statusCode : ${throwable.message}"
                Toast.makeText(
                    this@ListQuotesActivity, errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadNextPage() {
        getListQuotes()
    }
}