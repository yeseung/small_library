package com.gongdaeoppa.small_library


import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MybookAdapter(val MybookFeed: MybookFeed) : RecyclerView.Adapter<MybookViewHolder>() {


    override fun getItemCount(): Int {
        return MybookFeed.datas.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MybookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.mybook_row, parent, false)
        return MybookViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: MybookViewHolder, position: Int) {
        val textView_title: TextView = holder.view.findViewById<TextView>(R.id.textView_title)
        val textView1: TextView = holder.view.findViewById<TextView>(R.id.textView1)
        val textView2: TextView = holder.view.findViewById<TextView>(R.id.textView2)
        val textView3: TextView = holder.view.findViewById<TextView>(R.id.textView3)
        val textView4: TextView = holder.view.findViewById<TextView>(R.id.textView4)

        val data = MybookFeed.datas.get(position)

        var shelf_loc_name = data?.shelf_loc_name
        shelf_loc_name = shelf_loc_name?.replace("[", "")?.replace("]", " ")

        textView_title?.text = data.title_info
        textView1?.text = data.author
        textView2?.text = data.subject_code + " | " + data.publisher + " | " + data.pubdate
        if (data?.isbn != "")
            textView2?.append("\n도서번호: " + data?.isbn)
        if (data?.reg_no != "")
            textView2?.append("\n등록번호: " + data?.reg_no)

        textView3?.text = data.lib_name + " | " + shelf_loc_name
        textView4?.text = data.call_no
        holder?.data = data
        holder?.position = position

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position, data)
        }
    }

    //ClickListener
    interface OnItemClickListener {
        fun onClick(v: View, position: Int, data: DataMybook?)
    }
    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}


class MybookViewHolder(val view: View, var data: DataMybook? = null, var position: Int? = null) : RecyclerView.ViewHolder(view) {

    companion object{
        //val MYBOOK_TITLE_KEY = "MYBOOK_TITLE"
        //val MYBOOK_ID_KEY = "MYBOOK_ID"
    }
    init {
//        view.setOnClickListener {
//            Log.d("큐", "more~")
//            Toast.makeText(view.context, data?.title_info + " / " + data?.idx, Toast.LENGTH_SHORT).show()
//        }
//        view.setOnLongClickListener{
//            Log.d("큐", "___" + position.toString())
//            //notifyItemInserted
//
//            Toast.makeText(view.context, data?.title_info + " / " + data?.idx, Toast.LENGTH_SHORT).show()
//            return@setOnLongClickListener true
//        }
    }

}



