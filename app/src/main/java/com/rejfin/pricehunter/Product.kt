package com.rejfin.pricehunter

data class Product(
    val name:String = "",
    val price:Double = 0.0,
    val urls:List<String> = listOf(""),
    val currency :String = "",
    val date:String = ""
)