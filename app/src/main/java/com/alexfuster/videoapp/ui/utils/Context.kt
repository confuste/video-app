package com.alexfuster.videoapp.ui.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(message: Int){
    Toast.makeText(this,  message, Toast.LENGTH_LONG).show()
}
