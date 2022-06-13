package com.gongdaeoppa.small_library

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo

import android.widget.TextView.OnEditorActionListener

import android.view.inputmethod.InputMethodManager

import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class LibraryActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    var page = 1
    //var isLoading = false

    lateinit var adapter: MainAdapter
    lateinit var layoutManager: LinearLayoutManager

    var recyclerView_main: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var button: Button? = null
    var button_pre: Button? = null
    var button_next: Button? = null
    var editText: EditText? = null
    var spinner: Spinner? = null
    var spinner2: Spinner? = null

    val list1 = ArrayList<String>()
    val list2 = ArrayList<String>()
    val list3 = ArrayList<String>()

    var sp_site: String? = null
    var sp_lib_code: String? = null
    var field: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        button = findViewById<Button>(R.id.button)
        button_pre = findViewById<Button>(R.id.button_pre)
        button_next = findViewById<Button>(R.id.button_next)
        editText = findViewById<EditText>(R.id.editText)

        button_pre?.isEnabled = false
        button_next?.isEnabled = false

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)




        val navBarTitle = intent.getStringExtra("LIB_NAME")
        val iEditText = intent.getStringExtra("LIB_SEARCH")
        supportActionBar?.title = navBarTitle
        editText?.setText(iEditText)
        //Log.d("큐", iEditText)
        sp_site = intent.getStringExtra("LIB_SITE")
        sp_lib_code = intent.getStringExtra("LIB_CODE")




        progressBar = findViewById(R.id.progressBar)
        progressBar?.visibility = View.GONE

        recyclerView_main = findViewById<RecyclerView>(R.id.recyclerView_main)
        //layoutManager = LinearLayoutManager(this)
        recyclerView_main?.layoutManager = LinearLayoutManager(this)
        recyclerView_main?.setHasFixedSize(true)





        //getSpinner()
        //fetchJsonSpinner()

        //spinner = findViewById<Spinner>(R.id.spinner)
        spinner2 = findViewById<Spinner>(R.id.spinner2)

        val data = listOf("통합", "도서명", "저자", "출판사")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data)
        spinner2?.adapter = adapter

        spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        Log.d("큐", "--0")
                        field = "all"
                    }
                    1 -> {
                        Log.d("큐", "--1")
                        field = "bookname"
                    }
                    2 -> {
                        Log.d("큐", "--2")
                        field = "author"
                    }
                    3 -> {
                        Log.d("큐", "--3")
                        field = "publisher"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



//        val libraryCodeUrl = "https://b973456356743434.cafe24.com/small_library/code.php"
//        val client = OkHttpClient()
//        val request = Request.Builder().url(libraryCodeUrl).build()
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                val body = response?.body()?.string()
//                val jArray = JSONArray(body)
//                for (i in 0 until jArray.length()) {
//                    val jsonObject: JSONObject = jArray.getJSONObject(i)
//
//                    list1?.add(jsonObject.get("site").toString())
//                    list2?.add(jsonObject.get("lib_code").toString())
//                    list3?.add(jsonObject.get("lib_name").toString())
//
//                    //Log.d("큐", "-- " + jsonObject.get("lib_name").toString())
//                }
//
//                runOnUiThread {
//                    val adapter = ArrayAdapter<String>(this@LibraryActivity, android.R.layout.simple_list_item_1, list3)
//                    spinner?.adapter = adapter
//
//                    spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                            //val seletedView = list1[position] + " / " + list2[position] + " / " + list3[position]
//                            //Log.d("큐", seletedView)
//                            sp_site = list1[position]
//                            sp_lib_code = list2[position]
//                            Log.d("큐", sp_site.toString() + " / " + sp_lib_code.toString())
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>?) {}
//                    }
//                }
//
//
//            }
//
//            override fun onFailure(call: Call, e: IOException) {}
//        })


        page = 1
        val encodeStr: String = URLEncoder.encode(editText?.getText().toString(), "UTF-8")
        fetchJson(encodeStr)


        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(edit: Editable?) {
                val text = edit.toString()
                Log.d("큐", text)
                var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("search", text)
                editor.commit()
            }
        }
        editText?.addTextChangedListener(listener)
        editText?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                page = 1
                val encodeStr: String = URLEncoder.encode(editText?.getText().toString(), "UTF-8")
                Log.d("큐", encodeStr)



                var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("search", editText?.getText().toString())
                editor.commit()


                //키보드 숨기기
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText?.windowToken, 0)

                fetchJson(encodeStr)
                return@OnEditorActionListener true
            }
            false
        })

        button_next?.setOnClickListener {
            page++
            val encodeStr: String = URLEncoder.encode(editText?.getText().toString(), "UTF-8")
            fetchJson(encodeStr)
        }

        button_pre?.setOnClickListener {
            page--
            val encodeStr: String = URLEncoder.encode(editText?.getText().toString(), "UTF-8")
            fetchJson(encodeStr)
        }

        button?.setOnClickListener {
            page = 1
            val encodeStr: String = URLEncoder.encode(editText?.getText().toString(), "UTF-8")
            Log.d("큐", encodeStr)


            var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
            var editor = pref.edit()
            editor.putString("search", editText?.getText().toString())
            editor.commit()


            //키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText?.windowToken, 0)

            fetchJson(encodeStr)
        }
    }




    private fun fetchJson(encodeStr: String) {
        progressBar?.visibility = View.VISIBLE
        //isLoading = true

        //Log.d("큐", "ddddd : " + sp_site.toString() + " + " + sp_lib_code.toString())

        //val url = "https://b973456356743434.cafe24.com/small_library/search.php?lib_name=https%3A%2F%2Flib.geoje.go.kr%3A9080%2Fwkcms%2FKBookSearch%2FBookNomalSearch%2F&lib_code=MA&q=" + encodeStr + "&page=" + page
        val url = "https://b973456356743434.cafe24.com/small_library/search.php?lib_site=" + sp_site.toString() + "&lib_code=" + sp_lib_code.toString() + "&field=" + field.toString() + "&q=" + encodeStr + "&page=" + page
        Log.d("큐", url)

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                Log.d("큐", body.toString())

                val gson = GsonBuilder().create()

                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                runOnUiThread {

                    if (Integer.parseInt(homeFeed.total) == 0) {
                        Toast.makeText(this@LibraryActivity, "찾으시는 자료가 없습니다.", Toast.LENGTH_LONG).show()
                        progressBar?.visibility = View.GONE
                    } else {

                        if (homeFeed.datas != null) {
                            adapter = MainAdapter(homeFeed)
                            recyclerView_main?.adapter = adapter

                            button_next?.isEnabled = true
                            if (page == 1) {
                                button_pre?.isEnabled = false
                            } else {
                                button_pre?.isEnabled = true
                            }
                        } else {
                            Log.d("큐", "NUUUUUUULL")
                            page--
                            button_next?.isEnabled = false
                            Toast.makeText(this@LibraryActivity, "찾으시는 자료가 없습니다.", Toast.LENGTH_LONG).show()
                        }

                        progressBar?.visibility = View.GONE
                        //isLoading = false

                        val textView2: TextView = findViewById<TextView>(R.id.bi_textView2)
                        textView2.text = "총 " + homeFeed.total + " 건이 검색되었습니다. (" + homeFeed.page + "/" + homeFeed.fin_page + ")"

                        Log.d("큐", homeFeed.fin_page + " / " + homeFeed.url)

                        if (page == Integer.parseInt(homeFeed.fin_page)) {
                            button_next?.isEnabled = false
                        }
                        //recyclerView_main = findViewById(R.id.recyclerView_main)
                        //recyclerView_main?.adapter = MainAdapter(homeFeed)

                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@LibraryActivity, "일시적인 네트워크 장애 및 시스템에 문제가 발생하였습니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show()
                progressBar?.visibility = View.GONE
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId){
            R.id.menu_book -> {
                val intent = Intent(this, BookActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }



}