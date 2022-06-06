package com.example.a14firebase.utils

import android.app.Activity
import android.view.View
import android.widget.Toast

fun View.show(){
    visibility = View.VISIBLE
}
fun View.hide(){
    visibility = View.GONE
}
fun Activity.toast(msg: String?){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}