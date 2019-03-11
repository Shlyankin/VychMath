package com.heads.thinking.vychmath.custom.math

import kotlin.math.absoluteValue

class Matrix(val row : Int, val col : Int, val init : ((i : Int, j : Int) -> Double)) {
    private var matrix : Array<Array<Double>> = Array(row) { i : Int ->
        Array(col) { j : Int->
            init(i, j)
        }
    }

    fun inverseMatrix2x2() = Matrix(2, 2, init = { i: Int, j: Int ->
        var result = 0.0
        if (i == 0) {
            if (j == 0) result = matrix[1][1]
            if (j == 1) result = -matrix[0][1]
        } else if (i == 1) {
            if (j == 0) result = -matrix[1][0]
            if (j == 1) result = matrix[0][0]
        }
        result
    }) /det2x2()

    fun det2x2() : Double {
        return this[0][0]*this[1][1] - this[1][0]*this[0][1]
    }

    fun norma() : Double {
        var result : Double = 0.0
        for(row in matrix) {
            for(value in row) {
                result += value.absoluteValue
            }
        }
        return result
    }

    operator fun get(index : Int) : Array<Double> = matrix[index]

    operator fun plus(b : Matrix) : Matrix = Matrix(this.row, this.col, { i: Int, j: Int -> this[i][j] + b[i][j] })

    operator fun minus(b : Matrix) : Matrix = Matrix(this.row, this.col, { i: Int, j: Int -> this[i][j] - b[i][j] })

    operator fun times(b : Matrix) : Matrix = Matrix(this.row, b.col, init = { i: Int, j: Int ->
        var result = 0.0
        for (k in 0..(this.col - 1))
            result += this[i][k] * b[k][j]
        result
    })

    operator fun times(value : Double) : Matrix = Matrix(this.row, this.col, init = { i: Int, j: Int -> value * this[i][j] })

    operator fun div(value: Double) : Matrix = Matrix(this.row, this.col, init = { i: Int, j: Int -> this[i][j] / value })
}