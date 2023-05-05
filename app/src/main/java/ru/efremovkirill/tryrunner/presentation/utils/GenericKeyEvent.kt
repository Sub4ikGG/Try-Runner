package ru.efremovkirill.tryrunner.presentation.utils

/*
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.first_otp_char_editText && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}

class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        currentView.pressAnimated()

        val text = editable.toString()
        when (currentView.id) {
            R.id.first_otp_char_editText -> if (text.length == 1) {
                nextView!!.requestFocus()
            }
            R.id.second_otp_char_editText -> if (text.length == 1) {
                nextView!!.requestFocus()
            }
            R.id.third_otp_char_editText -> if (text.length == 1) {
                nextView!!.requestFocus()
            }
            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) {
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) {
    }

}*/
