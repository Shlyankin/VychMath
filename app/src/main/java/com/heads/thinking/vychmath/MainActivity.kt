package com.heads.thinking.vychmath

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.infoBtn -> {
                val builder =  AlertDialog.Builder(this);
                builder.setTitle("О приложении")
                        .setMessage("Привет, меня зовут Шлянкин Николай.\n" +
                                "Я разрабатываю приложения для операционной системы Android.\n\n" +
                                "Это приложение разработано в учебных целях и не является коммерческим продуктом.\n" +
                                "В приложении могут наблюдаться баги и вылеты.\n" +
                                "Надеюсь, этого не приозойдет и вы будете испытывать только положительные эмоции от моего продукта.\n" +
                                "Отзывы/предложения можете писать мне на почту: shlyankin123@gmail.com .\n\n" +
                                "Спасибо, что используете VychMath!")
                        .setCancelable(false)
                        .setNegativeButton("Закрыть",{ dialogInterface, i -> dialogInterface.cancel()})
                val alert = builder.create()
                alert.show();
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}
