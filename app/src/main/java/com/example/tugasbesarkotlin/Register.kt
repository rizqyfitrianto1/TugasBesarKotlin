package com.example.tugasbesarkotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Register : AppCompatActivity() {

    lateinit var pDialog: ProgressDialog
    lateinit var btn_register: Button
    lateinit var btn_login: TextView
    lateinit var textView: TextView
    lateinit var txt_nama: EditText
    lateinit var txt_nim: EditText
    lateinit var txt_password: EditText
    lateinit var txt_confirm_password: EditText
    internal lateinit var intent: Intent

    internal var success: Int = 0
    internal lateinit var conMgr: ConnectivityManager

    private val url = Server.URL + "register.php"

    internal var tag_json_obj = "json_obj_req"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

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

        btn_login = findViewById<View>(R.id.btn_login) as TextView
        btn_register = findViewById<View>(R.id.btn_register) as Button
        txt_nama = findViewById<View>(R.id.txt_nama) as EditText
        txt_nim = findViewById<View>(R.id.txt_nim) as EditText
        txt_password = findViewById<View>(R.id.txt_password) as EditText
        txt_confirm_password = findViewById<View>(R.id.txt_confirm_password) as EditText
        textView = findViewById<View>(R.id.TextView2) as TextView

        val typeface = Typeface.createFromAsset(assets, "fonts/italianno.otf")
        textView.typeface = typeface

        btn_login.setOnClickListener {
            // TODO Auto-generated method stub
            intent = Intent(this@Register, Login::class.java)
            finish()
            startActivity(intent)
        }

        btn_register.setOnClickListener {
            // TODO Auto-generated method stub
            val nama = txt_nama.text.toString()
            val nim = txt_nim.text.toString()
            val password = txt_password.text.toString()
            val confirm_password = txt_confirm_password.text.toString()

            if (conMgr.activeNetworkInfo != null
                && conMgr.activeNetworkInfo.isAvailable
                && conMgr.activeNetworkInfo.isConnected) {
                checkRegister(nama, nim, password, confirm_password)

                startActivity(Intent(this, Login::class.java))
            } else {
                Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkRegister(nama: String, nim: String, password: String, confirm_password: String) {
        pDialog = ProgressDialog(this)
        pDialog.setCancelable(false)
        pDialog.setMessage("Register ...")
        showDialog()

        val strReq = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Log.e(TAG, "Register Response: $response")
            hideDialog()

            try {
                val jObj = JSONObject(response)
                success = jObj.getInt(TAG_SUCCESS)

                // Check for error node in json
                if (success == 1) {

                    Log.e("Successfully Register!", jObj.toString())

                    Toast.makeText(applicationContext,
                        jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show()

                    txt_nama.setText("")
                    txt_nim.setText("")
                    txt_password.setText("")
                    txt_confirm_password.setText("")

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
                params["nama"] = nama
                params["nim"] = nim
                params["password"] = password
                params["confirm_password"] = confirm_password

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

    override fun onBackPressed() {
        intent = Intent(this@Register, Login::class.java)
        finish()
        startActivity(intent)
    }

    companion object {

        private val TAG = Register::class.java!!.getSimpleName()

        private val TAG_SUCCESS = "success"
        private val TAG_MESSAGE = "message"
    }
}


