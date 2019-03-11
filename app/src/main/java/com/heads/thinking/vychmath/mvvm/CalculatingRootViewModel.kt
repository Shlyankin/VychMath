package com.heads.thinking.vychmath.mvvm

import android.arch.lifecycle.ViewModel

class CalculatingRootViewModel : ViewModel() {
    var methodsType = ""
    var function  = ""
    var eps  = ""
    var leftInterval = ""
    var rightInterval = ""
    var answer = ""
    var nevyazka = ""
    var iterations = ""
    var message = ""
}