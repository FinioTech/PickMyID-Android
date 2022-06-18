package com.finiotech.ekycdemo

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context

class CommonUtil {
    companion object {

        private lateinit var progressBar: ProgressDialog

        fun showProgress(context: Context) {
            progressBar = ProgressDialog(context)
            progressBar.setMessage("Loading...")
            progressBar.show()
        }

        fun dismissProgress() {
            progressBar.dismiss()
        }

        fun showDialog(context: Context, message: String) {
            var dialog: AlertDialog.Builder = AlertDialog.Builder(context);
            dialog.setMessage(message)
            dialog.show();
        }
    }
}