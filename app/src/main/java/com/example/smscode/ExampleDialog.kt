package com.example.smscode

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment


class ExampleDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_dialog, null)



        builder.setView(view)
            .setTitle("Verification code")
            .setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, i -> })
            .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
            })

        return builder.create()
    }

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }*/
/*    override fun onAttach(context: Context) {
    }*/

}