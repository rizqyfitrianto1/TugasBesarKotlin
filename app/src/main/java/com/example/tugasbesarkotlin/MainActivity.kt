package com.example.tugasbesarkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    internal lateinit var btn_logout: ImageView
    internal lateinit var txt_id: TextView
    internal lateinit var txt_nim: TextView
    internal lateinit var id: String
    internal lateinit var nim: String
    internal lateinit var sharedpreferences: SharedPreferences

    //a list to store all the products
    internal lateinit var productList: MutableList<User>

    //the recyclerview
    internal lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_id = findViewById<View>(R.id.txt_id) as TextView
        txt_nim = findViewById<View>(R.id.txt_nim) as TextView
        btn_logout = findViewById<View>(R.id.btn_logout) as ImageView

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE)

        id = intent.getStringExtra(TAG_ID)
        nim = intent.getStringExtra(TAG_NIM)

        txt_id.text = "ID : $id"
        txt_nim.text = "NIM: $nim"

        btn_logout.setOnClickListener {
            // TODO Auto-generated method stub
            // update login session ke FALSE dan mengosongkan nilai id dan username
            val editor = sharedpreferences.edit()
            editor.putBoolean(Login.session_status, false)
            editor.putString(TAG_ID, null)
            editor.putString(TAG_NIM, null)
            editor.commit()

            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
        }

        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.contact_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //initializing the productlist
        productList = ArrayList()

        //this method will fetch and parse json
        //to display it in recyclerview
        loadProducts()

    }

    private fun loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        val stringRequest = StringRequest(
            Request.Method.GET, URL_PRODUCTS,
            Response.Listener { response ->
                try {
                    //converting the string to json array object
                    val array = JSONArray(response)

                    //traversing through all the object
                    for (i in 0 until array.length()) {

                        //getting product object from json array
                        val product = array.getJSONObject(i)

                        //adding the product to product list
                        productList.add(User(
                            product.getInt("id"),
                            product.getString("nama"),
                            product.getString("nim"),
                            product.getString("images")
                        ))
                    }

                    //creating adapter object and setting it to recyclerview
                    val adapter = RecyclerViewAdapter(this@MainActivity, productList)
                    recyclerView.adapter = adapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { })

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest)
    }

    companion object {

        val TAG_ID = "id"
        val TAG_NIM = "nim"

        private val URL_PRODUCTS = "http://172.16.10.84/tugasandroid/Api.php"
    }
}
