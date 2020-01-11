package com.example.tugasbesarkotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Login : AppCompatActivity() {
    internal lateinit var pDialog: ProgressDialog
    internal lateinit var btn_login: Button
    internal lateinit var btn_register: TextView
    internal lateinit var textView: TextView
    internal lateinit var txt_nim: EditText
    internal lateinit var txt_password: EditText
    internal lateinit var intent: Intent

    internal var success: Int = 0
    internal lateinit var conMgr: ConnectivityManager

    private val url = Server.URL + "login.php"

    internal var tag_json_obj = "json_obj_req"

    internal lateinit var sharedpreferences: SharedPreferences
    internal var session: Boolean? = false
    internal var id: String? = null
    internal var nim: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        run {
            if (conMgr.activeNetworkInfo != null
                && conMgr.activeNetworkInfo.isAvailable
                && conMgr.activeNetworkInfo.isConnected) {
            } else {
                Toast.makeText(applicationContext, "No Internet Connection",
                    Toast.LENGTH_LONG).show()
            }
        }

        btn_login = findViewById<View>(R.id.btn_login) as Button
        btn_register = findViewById<View>(R.id.btn_register) as TextView
        txt_nim = findViewById<View>(R.id.txt_nim) as EditText
        txt_password = findViewById<View>(R.id.txt_password) as EditText
        textView = findViewById<View>(R.id.TextView2) as TextView

        val typeface = Typeface.createFromAsset(assets, "fonts/italianno.otf")
        textView.typeface = typeface

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE)
        session = sharedpreferences.getBoolean(session_status, false)
        id = sharedpreferences.getString(TAG_ID, null)
        nim = sharedpreferences.getString(TAG_NIM, null)

        if (session!!) {
            val intent = Intent(this@Login, MainActivity::class.java)
            intent.putExtra(TAG_ID, id)
            intent.putExtra(TAG_NIM, nim)
            finish()
            startActivity(intent)
        }


        btn_login.setOnClickListener {
            // TODO Auto-generated method stub
            val nim = txt_nim.text.toString()
            val password = txt_password.text.toString()

            // mengecek kolom yang kosong
            if (nim.trim { it <= ' ' }.isNotEmpty() && password.trim { it <= ' ' }.isNotEmpty()) {
                if (conMgr.activeNetworkInfo != null
                    && conMgr.activeNetworkInfo.isAvailable
                    && conMgr.activeNetworkInfo.isConnected) {
                    checkLogin(nim, password)
                } else {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_LONG).show()
                }
            } else {
                // Prompt user to enter credentials
                Toast.makeText(applicationContext, "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show()
            }
        }

        btn_register.setOnClickListener {
            // TODO Auto-generated method stub
            intent = Intent(this@Login, Register::class.java)
            finish()
            startActivity(intent)
        }


    }

    private fun checkLogin(nim: String, password: String) {
        pDialog = ProgressDialog(this)
        pDialog.setCancelable(false)
        pDialog.setMessage("Logging in ...")
        showDialog()

        val strReq = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            Log.e(TAG, "Login Response: $response")
            hideDialog()

            try {
                val jObj = JSONObject(response)
                success = jObj.getInt(TAG_SUCCESS)

                // Check for error node in json
                if (success == 1) {
                    val nim = jObj.getString(TAG_NIM)
                    val id = jObj.getString(TAG_ID)

                    Log.e("Successfully Login!", jObj.toString())

                    Toast.makeText(applicationContext, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show()

                    // menyimpan login ke session
                    val editor = sharedpreferences.edit()
                    editor.putBoolean(session_status, true)
                    editor.putString(TAG_ID, id)
                    editor.putString(TAG_NIM, nim)
                    editor.commit()

                    // Memanggil main activity
                    val intent = Intent(this@Login, MainActivity::class.java)
                    intent.putExtra(TAG_ID, id)
                    intent.putExtra(TAG_NIM, nim)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext,
                        jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show()

                }
            } catch (e: JSONException) {
                // JSON error
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            Log.e(TAG, "Login Error: " + error.message)
            Toast.makeText(applicationContext,
                error.message, Toast.LENGTH_LONG).show()

            hideDialog()
        }) {

            override fun getParams(): Map<String, String> {
                // Posting parameters to login url
                val params = HashMap<String, String>()
                params["nim"] = nim
                params["password"] = password

                return params
            }

        }

        // Adding request to request queue
        AppController.instance?.addToRequestQueue(strReq, tag_json_obj)
    }

    private fun showDialog() {
        if (!pDialog.isShowing)
            pDialog.show()
    }

    private fun hideDialog() {
        if (pDialog.isShowing)
            pDialog.dismiss()
    }

    companion object {

        private val TAG = Login::class.java!!.getSimpleName()

        private val TAG_SUCCESS = "success"
        private val TAG_MESSAGE = "message"

        val TAG_NIM = "nim"
        val TAG_ID = "id"
        val my_shared_preferences = "my_shared_preferences"
        val session_status = "session_status"
    }

}