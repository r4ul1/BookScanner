package com.example.mybookscanner


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.widget.TextView
import com.beust.klaxon.JsonArray
import java.net.InetAddress
import java.net.Socket
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.beust.klaxon.JsonObject

class BookView : AppCompatActivity() {

    private lateinit var booksRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_book)

        booksRecyclerView = findViewById(R.id.recycler_view)
        booksRecyclerView.layoutManager = LinearLayoutManager(this)
        booksRecyclerView.adapter = BooksAdapter()
    }

    inner class BooksAdapter : RecyclerView.Adapter<BooksViewHolder>() {
        private val books = getBooks()

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): BooksViewHolder {
            val itemView = layoutInflater.inflate(R.layout.book_item, parent, false)
            return BooksViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return books.size
        }

        override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
            holder.titleTextView.text = books[position]
        }
    }

    inner class BooksViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.object_text_view)
    }

    private fun getBooks(): List<String> {
        val shared_preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        var token = shared_preferences.getString("token", MainApplication.Companion.token)

        val address = InetAddress.getByName("ableytner.ddns.net")

        val user_client = Socket(address.hostAddress, 20002)
        val user_output = PrintWriter(user_client.getOutputStream(), true)
        val user_input = BufferedReader(InputStreamReader(user_client.getInputStream()))


        var user_auth = mapOf(
            "type" to "token",
            "token" to token
        )

        var user_request = mapOf (
            "request" to "GET",
            "type" to "user",
            "auth" to user_auth
        )

        var user_request_data = Klaxon().toJsonString(user_request).replace("\\", "")

        user_output.println(user_request_data)

        Thread.sleep(100)
        var user_return_data = user_input.readLine()
        var user_return_json = Parser.default().parse(java.lang.StringBuilder(user_return_data)) as JsonObject

        val data = mapOf(
            "user_id" to user_return_json.obj("data")?.int("user_id")
        )

        val auth = mapOf(
            "type" to "token",
            "token" to token
        )

        val request = mapOf (
            "request" to "GET",
            "type" to "borrow",
            "auth" to auth,
            "data" to data
        )

        val request_data = Klaxon().toJsonString(request).replace("\\", "")

        val client = Socket(address.hostAddress, 20002)
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.getInputStream()))

        output.println(request_data)

        Thread.sleep(100)
        var return_data = input.readLine()
        var return_json = Parser.default().parse(StringBuilder(return_data)) as JsonObject

        val books = mutableListOf<String>()
        val dataArray = return_json["data"] as JsonArray<JsonObject>?
        dataArray?.forEach { book ->
            book.int("book_id")?.let { getBook(it)?.let { books.add(it) } }
        }

        return books
    }

    fun getBook(book_id: Int): String? {

        val shared_preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        var token = shared_preferences.getString("token", MainApplication.Companion.token)

        val address = InetAddress.getByName("ableytner.ddns.net")

        val data = mapOf(
            "book_id" to book_id
        )

        val auth = mapOf(
            "type" to "token",
            "token" to token
        )

        val request = mapOf (
            "request" to "GET",
            "type" to "book",
            "auth" to auth,
            "data" to data
        )

        val request_data = Klaxon().toJsonString(request).replace("\\", "")

        val client = Socket(address.hostAddress, 20002)
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.getInputStream()))

        output.println(request_data)

        Thread.sleep(100)
        var return_data = input.readLine()
        var return_json = Parser.default().parse(StringBuilder(return_data)) as JsonObject

        return return_json.obj("data")?.string("title")
    }
}