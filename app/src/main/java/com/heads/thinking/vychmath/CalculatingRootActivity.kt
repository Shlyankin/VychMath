package com.heads.thinking.vychmath


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.heads.thinking.vychmath.mvvm.CalculatingRootViewModel
import kotlinx.android.synthetic.main.activity_calculating_root.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException
import java.lang.ArithmeticException
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import kotlin.math.absoluteValue

class CalculatingRootActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel : CalculatingRootViewModel

    private var iterations = 0

    private var methodsType = ""

    private lateinit var funcET: EditText
    private lateinit var leftIntervalET: EditText
    private lateinit var rightIntervalET: EditText
    private lateinit var epsET: EditText
    private lateinit var startingXET: EditText
    private lateinit var derivativeET: EditText

    private lateinit var answerTV : TextView
    private lateinit var nevyazkaTV : TextView
    private lateinit var iterationsTV : TextView
    private lateinit var messageTV : TextView

    private lateinit var autoDerivativeCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculating_root)

        funcET = findViewById(R.id.funcET)
        leftIntervalET = findViewById(R.id.leftIntervalET)
        rightIntervalET = findViewById(R.id.rightIntervalET)
        epsET = findViewById(R.id.epsET)
        startingXET = findViewById(R.id.startingXET)
        derivativeET = findViewById(R.id.derivativeET)

        answerTV = findViewById(R.id.answerTV)
        nevyazkaTV = findViewById(R.id.nevyazkaTV)
        iterationsTV = findViewById(R.id.iterationsTV)
        messageTV = findViewById(R.id.messageTV)

        autoDerivativeCheckBox = findViewById(R.id.autoDerivativeCheckBox)

        methodsType = this.intent.getStringExtra("method")
        if(methodsType == "dichotomy") {
            startingXET.visibility = View.GONE
            derivativeET.visibility = View.GONE
            autoDerivativeCheckBox.visibility = View.GONE
        }

        viewModel = ViewModelProviders.of(this).get(CalculatingRootViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.calculationg_root_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.backBtn -> {
                startActivity(Intent(this, MainActivity::class.java)
                        .apply {
                            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        if(viewModel.message != "")
            messageTV.text = viewModel.message
        if(viewModel.answer != "") {
            answerTV.text = viewModel.answer
            nevyazkaTV.text = viewModel.nevyazka
            iterationsTV.text = viewModel.iterations
        }
        if(methodsType == "") methodsType = viewModel.methodsType
        if(viewModel.function != "") funcET.setText(viewModel.function)
        epsET.setText( viewModel.eps)
        leftIntervalET.setText(viewModel.leftInterval)
        rightIntervalET.setText(viewModel.rightInterval)
        super.onResume()
    }

    override fun onPause() {
        viewModel.answer = answerTV.text.toString()
        viewModel.message = messageTV.text.toString()
        viewModel.iterations = iterationsTV.text.toString()
        viewModel.nevyazka = nevyazkaTV.text.toString()
        viewModel.methodsType = methodsType
        viewModel.function = funcET.text.toString()
        viewModel.eps = epsET.text.toString()
        viewModel.leftInterval = leftIntervalET.text.toString()
        viewModel.rightInterval = rightIntervalET.text.toString()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.calculateBtn -> {
                try {
                    val expression = ExpressionBuilder(funcET.text.toString())
                            .variable("x")
                            .build()
                    val leftInterval = leftIntervalET.text.toString().toDouble()
                    val rightInterval = rightIntervalET.text.toString().toDouble()
                    if(leftInterval > rightInterval) {
                        messageTV.text = "Неправильно заданы границы"
                        answerTV.text = "Корень: "
                        iterationsTV.text = "Количество итераций: "
                        nevyazkaTV.text = "Невязка: "
                        return
                    }
                    val eps = epsET.text.toString().toDouble()
                    val f : (x: Double) -> Double = {x: Double -> expression.setVariable("x", x).evaluate() }
                    val result : Double? = when(methodsType) {
                        "hybrid" -> {
                            val startingX = startingXET.text.toString().toDouble()
                            val der_f : ((x : Double) -> Double)? =
                                if(!autoDerivativeCheckBox.isChecked) {
                                    {x: Double ->
                                        ExpressionBuilder(derivativeET.text.toString())
                                                .variable("x")
                                                .build()
                                                .setVariable("x", x)
                                                .evaluate() }
                                } else null
                            hybridMethod(startingX, eps, f, der_f)
                        }
                        "dichotomy" -> {
                            dichotomy(eps, leftInterval, rightInterval, f)
                        }
                        else -> null
                    }
                    if (result != null) {
                        if(result.isFinite() && result.isFinite()) {
                            if (result < leftInterval || result > rightInterval)
                                messageTV.text = "Метод сошелся к корню вне интервала"
                            else
                                messageTV.text = "OK"
                            val nevyzkaValue = expression.setVariable("x", result).evaluate().absoluteValue
                            nevyazkaTV.text = "Невязка: " + nevyzkaValue.toString()
                            if (nevyzkaValue > 0.1) {
                                messageTV.text = "Метод не сошелся. Попробуйте другое начальное приближение"
                                answerTV.text = "Корень: "
                            } else answerTV.text = "Корень: " + result.toString()
                            iterationsTV.text = "Количество итераций: " + iterations.toString()
                        } else {
                            messageTV.text = "Ответ не удалось найти.\nПопробуйте изменить границы."
                            answerTV.text = "Корень: неизвестно"
                            iterationsTV.text = "Количество итераций: $iterations"
                            nevyazkaTV.text = "Невязка: неизвестно"
                        }
                    } else {
                        messageTV.text = "Метод не сошелся за 100 итераций!"
                        answerTV.text = "Корень: неизвестно"
                        iterationsTV.text = "Количество итераций: >100"
                        nevyazkaTV.text = "Невязка: неизвестно"
                    }
                } catch (e : ArithmeticException) {
                    messageTV.text = "Функция имеет разрыв в данном интервале.\nИзмените интервал."
                    answerTV.text = "Корень: неизвестно"
                    iterationsTV.text = "Количество итераций: неизвестно"
                    nevyazkaTV.text = "Невязка: неизвестно"
                } catch (e : NumberFormatException) {
                    messageTV.text = "Вы не заполнили все поля!"
                    answerTV.text = "Корень: неизвестно"
                    iterationsTV.text = "Количество итераций: неизвестно"
                    nevyazkaTV.text = "Невязка: неизвестно"
                } catch (e : UnknownFunctionOrVariableException) {
                    messageTV.text = "Неправильно задана функция!"
                    answerTV.text = "Корень: неизвестно"
                    iterationsTV.text = "Количество итераций: неизвестно"
                    nevyazkaTV.text = "Невязка: неизвестно"
                } catch (e : IllegalArgumentException) {
                    messageTV.text = "Неправильно задана функция!"
                    answerTV.text = "Корень: неизвестно"
                    iterationsTV.text = "Количество итераций: неизвестно"
                    nevyazkaTV.text = "Невязка: неизвестно"
                }
            }
            R.id.plotBtn -> {
                startActivity(Intent(this@CalculatingRootActivity, AlternativeGraphActivity::class.java).apply {
                    this.putExtra("numberOfPlot", 1)
                    this.putExtra("function1", parseForLatex("y = " + funcET.text.toString()))
                })
            }
        }
    }

    private fun hybridMethod(startingX : Double, eps : Double,
                             f : (x: Double) -> Double, der_f : ((x: Double) -> Double)? = null) : Double? {
        iterations = 0
        var derivative = der_f ?: {x : Double -> (f(x + 0.000001) - f(x)) / 0.000001}
        var nextX = startingX
        var prevX = startingX
        do{
            prevX = nextX
            if(iterations++ > 100) return null
            val f_prevX = f(prevX)
            nextX = prevX - (f_prevX)/derivative(prevX)
            while(f(nextX).absoluteValue > f(prevX).absoluteValue) {
                nextX = 0.5 *(nextX + prevX)
            }
        } while ((nextX - prevX).absoluteValue > eps)
        return nextX
    }

    private fun dichotomy(eps : Double, leftInterval : Double, rightInterval : Double, f : (x : Double) -> Double) : Double? {
        iterations = 0
        var x : Double? = null
        var left = leftInterval
        var right = rightInterval
        while (iterations < 100) {
            iterations++
            val middle = (right + left) / 2
            if((right - left).absoluteValue <= 2*eps) {
                x = middle
                break
            }
            val fvalue_middle = f(middle)
            if (fvalue_middle.absoluteValue < eps) {
                x = middle
                break
            }
            if (fvalue_middle * f(left) < 0) {
                right = middle
            } else if (fvalue_middle * f(right) < 0) {
                left = middle
            }
        }
        return x
    }
}
