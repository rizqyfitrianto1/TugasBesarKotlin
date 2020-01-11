package com.example.tugasbesarkotlin

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class Splashscreen  : AppCompatActivity() {
    private var iv: ImageView? = null
    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        iv = findViewById<View>(R.id.logogundar) as ImageView?
        val myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition)
        iv!!.startAnimation(myanim)
        val i = Intent(this@Splashscreen, Login::class.java)
        val timer = object : Thread() {
            override fun run() {
                try {
                    sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(i)
                    finish()
                }
            }
        }
        timer.start()

        //Slogan Universitas Gunadarma
        textView = findViewById<View>(R.id.slogan) as TextView?
        val typeface = Typeface.createFromAsset(assets, "fonts/AlexBrush.ttf")
        textView!!.typeface = typeface

        textView!!.startAnimation(myanim)
        val intent = Intent(this@Splashscreen, Login::class.java)
        val timers = object : Thread() {
            override fun run() {
                try {
                    sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(intent)
                    finish()
                }
            }
        }
        timers.start()
    }
}