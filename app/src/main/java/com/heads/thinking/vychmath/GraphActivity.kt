package com.heads.thinking.vychmath

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.heads.thinking.vychmath.mvvm.GraphActivityViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException
import java.lang.ArithmeticException
import java.lang.IllegalArgumentException
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine

class GraphActivity : AppCompatActivity(), View.OnClickListener {

    private var expressions : ArrayList<Expression> = ArrayList()
    private var stringExpressions : ArrayList<String> = ArrayList()

    private lateinit var graphView: GraphView
    private lateinit var leftIntervalET : EditText
    private lateinit var rightIntervalET : EditText
    private lateinit var funcET : EditText
    private lateinit var log : TextView
    private lateinit var argSpinner: Spinner

    private lateinit var viewModel: GraphActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        graphView = findViewById(R.id.graph)
        leftIntervalET = findViewById(R.id.leftIntervalET)
        rightIntervalET = findViewById(R.id.rightIntervalET)
        funcET = findViewById(R.id.funcET)
        log = findViewById(R.id.log)
        argSpinner = findViewById(R.id.argSpinner)

        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMaxX(5.0)
        graphView.viewport.setMinX(-5.0)
        graphView.viewport.setMaxY(10.0)
        graphView.viewport.setMinY(-10.0)
        graphView.viewport.setScalable(true)

        graphView.legendRenderer.margin = 5
        graphView.legendRenderer.width = 400
        graphView.legendRenderer.isVisible = true
        graphView.legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM

        viewModel = ViewModelProviders.of(this).get(GraphActivityViewModel::class.java)

        val countsFunction = this.intent.getIntExtra("countFunction", 0)
        for(i in 1..countsFunction) {
            val functionString = this.intent.getStringExtra("function" + i)
            val xIsArg = functionString[0] == 'y'
            if(xIsArg)
                expressions.add(ExpressionBuilder(functionString.substring(2)).variable("x").build())
            else
                expressions.add(ExpressionBuilder(functionString.substring(2)).variable("y").build())
            stringExpressions.add(functionString)
            drawFunction(-5.0, 5.0, expressions[i-1], stringExpressions[i-1], xIsArg)
        }
    }

    override fun onResume() {
        if(expressions.isEmpty()) {
            expressions = viewModel.expressions
            stringExpressions = viewModel.stringExpressions
        }
        funcET.setText(viewModel.function)
        leftIntervalET.setText(viewModel.leftInterval)
        rightIntervalET.setText(viewModel.rightInterval)
        onClick(findViewById(R.id.redrawBtn))
        super.onResume()
    }

    override fun onPause() {
        viewModel.leftInterval = leftIntervalET.text.toString()
        viewModel.rightInterval = rightIntervalET.text.toString()
        viewModel.function = funcET.text.toString()
        viewModel.expressions = expressions
        viewModel.stringExpressions = stringExpressions
        super.onPause()
    }



    private fun drawFunction(left : Double, right : Double, expression : Expression, stringExpression : String, xIsArg : Boolean = true) {
        val size = ((right - left) * 100).toInt()
        if(right < left)
            log.text = "Неправильно заданы границы!"
        else {
            val delta = (right - left) / size
            val result = ArrayList<Pair<Double, Double>>()
            var i = 0
            while(i < size) {
                val current = left + delta * i
                try {
                    val value = if(xIsArg) expression.setVariable("x", current).evaluate()
                    else expression.setVariable("y", current).evaluate()
                    result.add(Pair(value, current))
                } catch(e : ArithmeticException) {
                    Double.MAX_VALUE
                }
                i++
            }
            if(!xIsArg)
                result.sortBy { it.first }
            var points = Array<DataPoint>(size, {
                    if (xIsArg)
                        DataPoint(result[it].second, result[it].first)
                    else
                        DataPoint(result[it].first, result[it].second)
            })
            val series = LineGraphSeries<DataPoint>(points)
            series.color = getRandomColor()
            series.title = stringExpression
            graphView.addSeries(series)
            /*
            var points = Array<DataPoint>(size, {
                val current = left + delta * it
                try {
                    if(xIsArg)
                    DataPoint(current, expression.setVariable("x", current).evaluate())
                    else
                        DataPoint( expression.setVariable("y", current).evaluate(), current)
                } catch(e : ArithmeticException) {
                    if(xIsArg)
                        log.text = log.text.toString() + "В точке x=$current разрыв функции"
                    else
                        log.text = log.text.toString() + "В точке y=$current разрыв функции"
                    DataPoint(current, Double.MAX_VALUE)
                }
            })
            val series = LineGraphSeries<DataPoint>(points)
            series.color = getRandomColor()
            series.title = stringExpression
            graphView.addSeries(series)
            */

        }
    }

    fun getRandomColor() : Int =
        when((Math.random()*100).toInt()%3) {
            0 -> Color.rgb((Math.random()*1000).toInt()%40, (Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%256)
            1 -> Color.rgb((Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%40, (Math.random()*1000).toInt()%255)
            2 -> Color.rgb((Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%40)
            else -> Color.rgb((Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%256, (Math.random()*1000).toInt()%256)
        }

    override fun onClick(view: View?) {
        log.text = ""
        when(view!!.id) {
            R.id.redrawBtn -> {
                var left : Double
                var right : Double
                try {
                    left = leftIntervalET.text.toString().toDouble()
                    right = rightIntervalET.text.toString().toDouble()
                } catch (e : IllegalArgumentException) {
                    log.text = "Установлены значения по умолчанию [-5;5] для границ"
                    left = -5.0
                    right = 5.0
                }
                graphView.removeAllSeries()
                var i = 0
                while(i < expressions.size) {
                    try {
                        drawFunction(left, right, expressions[i], stringExpressions[i])
                    } catch (e : UnknownFunctionOrVariableException) {
                        log.text = "Неправильно задана функция!"
                    }
                    i++
                }
            }
            R.id.addFuncBtn -> {
                try {
                    var left = graphView.viewport.getMinX(true)
                    var right = graphView.viewport.getMaxX(true)
                    if(left == 0.0 && right == 0.0) {
                        left = -5.0
                        right = 5.0
                    }
                    val xIsArg = argSpinner.selectedItem.toString()[0] == 'y'
                    val expression = if(xIsArg)
                        ExpressionBuilder(funcET.text.toString()).variables("x").build()
                    else
                        ExpressionBuilder(funcET.text.toString()).variables("y").build()
                    expressions.add(expression)
                    stringExpressions.add(argSpinner.selectedItem.toString() + funcET.text.toString())
                    drawFunction(left, right, expression, funcET.text.toString(), xIsArg)
                } catch (e : UnknownFunctionOrVariableException) {
                    log.text = "Неправильно задана функция!"
                } catch (e : IllegalArgumentException) {
                    log.text = "Неправильно задана функция!"
                }
            }
            R.id.clearBtn -> {
                expressions.clear()
                stringExpressions.clear()
                graphView.removeAllSeries()
            }
        }
    }
}
