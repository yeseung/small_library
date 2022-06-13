package com.gongdaeoppa.small_library

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    var isBarcode = false

    private val REQ_SUB = 99
    //private val LIB_SITE = "LIB_SITE"
    //private val LIB_CODE = "LIB_CODE"
    //private val LIB_NAME = "LIB_NAME"

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0

    var spinner: Spinner? = null
    var editText : EditText? = null

    var barcodeTextView: TextView? = null
    var barcodeImage: ImageView? = null

    val list1 = ArrayList<String>()
    val list2 = ArrayList<String>()
    val list3 = ArrayList<String>()

    var sp_site: String? = null
    var sp_lib_code: String? = null
    var sp_lib_name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        Log.d("큐", checkInternetConnection().toString())

        if (!checkInternetConnection()) {
            val intent = Intent(this, InternetConnectionActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


        var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        var isBarcodeResult = pref.getBoolean("isBarcode", false)
        var codeResult = pref.getString("code", (Math.random() * 99999999).toString())

        Log.d("큐", codeResult.toString())

        isBarcode = isBarcodeResult

        spinner = findViewById<Spinner>(R.id.spinner3)
        editText = findViewById<EditText>(R.id.edit_Text)
        barcodeImage = findViewById<ImageView>(R.id.barcodeImage)
        barcodeTextView = findViewById<TextView>(R.id.barcodeTextView)

        getPreferences()
        getBarcodeImage()

        //var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        //var nameResult = pref.getString("name", "")

        barcodeImage?.setOnClickListener {
            Log.d("큐", "!!")
            if (!isBarcode){
                getBarcode()
            }
        }




        barcodeTextView?.setOnClickListener {
            Log.d("큐", "@@")
            if (!isBarcode){
                getBarcode()
            }
        }


        var button: Button = findViewById<Button>(R.id.button)
        button.setOnClickListener {

            var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
            var editor = pref.edit()
            editor.putString("search", editText?.getText().toString())
            editor.commit()

            var lib_siteResult = pref.getString("lib_site", "https%3A%2F%2Flib.geoje.go.kr%3A9080%2Fwkcms%2FKBookSearch%2FBookNomalSearch%2F")
            var lib_codeResult = pref.getString("lib_code", "MD")
            var lib_nameResult = pref.getString("lib_name", "거제시립수양도서관")
            var searchResult = pref.getString("search", "")

            //키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText?.windowToken, 0)

            val intent = Intent(this, LibraryActivity::class.java)
//            intent.putExtra("LIB_SITE", sp_site.toString())
//            intent.putExtra("LIB_CODE", sp_lib_code.toString())
//            intent.putExtra("LIB_NAME", sp_lib_name.toString())
//            intent.putExtra("LIB_SEARCH", editText?.getText().toString())
            intent.putExtra("LIB_SITE", lib_siteResult)
            intent.putExtra("LIB_CODE", lib_codeResult)
            intent.putExtra("LIB_NAME", lib_nameResult)
            intent.putExtra("LIB_SEARCH", searchResult)
            startActivityForResult(intent, REQ_SUB)
        }



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
                var pref = getSharedPreferences("data", MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("search", editText?.getText().toString())
                editor.commit()

                var lib_siteResult = pref.getString("lib_site", "https%3A%2F%2Flib.geoje.go.kr%3A9080%2Fwkcms%2FKBookSearch%2FBookNomalSearch%2F")
                var lib_codeResult = pref.getString("lib_code", "MD")
                var lib_nameResult = pref.getString("lib_name", "거제시립수양도서관")
                var searchResult = pref.getString("search", "")

                //키보드 숨기기
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText?.windowToken, 0)

                val intent = Intent(this, LibraryActivity::class.java)
//                intent.putExtra("LIB_SITE", sp_site.toString())
//                intent.putExtra("LIB_CODE", sp_lib_code.toString())
//                intent.putExtra("LIB_NAME", sp_lib_name.toString())
//                intent.putExtra("LIB_SEARCH", editText?.getText().toString())
                intent.putExtra("LIB_SITE", lib_siteResult)
                intent.putExtra("LIB_CODE", lib_codeResult)
                intent.putExtra("LIB_NAME", lib_nameResult)
                intent.putExtra("LIB_SEARCH", searchResult)
                startActivityForResult(intent, REQ_SUB)

                return@OnEditorActionListener true
            }
            false
        })






        var imageView5: ImageView = findViewById<ImageView>(R.id.imageView5)
        var imageView6: ImageView = findViewById<ImageView>(R.id.imageView6)
        var imageViewBook: ImageView = findViewById<ImageView>(R.id.imageViewBook)

        imageViewBook.setOnClickListener {
            val intent = Intent(this, BookActivity::class.java)
            //intent.putExtra("q", "하예승")
            startActivity(intent)
            //startActivityForResult(intent, REQ_SUB)
        }

        imageView6.setOnClickListener {
            var intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.gongdaeoppa.small_library"
            )
            startActivity(Intent.createChooser(intent, "공유"))
        }

        imageView5.setOnClickListener {
            getBarcode()
        }



        val libraryCodeUrl = "https://b973456356743434.cafe24.com/small_library/code.php"
        val client = OkHttpClient()
        val request = Request.Builder().url(libraryCodeUrl).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val jArray = JSONArray(body)
                for (i in 0 until jArray.length()) {
                    val jsonObject: JSONObject = jArray.getJSONObject(i)
                    list1?.add(jsonObject.get("site").toString())
                    list2?.add(jsonObject.get("lib_code").toString())
                    list3?.add(jsonObject.get("lib_name").toString())
                }

                runOnUiThread {
                    val adapter = ArrayAdapter<String>(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        list3
                    )
                    spinner?.adapter = adapter

                    var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                    var libPosition = pref.getInt("lib_position", 0)
                    spinner?.setSelection(libPosition)

                    spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            sp_site = list1[position]
                            sp_lib_code = list2[position]
                            sp_lib_name = list3[position]
                            Log.d(
                                "큐",
                                sp_site.toString() + " / " + sp_lib_code.toString() + " / " + sp_lib_name.toString()
                            )

                            var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                            var editor = pref.edit()
                            editor.putInt("lib_position", position)
                            editor.putString("lib_site", sp_site.toString())
                            editor.putString("lib_code", sp_lib_code.toString())
                            editor.putString("lib_name", sp_lib_name.toString())
                            editor.commit()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {}
        })

    }

    private fun getBarcode() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128) // 특정 규격의 바코드만 지원
        integrator.setPrompt("도서관 이용증 스캔해 주십시오.") // 카메라 프리뷰 하단에 표시되는 문구
        integrator.setCameraId(0) // 0 후면카메라, 1 전면카메라
        integrator.setBeepEnabled(true) // 바코드 인식할 때 비프음 여부
        integrator.setBarcodeImageEnabled(true) // 인식한 바코드 사진을 저장하고 경로를 반환
        integrator.setOrientationLocked(false) // orientation이 fullSensor일 때 orientation 변경을 허용할지 여부
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "" + result.contents, Toast.LENGTH_SHORT).show()

                var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("barcode", result.contents)
                editor.putBoolean("isBarcode", true)
                editor.putString("code", (Math.random() * 99999999).toInt().toString())
                editor.commit()

                isBarcode = true

                getPreferences()
                getBarcodeImage()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getBarcodeImage() {
        var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        var scannerResult = pref.getString("barcode", "10000000000001")

        val barcodeEncoder = BarcodeEncoder()
        val barcodeBitmap = barcodeEncoder.encodeBitmap(
            scannerResult,
            BarcodeFormat.CODE_128,
            500,
            100
        )
        barcodeImage?.setImageBitmap(barcodeBitmap)
    }

    private fun getPreferences(){
        var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        var scannerResult = pref.getString("barcode", "모바일회원증 - 도서대출시 편리하게 이용하세요.")
        var searchResult = pref.getString("search", "")

        //var barcodeTextView: TextView = findViewById<TextView>(R.id.barcodeTextView)
        editText = findViewById<EditText>(R.id.edit_Text)

        barcodeTextView?.text = scannerResult
        editText?.setText(searchResult)
    }

//    private fun getSearchText(){
//        var pref = getSharedPreferences("data", Context.MODE_PRIVATE)
//        var scannerResult = pref.getString("search", "")
//
//        editText = findViewById<EditText>(R.id.edit_Text)
//    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this@MainActivity, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {

            val pref = getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.remove("search")
            editor.commit()

            finish() //액티비티 종료
        }
    }


    override fun onPause() {
        mAdView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mAdView.resume()
    }

    override fun onDestroy() {
        mAdView.destroy()

//        val pref = getSharedPreferences("data", Context.MODE_PRIVATE)
//        val editor = pref.edit()
//        editor.remove("search")
//        editor.commit()

        super.onDestroy()
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