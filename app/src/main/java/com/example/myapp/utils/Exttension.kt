package com.example.myapp.utils

import android.view.View
import androidx.navigation.Navigation

fun View.navigateFragment(id:Int){
    try {
        Navigation.findNavController(this).navigate(id)
    }catch (e: Exception){
        e.printStackTrace()
    }
}