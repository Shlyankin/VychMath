package com.heads.thinking.vychmath.mvvm

import android.arch.lifecycle.ViewModel
import net.objecthunter.exp4j.Expression

class TwoDimensialCalculatingRootViewModel : ViewModel() {
    var methodsType = ""
    var answer      = ""
    var nevyazka    = ""
    var iterations  = ""
    var message      = ""
    var leftIntervalX = ""
    var rightIntervalX = ""
    var leftIntervalY = ""
    var rightIntervalY = ""
    var eps = ""
    var f1 = ""
    var f2 = ""
    var f1x = ""
    var f1y = ""
    var f2x = ""
    var f2y = ""
}