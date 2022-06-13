package com.gongdaeoppa.small_library

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class InternetConnectionActivity : AppCompatActivity() {

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internet_connection)


        var button: Button = findViewById<Button>(R.id.button3)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this@InternetConnectionActivity, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            val pref = getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.remove("search")
            editor.commit()
            finish() //액티비티 종료
        }
    }

}