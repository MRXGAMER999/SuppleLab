package com.example.supplelab.domain.model

import com.example.supplelab.R

enum class Country(
    val dialCode: Int,
    val code: String,
    val flag: Int

) {
    Egypt(
        dialCode = 20,
        code = "EG",
        flag = R.drawable.eg
    ),
    SaudiArabia(
        dialCode = 966,
        code = "SA",
        flag = R.drawable.sa
    ),
    kwait(
        dialCode = 965,
        code = "KW",
        flag = R.drawable.kw
    ),
    UAE(
        dialCode = 971,
        code = "AE",
        flag = R.drawable.ae
    ),
    Jordan(
        dialCode = 962,
        code = "JO",
        flag = R.drawable.jo
    ),
}