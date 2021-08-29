package com.example.weatherappmvvm.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager


class Apphelper {
    companion object {

        fun isOnline(mContext: Context): Boolean {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }

        private var progressBar: ProgressDialog? = null
        fun showProgressBar(context: Context?) {
            progressBar = ProgressDialog(context)
            progressBar!!.setMessage("Loading Please Wait...")
            progressBar!!.setCancelable(false)
            progressBar!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressBar!!.show()
        }

        fun hideProgressBar() {
            if (progressBar != null && progressBar!!.isShowing) progressBar!!.dismiss()
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view: View? = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}