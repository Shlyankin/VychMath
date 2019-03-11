package com.heads.thinking.vychmath

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.dichotomyBtn -> {
                startActivity(Intent(this, CalculatingRootActivity::class.java).apply {
                    this.putExtra("method", "dichotomy")
                })
            }
            R.id.hybridBtn -> {
                startActivity(Intent(this, CalculatingRootActivity::class.java).apply {
                    this.putExtra("method", "hybrid")
                })
            }
            R.id.newtonBtn -> {
                startActivity(Intent(this, TwoDimensialCalculatingRootActivity::class.java).apply {
                    this.putExtra("method", "Newton")
                })
            }
            R.id.modifyNewtonBtn -> {
                startActivity(Intent(this, TwoDimensialCalculatingRootActivity::class.java).apply {
                    this.putExtra("method", "ModifyNewton")
                })
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}
