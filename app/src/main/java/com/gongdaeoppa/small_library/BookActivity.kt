package com.gongdaeoppa.small_library

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class BookActivity : AppCompatActivity() {

    lateinit var mAdapter: MybookAdapter
    var recyclerView_mybook: RecyclerView? = null
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        Log.d("큐", checkInternetConnection().toString())

        if (!checkInternetConnection()) {
            val intent = Intent(this, InternetConnectionActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        progressBar = findViewById(R.id.progressBar_mybook)
        progressBar?.visibility = View.GONE

        recyclerView_mybook = findViewById<RecyclerView>(R.id.recyclerView_mybook)
        //layoutManager = LinearLayoutManager(this)
        recyclerView_mybook?.layoutManager = LinearLayoutManager(this)
        recyclerView_mybook?.setHasFixedSize(true)

        fetchJson()


    }

    private fun fetchJson() {
        progressBar?.visibility = View.VISIBLE

        var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        var scannerResult = pref.getString("barcode", "")
        var codeResult = pref.getString("code", "")

        val url = "https://b973456356743434.cafe24.com/small_library/mybook_select.php?barcode=" + scannerResult + "&code=" + codeResult
        Log.d("큐", url)

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                Log.d("큐", body.toString())

                val gson = GsonBuilder().create()

                val MybookFeed = gson.fromJson(body, MybookFeed::class.java)

                runOnUiThread {
                    if (Integer.parseInt(MybookFeed.total) == 0) {
                        //Toast.makeText(this@BookActivity, "해당 데이터가 없습니다.", Toast.LENGTH_LONG).show()
                        progressBar?.visibility = View.GONE

                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                finish()
                            }
                        }
                        var builder = AlertDialog.Builder(this@BookActivity)
                        builder.setMessage("해당 데이터가 없습니다.")
                        builder.setPositiveButton("확인", listener)
                        builder.show()

                    } else {
                        progressBar?.visibility = View.GONE

                        mAdapter = MybookAdapter(MybookFeed)

                        mAdapter.setItemClickListener(object :
                                MybookAdapter.OnItemClickListener {

                            override fun onClick(v: View, position: Int, data: DataMybook?) {
                                //MybookFeed.datas.removeAt(position)
                                //mAdapter.notifyDataSetChanged()
                                Log.d("큐", position.toString() + " / " + data?.idx)

                                var listener = object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {

                                        var pref =
                                                getSharedPreferences("data", Context.MODE_PRIVATE)
                                        var scannerResult = pref.getString("barcode", "")
                                        var codeResult = pref.getString("code", "")
                                        val url =
                                                "https://b973456356743434.cafe24.com/small_library/mybook_del.php?barcode=" + scannerResult + "&code=" + codeResult + "&idx=" + data?.idx
                                        Log.d("큐", url)
                                        val request = Request.Builder().url(url).build()
                                        val client = OkHttpClient()
                                        client.newCall(request).enqueue(object : Callback {
                                            override fun onResponse(
                                                    call: Call,
                                                    response: Response
                                            ) {
                                                val body = response?.body()?.string()
                                                Log.d("큐", body.toString())

                                                runOnUiThread {
                                                    fetchJson()
                                                }
                                            }
                                            override fun onFailure(call: Call, e: IOException) {}
                                        })
                                    }
                                }

                                var builder = AlertDialog.Builder(this@BookActivity)
                                builder.setTitle(data?.title_info)
                                builder.setMessage("삭제하시겠습니까?")
                                builder.setPositiveButton("확인", listener)
                                builder.setNegativeButton("취소", null)
                                builder.show()

                            }
                        })

                        recyclerView_mybook?.adapter = mAdapter

                        Log.d("큐", "---" + MybookFeed.total)
                        supportActionBar?.title = "내 서재 (" + MybookFeed.total + "건)"
                    }
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@BookActivity, "일시적인 네트워크 장애 및 시스템에 문제가 발생하였습니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show()
                //progressBar?.visibility = View.GONE
            }
        })

    }

    // 인터넷 연결 확인
    fun checkInternetConnection(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if (activeNetwork != null)
            return true

        return false
    }
}