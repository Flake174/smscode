package com.example.smscode

import android.Manifest
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class MainActivity : AppCompatActivity() {

    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    private val SMS_CONSENT_REQUEST = 2
    private var exampleDialog: ExampleDialog? = null
    private var randomCode: Int = 0
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)
        SmsRetriever.getClient(this).startSmsUserConsent(null)

        val submitBtn: Button = findViewById<Button>(R.id.submitBtn)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE);

        val editText: EditText = findViewById(R.id.phoneNumber)
        var str: String

        submitBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                str = editText.text.toString()
                if(str.trim().isNotEmpty()) {
                    openDialog()
                    sendMessage()
                } else {
                    val t = Toast.makeText(this@MainActivity, "Empty number!", Toast.LENGTH_LONG)
                    t.show()
                }
            }
        })

    }

    fun openDialog() {
        exampleDialog = ExampleDialog()
        exampleDialog!!.show(supportFragmentManager, "example_dialog")
    }

    private fun sendMessage() {
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            val smsManager: SmsManager = SmsManager.getDefault()
            randomCode = (1000..9999).random()
            smsManager.sendTextMessage("+15555215554",
                null,
                "Your verification code: $randomCode",
                null, null)
        } else {
            val t = Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG)
            t.show()
        }
    }

    private fun checkPermission(permission: String?): Boolean {
        val check = ContextCompat.checkSelfPermission(this, permission!!)
        return check == PackageManager.PERMISSION_GRANTED
    }


    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                        } catch (e: ActivityNotFoundException) {
                            Log.d("MainActivity", "ActivityNotFoundException")
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d("MainActivity", "Timeout error")
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                    val inflater: LayoutInflater = LayoutInflater.from(this@MainActivity)
                    val v: View = inflater.inflate(R.layout.fragment_dialog, null)
                    val editText: EditText = v.findViewById(R.id.sms_code)
                    editText.setText(parseSmsCode(message!!))
                }
            }
        }
    }

    private fun parseSmsCode(string: String): String {
        return string.replace("[^0-9]".toRegex(), "")
    }

}


