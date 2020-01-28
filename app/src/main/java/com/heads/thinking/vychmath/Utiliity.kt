package com.heads.thinking.vychmath

import kotlin.math.absoluteValue
import kotlin.math.sin

fun function(x : Double) : Double = Math.PI * sin(8*x) /x + x*x

val tabulation : ArrayList<Pair<Double, Double>> = ArrayList()

fun Lagrange(tabulation : ArrayList<Pair<Double, Double>>) : ((x : Double) -> Double) {
    return { x: Double ->
        var result = 0.0
        for (i in 0 until tabulation.size) {
            var chislitel = 1.0
            var znamenatel = 1.0
            for (j in 0 until tabulation.size) {
                if (i != j) {
                    chislitel *= x - tabulation[j].first
                    znamenatel *= tabulation[i].first - tabulation[j].first
                }
            }
            result += tabulation[i].second * chislitel / znamenatel
        }
        result
    }
}

fun createTable(interval : ArrayList<Pair<Double, Double>>) : Array<Array<Double>> {
    val size = interval.size
    val table = Array<Array<Double>>(size + 1) { i : Int ->
        if(i == 0 || i == 1)
            Array(size , {0.0})
        else
            Array(size - i + 1, {0.0})
    }
    for(j in 0 until size) {
        table[0][j] = interval[j].first
        table[1][j] = interval[j].second
    }
    for(i in 2 until size + 1)
        for(j in 0 until size - i + 1)
            table[i][j] = (table[i-1][j + 1] - table[i-1][j]) / (table[0][j + i - 1] - table[0][j])

    return table
}

fun Newton(tabulation: ArrayList<Pair<Double, Double>>, table: Array<Array<Double>>) : ((x : Double) -> Double) {
    return { x : Double ->
        var result = 0.0
        for(i in 1 until table.size) {
            var delta = 1.0
            for(j in 0 until (i - 1)) {
                delta *= x - table[0][j]
            }
            result += table[i][0] * delta
        }
        result
    }
}

fun linearSpline(tabulation: ArrayList<Pair<Double, Double>>) : ((x : Double) -> Double) = {   x : Double ->
    var interval : IntRange? = null
    for(i in 1 until tabulation.size) {
        if(x >= tabulation[i - 1].first && x <= tabulation[i].first) {
            if(i == (tabulation.size - 1)) interval = (tabulation.size - 2)..(tabulation.size - 1)
            else if(i == 0) interval = null
            else interval = (i - 1)..i
            break
        }
    }
    if(interval == null) throw ArithmeticException("Аргумент вне интервала")
    val first = interval.first
    val last = interval.last

    -((tabulation[first].second - tabulation[last].second) * x +
            (tabulation[first].first * tabulation[last].second
                    - tabulation[last].first * tabulation[first].second)) / (tabulation[last].first - tabulation[first].first)
}


fun getMatrixForParabolaSpline(h : Double, y : Array<Double>) : Array<Array<Double>> {
    val h2 = Math.pow(h, 2.0)
    return arrayOf(
            doubleArrayOf(1.0,  0.0,     0.0,    0.0,    0.0,    0.0,    0.0,    0.0,   0.0,    y[0]).toTypedArray(),
            doubleArrayOf(1.0,  h,       h2,     0.0,    0.0,    0.0,    0.0,    0.0,   0.0,    y[1]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     0.0,    1.0,    0.0,    0.0,    0.0,    0.0,   0.0,    y[1]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     0.0,    1.0,    h,      h2,     0.0,    0.0,   0.0,    y[2]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     0.0,    0.0,    0.0,    0.0,    1.0,    0.0,   0.0,    y[2]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     0.0,    0.0,    0.0,    0.0,    1.0,    h,     h2,     y[3]).toTypedArray(),
            doubleArrayOf(0.0,  1.0,     2 * h,  0.0,    -1.0,   0.0,    0.0,    0.0,   0.0,     0.0).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     0.0,    0.0,    1.0,    2 * h,  0.0,    -1.0,  0.0,     0.0).toTypedArray(),
            doubleArrayOf(0.0,  0.0,     1.0,    0.0,    0.0,    0.0,    0.0,    0.0,   0.0,     0.0).toTypedArray()
    )
}


fun getMatrixForCubicSpline(h : Double, y : Array<Double>) : Array<Array<Double>> {
    val h2 = Math.pow(h, 2.0)
    val h3 = Math.pow(h, 3.0)
    return arrayOf(
            doubleArrayOf(1.0,  0.0,  0.0, 0.0,     0.0, 0.0,   0.0,    0.0,    0.0, 0.0,   0.0,    0.0,    y[0]).toTypedArray(),
            doubleArrayOf(1.0,  h,    h2,  h3,      0.0, 0.0,   0.0,    0.0,    0.0, 0.0,   0.0,    0.0,    y[1]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     1.0, 0.0,   0.0,    0.0,    0.0, 0.0,   0.0,    0.0,    y[1]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     1.0, h,     h2,     h3,     0.0, 0.0,   0.0,    0.0,    y[2]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     0.0, 0.0,   0.0,    0.0,    1.0, 0.0,   0.0,    0.0,    y[2]).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     0.0, 0.0,   0.0,    0.0,    1.0, h,     h2,     h3,     y[3]).toTypedArray(),
            doubleArrayOf(0.0,  1.0,  2*h, 3 * h2,  0.0, -1.0,   0.0,   0.0,    0.0, 0.0,   0.0,    0.0,    0.0 ).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  1.0, 3 * h,   0.0, 0.0,   -1.0,   0.0,    0.0, 0.0,   0.0,    0.0,    0.0 ).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     0.0, 1.0,   2 * h,  3 * h2, 0.0, -1.0,  0.0,    0.0,    0.0 ).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     0.0, 0.0,   1.0,    3 * h,  0.0, 0.0,   -1.0,   0.0,    0.0 ).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  0.0, 0.0,     0.0, 0.0,   0.0,    0.0,    0.0, 0.0,   1.0,    3 * h,  0.0 ).toTypedArray(),
            doubleArrayOf(0.0,  0.0,  1.0, 0.0,     0.0, 0.0,   0.0,    0.0,    0.0, 0.0,   0.0,    0.0,    0.0 ).toTypedArray()
    )
}

fun createParabolaSpline(h : Double,x : Array<Double>, y : Array<Double>) : ((x : Double) -> Double) {
    val matrix = getMatrixForParabolaSpline(h, y)
    val coefficient = gauss(matrix) ?: throw ArithmeticException("Не удалось найти коэффициенты")
    return { arg : Double ->
        var currentPolynom : Int? = null
        for (i in 0 until tabulation.size) {
            if(arg < tabulation[i].first) {
                currentPolynom = i - 1
                break
            }
        }
        if(currentPolynom == null) throw ArithmeticException("Аргумент вне границ сплайна")
        val newX = arg - x[currentPolynom]
        Math.pow(newX, 2.0) * coefficient[currentPolynom*3 + 2] + newX * coefficient[currentPolynom * 3 + 1] + coefficient[currentPolynom * 3]
    }
}

fun createCubicSpline(h : Double,x : Array<Double>, y : Array<Double>) : ((x : Double) -> Double) {
    val matrix = getMatrixForCubicSpline(h, y)
    val coefficient = gauss(matrix) ?: throw ArithmeticException("Не удалось найти коэффициенты")
    return { arg : Double ->
        var currentPolynom : Int? = null
        for (i in 0 until tabulation.size) {
            if(arg < tabulation[i].first) {
                currentPolynom = i - 1
                break
            }
        }
        if(currentPolynom == null) throw ArithmeticException("Аргумент вне границ сплайна")
        val newX = arg - x[currentPolynom]
        Math.pow(newX, 3.0) * coefficient[currentPolynom*4 + 3] + Math.pow(newX, 2.0) * coefficient[currentPolynom * 4 + 2] + newX * coefficient[currentPolynom * 4 + 1] + coefficient[currentPolynom * 4]
    }
}


//4 производная (64 π (-3 + 32 x^2) cos(8 x))/x^4 + (8 π (3 - 96 x^2 + 512 x^4) sin(8 x))/x^5
fun main(args : Array<String>) {
    val x : Array<Double> = Array(4, {
        0.8 + it * 0.029
    })
    val y : Array<Double> = Array(4, {
        function(0.8 + it * 0.029)
    })
    for(i in 0 until 4) {
        tabulation.add(Pair(0.8 + i * 0.029, function(0.8 + i * 0.029)))
    }
    val parabolaSpline = createParabolaSpline(0.029, x, y)
    val cubicSpline = createCubicSpline(0.029, x, y)
    val lagrangePoly = Lagrange(tabulation)
    val newton = Newton(tabulation, createTable(tabulation))
    val linearSpline = linearSpline(tabulation)
    while (true) {
        try {
            print("Enter value: ")
            val x: Double = readLine()!!.toDouble()
            println("answer: ${function(x)}")
            println("Lagrange answer: ${lagrangePoly(x)}")
            println("Newton answer: ${newton(x)}")
            println("Linear spline: ${linearSpline(x)}")
            println("Parabola spline: ${parabolaSpline(x)}")
            println("Cubic spline: ${cubicSpline(x)}")
            //println("Parabola spline: ${quadraticSpline(tabulation)(x)}")
            //println("Qubic spline: ${qubicSpline(tabulation)(x)}")
        } catch (e: ArithmeticException) {
            println(e.message)
        }
    }
}


fun gauss(matrix : Array<Array<Double>>) : ArrayList<Double>? {
    for(i in 0 until (matrix.size)) {
        var columnsMax = 0.0
        var indexColumnsMax = 0
        for(j in i until matrix.size) {
            if(matrix[j][i].absoluteValue > columnsMax) {
                columnsMax = matrix[j][i]
                indexColumnsMax = j
            }
        }
        if(columnsMax == 0.0) return null
        if(i != indexColumnsMax) {
            for(j in i until matrix[0].size) {
                //swap
                matrix[i][j] = matrix[i][j] + matrix[indexColumnsMax][j]
                matrix[indexColumnsMax][j] = matrix[i][j] - matrix[indexColumnsMax][j]
                matrix[i][j] = matrix[i][j] - matrix[indexColumnsMax][j]
            }
        }
        for(j in i + 1 until matrix.size) {
            val coeff = matrix[j][i] / columnsMax
            matrix[j][i] = 0.0
            for(k in i + 1 until matrix[0].size)
                matrix[j][k] = matrix[j][k] - matrix[i][k] * coeff
        }
    }
    val ans = ArrayList<Double>(matrix.size)
    for(i in 0 until matrix.size) ans.add(0.0)
    for(i in (matrix.size - 1) downTo 0) {
        if(matrix[i][i] != 0.0) {
            ans[i] = matrix[i][matrix[0].size - 1]
            for(j in (matrix.size - 1) downTo (i + 1))
                ans[i] -= ans[j] * matrix[i][j]
            ans[i] /= matrix[i][i]
        } else ans[i] = 0.0

    }
    return ans
}

