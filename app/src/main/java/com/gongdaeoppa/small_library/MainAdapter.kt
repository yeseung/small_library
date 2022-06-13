package com.gongdaeoppa.small_library

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder

class MainAdapter(val homeFeed: HomeFeed) : RecyclerView.Adapter<CustomViewHolder>() {


    override fun getItemCount(): Int {
        return homeFeed.datas.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.item_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val textView_title: TextView = holder.view.findViewById<TextView>(R.id.textView_title)
        val textView1: TextView = holder.view.findViewById<TextView>(R.id.textView1)
        val textView2: TextView = holder.view.findViewById<TextView>(R.id.textView2)
        val textView3: TextView = holder.view.findViewById<TextView>(R.id.textView3)
        val textView4: TextView = holder.view.findViewById<TextView>(R.id.textView4)
        val textView5: TextView = holder.view.findViewById<TextView>(R.id.textView5)
        val textView6: TextView = holder.view.findViewById<TextView>(R.id.textView6)
        var imageView: ImageView = holder.view.findViewById<ImageView>(R.id.imageView)

        val data = homeFeed.datas.get(position)

        textView_title?.text = data.title_info
        textView1?.text = data.author
        textView2?.text = data.publisher + " | " + data.pubdate
        textView3?.text = data.lib_name
        textView4?.text = data.call_no
        textView5?.text = data.loan_ok
        textView6?.text = data.subject_code

        //Picasso.with(holder?.view?.context).load(items.img).placeholder(R.drawable.ic_baseline_insert_emoticon_24).into(imageView)
        if (data?.img == "https://b973456356743434.cafe24.com/small_library/book.png") {
            imageView.setImageResource(R.drawable.ic_book)
        }else {
            Picasso.with(holder?.view?.context).load(data.img).resize(200, 290).into(imageView)
        }
        holder?.data = data
    }
}


class CustomViewHolder(val view: View, var data: Data? = null) : RecyclerView.ViewHolder(view) {

    //    companion object{
//        val LIB_TITLE_KEY = "LIB_TITLE"
//        val LIB_ID_KEY = "LIB_ID"
//    }
    init {
        view.setOnClickListener {
            //Log.d("큐", "more~")
            //Toast.makeText(view.context, "${data?.title_info}", Toast.LENGTH_LONG).show()

            var builder = AlertDialog.Builder(view.context)
            //builder.setTitle(data?.title_info)
            //builder.setIcon(R.mipmap.ic_launcher_round)
            //builder.setMessage("testtest test ~~~~")

            var v1 = LayoutInflater.from(view.context).inflate(R.layout.book_input, null)
            builder.setView(v1)

            var bi_textView_title = v1.findViewById<TextView>(R.id.bi_textView_title)
            var bi_textView1 = v1.findViewById<TextView>(R.id.bi_textView1)
            var bi_textView2 = v1.findViewById<TextView>(R.id.bi_textView2)
            var bi_textView3 = v1.findViewById<TextView>(R.id.bi_textView3)
            var bi_imageView: ImageView = v1.findViewById<ImageView>(R.id.bi_imageView)

            var bi_linearLayout: LinearLayout = v1.findViewById<LinearLayout>(R.id.layout_right)

            bi_textView_title.text = data?.title_info

            if (data?.img == "https://b973456356743434.cafe24.com/small_library/book.png") {
                //bi_imageView.scaleType = ImageView.ScaleType.FIT_XY
                //bi_imageView.scaleType = ImageView.ScaleType.CENTER
                //bi_imageView.setImageResource(R.drawable.ic_book)
                bi_imageView.visibility = View.GONE
                bi_linearLayout.setPadding(0, 0, 0, 0)
            }else{
                Picasso.with(view.context).load(data?.img).resize(200, 280).into(bi_imageView)
            }
            //d.text = data?.loan_ok + "\n" + data?.isbn + "\n" + data?.lib_name

            bi_textView1?.text = data?.subject_code + "\n"
            bi_textView1.append(data?.author + "\n")
            bi_textView1.append(data?.publisher + " | " + data?.pubdate + "\n")

            bi_textView1.append(data?.lib_name + "\n")

            var shelf_loc_name = data?.shelf_loc_name
            shelf_loc_name = shelf_loc_name?.replace("[", "")?.replace("]", " ")
            bi_textView1.append(shelf_loc_name + "\n")
            if (data?.isbn != "")
                bi_textView1.append("도서번호: " + data?.isbn + "\n")
            if (data?.reg_no != "")
                bi_textView1.append("등록번호: " + data?.reg_no + "\n")
            bi_textView2.text = data?.call_no
            bi_textView3.text = data?.loan_ok

            var listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        //DialogInterface.BUTTON_POSITIVE ->
                        //DialogInterface.BUTTON_NEUTRAL ->
                        DialogInterface.BUTTON_NEGATIVE -> {

                            var pref = view.context.getSharedPreferences("data", Context.MODE_PRIVATE)
                            var scannerResult = pref.getString("barcode", "")
                            var codeResult = pref.getString("code", "")
                            var isBarcodeResult = pref.getBoolean("isBarcode", false)

                            Log.d("큐", scannerResult.toString())

                            if (isBarcodeResult) {

                                var pubdate: String? = null
                                var subject_code: String? = null
                                var call_no: String? = null
                                var title_info: String? = null
                                var author: String? = null
                                var publisher: String? = null
                                var isbn: String? = null
                                var reg_no: String? = null
                                var shelf_loc_name: String? = null
                                var lib_name: String? = null

                                if (data?.pubdate == "") {
                                    pubdate = "-"
                                } else {
                                    pubdate = data?.pubdate
                                }
                                if (data?.subject_code == "") {
                                    subject_code = "-"
                                } else {
                                    subject_code = data?.subject_code
                                }
                                if (data?.call_no == "") {
                                    call_no = "-"
                                } else {
                                    call_no = data?.call_no
                                }
                                if (data?.title_info == "") {
                                    title_info = "-"
                                } else {
                                    title_info = data?.title_info
                                }
                                if (data?.author == "") {
                                    author = "-"
                                } else {
                                    author = data?.author
                                }
                                if (data?.publisher == "") {
                                    publisher = "-"
                                } else {
                                    publisher = data?.publisher
                                }
                                if (data?.isbn == "") {
                                    isbn = "-"
                                } else {
                                    isbn = data?.isbn
                                }
                                if (data?.reg_no == "") {
                                    reg_no = "-"
                                } else {
                                    reg_no = data?.reg_no
                                }
                                if (data?.shelf_loc_name == "") {
                                    shelf_loc_name = "-"
                                } else {
                                    shelf_loc_name = data?.shelf_loc_name
                                }
                                if (data?.lib_name == "") {
                                    lib_name = "-"
                                } else {
                                    lib_name = data?.lib_name
                                }

                                var q = URLEncoder.encode(
                                    scannerResult.toString() + "||" + codeResult.toString() + "||" + pubdate + "||" + subject_code + "||" + call_no + "||" + title_info + "||" + author + "||" + publisher + "||" + isbn + "||" + reg_no + "||" + shelf_loc_name + "||" + lib_name,
                                    "UTF-8"
                                )
                                val url =
                                    "https://b973456356743434.cafe24.com/small_library/mybook_insert.php?q=" + q.toString()
                                Log.d("큐", url)
                                val client = OkHttpClient()
                                val request = Request.Builder().url(url).build()
                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {}
                                    override fun onResponse(call: Call, response: Response) {
                                        var str = response!!.body()!!.string()
                                        Log.d("큐", str)
                                    }
                                })
                                Toast.makeText(v1.context, "등록 되었습니다.", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(v1.context, "도서관 이용증 스캔해 주십시오.", Toast.LENGTH_SHORT).show()
                            }
                        } //DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }


            builder.setPositiveButton("확인", null)
            builder.setNegativeButton("내서재담기", listener)

            builder.show()

        }
    }

}



