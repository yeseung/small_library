package com.gongdaeoppa.small_library


data class HomeFeed(val total: String, var page: String, var fin_page: String, val url: String, val datas: List<Data>)

data class Data(val img: String,
                val pubdate: String,
                val loan_ok: String,
                val subject_code: String,
                val call_no: String,
                val title_info: String,
                val author: String,
                val publisher: String,
                val isbn: String,
                val reg_no: String,
                val shelf_loc_name: String,
                val rec_key: String,
                val lib_code: String,
                val lib_name: String,
                val return_plan_date: String,
                val loanable_copy_cnt: String,
                val location_data: String)


data class MybookFeed(val total: String, val datas: List<DataMybook>)

data class DataMybook(val idx: String,
                      val barcode: String,
                      val pubdate: String,
                      val subject_code: String,
                      val call_no: String,
                      val title_info: String,
                      val author: String,
                      val publisher: String,
                      val isbn: String,
                      val reg_no: String,
                      val shelf_loc_name: String,
                      val lib_name: String,
                      val datatime: String)

//items": [
//{
//    "img": "https://bookthumb-phinf.pstatic.net/cover/002/475/00247582.jpg",
//    "pubdate": "2011",
//    "loan_ok": "대출가능(비치중)",
//    "subject_code": "문학",
//    "call_no": "823.5-나16삼-1",
//    "title_info": "(고우영)三國志. 1",
//    "author": "고우영 지음",
//    "publisher": "애니북스",
//    "isbn": "9788959191192",
//    "reg_no": "AEM000043882",
//    "shelf_loc_name": "[장평]종합자료실",
//    "rec_key": "302435954",
//    "lib_code": "148184",
//    "lib_name": "거제시립장평도서관",
//    "return_plan_date": "",
//    "loanable_copy_cnt": "",
//    "location_data": ""
//},


//data class LibraryCode(val site: String, val lib_code: String, val lib_name: String)

//{
//    "site": "http%3A%2F%2Fkcms.kdot.co.kr%2FKBookSearch%2FBookNomalSearch%2F",
//    "lib_code": "MA",
//    "lib_name": "채움상상도서관"
//},