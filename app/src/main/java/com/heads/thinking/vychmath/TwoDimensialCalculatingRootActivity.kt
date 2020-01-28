package com.heads.thinking.vychmath

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.heads.thinking.vychmath.custom.math.Matrix
import com.heads.thinking.vychmath.mvvm.CalculatingRootViewModel
import com.heads.thinking.vychmath.mvvm.TwoDimensialCalculatingRootViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException
import java.lang.ArithmeticException
import java.lang.Error
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NumberFormatException
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.absoluteValue


fun parseForLatex(formula : String) : String {
    var latex = ""
    for (i in 0..(formula.length - 1)) {
        try {
            when (formula[i]) {
                's' -> if (formula.substring(i, i + 3) == "sin") latex += "\\\\"
                'c' -> if (formula.substring(i, i + 3) == "cos") latex += "\\\\"
                't' -> if (formula.substring(i, i + 3) == "tan") latex += "\\\\"
                'e' -> latex += "\\\\"
                'p' -> if (formula.substring(i, i + 2) == "pi") latex += "\\\\"
            }
        } catch (e: IllegalStateException) { /*do nothinп*/ }
        latex += formula[i]
    }
    return latex
}
class TwoDimensialCalculatingRootActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var leftIntervalXET :EditText
    private lateinit var rightIntervalXET :EditText
    private lateinit var leftIntervalYET :EditText
    private lateinit var rightIntervalYET :EditText
    private lateinit var epsET :EditText
    private lateinit var f1ET :EditText
    private lateinit var f2ET :EditText
    private lateinit var f1xET :EditText
    private lateinit var f1yET :EditText
    private lateinit var f2xET :EditText
    private lateinit var f2yET :EditText

    private lateinit var answerTV : TextView
    private lateinit var nevyazkaTV : TextView
    private lateinit var iterationsTV : TextView
    private lateinit var messageTV : TextView

    private lateinit var autoDerivativeCheckBox: CheckBox

    private var methodsType = ""

    private lateinit var viewModel : TwoDimensialCalculatingRootViewModel

    fun derivative_f(f : ((v : Matrix) -> Matrix), v : Matrix) : Matrix {
        val x = v[0][0]
        val y = v[1][0]
        val prirashenie = (f(Matrix(2, 1, init = { i: Int, j: Int ->
            if(i == 0) x + 0.000001
            else y
        })))
        val fx = (prirashenie - f(v)) / 0.000001

        val fy = (f(Matrix(2, 1, init = { i: Int, j: Int ->
            if(i == 0) x
            else y + 0.000001
        })) - f(v)) / 0.000001
        return Matrix(2, 2, init = { i: Int, j: Int ->
            var result = 0.0

            if (i == 0) {
                if (j == 0) result = fx[0][0]
                if (j == 1) result = fy[0][0]
            } else if (i == 1) {
                if (j == 0) result = fx[1][0]
                if (j == 1) result = fy[1][0]
            }
            result
        })
    }

    var iterations = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_dimensial_calculating_root)

        leftIntervalXET = findViewById(R.id.leftIntervalXET)
        rightIntervalXET = findViewById(R.id.rightIntervalXET)
        leftIntervalYET = findViewById(R.id.leftIntervalYET)
        rightIntervalYET = findViewById(R.id.rightIntervalYET)
        epsET = findViewById(R.id.epsET)
        f1ET = findViewById(R.id.f1ET)
        f2ET = findViewById(R.id.f2ET)
        f1xET = findViewById(R.id.f1xET)
        f1yET = findViewById(R.id.f1yET)
        f2xET = findViewById(R.id.f2xET)
        f2yET = findViewById(R.id.f2yET)

        answerTV = findViewById(R.id.answerTV)
        nevyazkaTV = findViewById(R.id.nevyazkaTV)
        iterationsTV = findViewById(R.id.iterationsTV)
        messageTV = findViewById(R.id.messageTV)

        autoDerivativeCheckBox = findViewById(R.id.autoDerivativeCheckBox)

        methodsType = intent.getStringExtra("method")
        viewModel = ViewModelProviders.of(this).get(TwoDimensialCalculatingRootViewModel::class.java)
    }

    override fun onResume() {
        if(viewModel.methodsType != "") {
            f1xET.setText(viewModel.f1x)
            f1yET.setText(viewModel.f1y)
            f2xET.setText(viewModel.f2x)
            f2yET.setText(viewModel.f2y)
            epsET.setText(viewModel.eps)
            f1ET.setText(viewModel.f1)
            f2ET.setText(viewModel.f2)
            leftIntervalXET.setText(viewModel.leftIntervalX)
            rightIntervalXET.setText(viewModel.rightIntervalX)
            leftIntervalYET.setText(viewModel.leftIntervalY)
            rightIntervalYET.setText(viewModel.rightIntervalY)
            answerTV.text = viewModel.answer
            nevyazkaTV.text = viewModel.nevyazka
            iterationsTV.text = viewModel.iterations
            messageTV.text = viewModel.message
            methodsType = viewModel.methodsType
        }
        super.onResume()
    }

    override fun onPause() {
        viewModel.f1x = f1xET.text.toString()
        viewModel.f1y = f1yET.text.toString()
        viewModel.f2x = f2xET.text.toString()
        viewModel.f2y = f2yET.text.toString()
        viewModel.eps = epsET.text.toString()
        viewModel.f1 = f1ET.text.toString()
        viewModel.f2 = f2ET.text.toString()
        viewModel.leftIntervalX  = leftIntervalXET.text.toString()
        viewModel.rightIntervalX = rightIntervalXET.text.toString()
        viewModel.leftIntervalY  = leftIntervalYET.text.toString()
        viewModel.rightIntervalY = rightIntervalYET.text.toString()
        viewModel.answer = answerTV.text.toString()
        viewModel.nevyazka = nevyazkaTV.text.toString()
        viewModel.iterations = iterationsTV.text.toString()
        viewModel.message = messageTV.text.toString()
        viewModel.methodsType = methodsType

        super.onPause()
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.calculateBtn -> {
                try {
                    val leftX = leftIntervalXET.text.toString().toDouble()
                    val rightX = rightIntervalXET.text.toString().toDouble()
                    val leftY = leftIntervalYET.text.toString().toDouble()
                    val rightY = rightIntervalYET.text.toString().toDouble()
                    if(leftX > rightX || leftY > rightY) {
                        messageTV.text = "Неправильно заданы границы"
                        answerTV.text = "Корень: "
                        iterationsTV.text = "Количество итераций: "
                        nevyazkaTV.text = "Невязка: "
                        return
                    }
                    val eps = epsET.text.toString().toDouble()

                    val f1 = ExpressionBuilder(f1ET.text.toString()).variables("x", "y").build()
                    val f2 = ExpressionBuilder(f2ET.text.toString()).variables("x", "y").build()

                    val f : ((v : Matrix) -> Matrix) = { v : Matrix ->
                        val x = v[0][0]
                        val y = v[1][0]
                        Matrix(2, 1, init = { i: Int, j: Int ->
                            if (i == 0) {
                                f1.setVariable("x", x).setVariable("y", y).evaluate()
                            }
                            else {
                                f2.setVariable("x", x).setVariable("y", y).evaluate()
                            }
                        })
                    }
                    val der_f : ((v : Matrix) -> Matrix)? =
                            if(!autoDerivativeCheckBox.isChecked) {
                                val f1x = ExpressionBuilder(f1xET.text.toString()).variables("x", "y").build();
                                val f1y = ExpressionBuilder(f1yET.text.toString()).variables("x", "y").build();
                                val f2x = ExpressionBuilder(f2xET.text.toString()).variables("x", "y").build();
                                val f2y = ExpressionBuilder(f2yET.text.toString()).variables("x", "y").build();
                                { v: Matrix ->
                                    val x = v[0][0]
                                    val y = v[1][0]
                                    Matrix(2, 2, init = { i: Int, j: Int ->
                                        var result = 0.0
                                        if (i == 0) {
                                            if (j == 0) result = f1x.setVariable("x", x).setVariable("y", y).evaluate()
                                            if (j == 1) result = f1y.setVariable("x", x).setVariable("y", y).evaluate()
                                        } else if (i == 1) {
                                            if (j == 0) result = f2x.setVariable("x", x).setVariable("y", y).evaluate()
                                            if (j == 1) result = f2y.setVariable("x", x).setVariable("y", y).evaluate()
                                        }
                                        result
                                    })
                                }
                            } else null

                    val result = when (methodsType) {
                        "Newton" -> {
                            NewtonMethod(eps, Pair(leftX, rightX), Pair(leftY, rightY), f, der_f)
                        }
                        "ModifyNewton" -> {
                            modifyNewtonMethod(eps, Pair(leftX, rightX), Pair(leftY, rightY), f, der_f)
                        }
                        else -> null
                    }
                    if (result != null ) {
                        if(result[0][0].isFinite() && result[1][0].isFinite()) {
                            messageTV.text = ""
                            if (result[0][0] < leftX || result[0][0] > rightX || result[1][0] < leftY || result[1][0] > rightY)
                                messageTV.text = "Метод сошелся к корню вне интервала"
                            else
                                messageTV.text = "OK"
                            val nevyzkaValue = f(result).norma()
                            nevyazkaTV.text = "Невязка: $nevyzkaValue"
                            if (nevyzkaValue > 0.1) {
                                messageTV.text = "Метод не сошелся. Попробуйте другое начальное приближение"
                                answerTV.text = "Корень: "
                            } else answerTV.text = "x = ${result[0][0]}\ny = ${result[1][0]}"
                            iterationsTV.text = "Количество итераций: $iterations"
                        } else {
                            messageTV.text = "Ответ не удалось найти.\nПопробуйте изменить границы."
                            answerTV.text = "Корень: неизвестно"
                            iterationsTV.text = "Количество итераций: $iterations"
                            nevyazkaTV.text = "Невязка: неизвестно"
                        }
                    }
                    else {
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
                val function1 = f1ET.text.toString()
                val function2 = f2ET.text.toString()
                startActivity(Intent(this@TwoDimensialCalculatingRootActivity, AlternativeGraphActivity::class.java).apply {
                    this.putExtra("numberOfPlot", 2)
                    this.putExtra("function1", parseForLatex(function1 + "=0"))
                    this.putExtra("function2", parseForLatex(function2 + "=0"))
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.calculationg_root_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.backBtn -> {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun NewtonMethod(eps : Double, intervalX : Pair<Double, Double>, intervalY : Pair<Double, Double>,
                     f : (v : Matrix) -> Matrix, der_f : ((v : Matrix) -> Matrix)?) : Matrix? {
        iterations = 0
        val derivative_f = if(der_f == null) {v:Matrix -> derivative_f(f, v)} else der_f
        val startingX = Matrix(2, 1, init = { i: Int, j: Int ->
            if (i == 0) (intervalX.second + intervalX.first) / 2
            else (intervalY.second + intervalY.first) / 2
        })
        var prevX = startingX
        var nextX : Matrix = startingX
        do {
            if(iterations == 100) return null
            iterations++
            prevX = nextX
            nextX = prevX - derivative_f(prevX).inverseMatrix2x2() * f(prevX)
        } while ((nextX.norma() - prevX.norma()).absoluteValue > eps)
        return nextX
    }

    fun modifyNewtonMethod(eps : Double, intervalX : Pair<Double, Double>, intervalY : Pair<Double, Double>,
                           f : (v : Matrix) -> Matrix, der_f : ((v : Matrix) -> Matrix)?) : Matrix? {
        iterations = 0
        val derivative_f = if(der_f == null) {v:Matrix -> derivative_f(f, v)} else der_f
        val startingX = Matrix(2, 1, init = { i: Int, j: Int ->
            if (i == 0) ((intervalX.second + intervalX.first) / 2)
            else (intervalY.second + intervalY.first) / 2
        })
        var prevX = startingX
        var nextX : Matrix = startingX
        val invDerivative = derivative_f(startingX).inverseMatrix2x2()
        do {
            if(iterations == 100) return null
            iterations++
            prevX = nextX
            nextX = prevX - invDerivative * f(prevX)
        } while ((nextX.norma() - prevX.norma()).absoluteValue > eps)
        return nextX
    }
}
