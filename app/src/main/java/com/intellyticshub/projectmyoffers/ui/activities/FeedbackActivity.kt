package com.intellyticshub.projectmyoffers.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.utils.Constants
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val feedback = etFeedbackBody.text.toString()

            if (feedback.isNotEmpty() && name.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:" + Constants.contactEmail)
                    putExtra(Intent.EXTRA_TEXT, "Name : $name \n $feedback")
                }
                startActivity(Intent.createChooser(intent, "Choose your option"))
            }
        }
    }
}
