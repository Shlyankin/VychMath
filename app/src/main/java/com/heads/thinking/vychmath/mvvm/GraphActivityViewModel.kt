package com.heads.thinking.vychmath.mvvm

import android.arch.lifecycle.ViewModel
import net.objecthunter.exp4j.Expression

class GraphActivityViewModel : ViewModel() {
    var expressions : ArrayList<Expression> = ArrayList()
    var stringExpressions : ArrayList<String> = ArrayList()
    var leftInterval : String = ""
    var rightInterval : String = ""
    var function : String = ""
}