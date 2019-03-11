package com.heads.thinking.vychmath

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class AlternativeGraphActivity : AppCompatActivity() {

    private lateinit var webView : WebView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternative_graph)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/desmos.HTML")//"https://www.desmos.com/calculator")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                val size = intent.getIntExtra("numberOfPlot",1)
                for(i in 1..size) {
                    val latex = intent.getStringExtra("function" + i)
                    webView.loadUrl("javascript:plot(\"function$i\", \"$latex\");")
                }
            }
        }
    }
}