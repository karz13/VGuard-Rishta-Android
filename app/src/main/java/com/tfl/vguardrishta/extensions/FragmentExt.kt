package com.tfl.vguardrishta.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


fun FragmentActivity.toast(@StringRes resourceId: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(resourceId), length)
}

fun FragmentActivity.toast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        addToBackStack(fragment.javaClass.name)
        add(frameId, fragment)
    }
}

fun AppCompatActivity.addFragment(
    fragment: Fragment,
    frameId: Int,
    targetFragment: Fragment,
    requestCode: Int
) {
    fragment.setTargetFragment(targetFragment, requestCode)
    addFragment(fragment, frameId)
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, tag: String = "") {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment, tag)
    }
}