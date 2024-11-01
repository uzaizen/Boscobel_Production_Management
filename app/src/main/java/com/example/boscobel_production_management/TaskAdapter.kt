package com.example.boscobel_production_management

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var mTaskList = mutableListOf<Task>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return mTaskList.size
    }

    override fun getItem(position: Int): Any {
        return mTaskList[position]
    }

    override fun getItemId(position: Int): Long {
        return mTaskList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = convertView ?: mLayoutInflater.inflate(R.layout.activity_taskadapter, null)

        Log.d("uztest", "taskadapter0")

        val textView1 = view.findViewById<TextView>(R.id.text1)
        val textView2 = view.findViewById<TextView>(R.id.text2)
        val textView3 = view.findViewById<TextView>(R.id.text3)
        val textView4 = view.findViewById<TextView>(R.id.text4)
        val textView5 = view.findViewById<TextView>(R.id.text5)
        val textView6 = view.findViewById<TextView>(R.id.text6)

        Log.d("uztest", "taskadapter1")

        textView1.text = mTaskList[position].date.toString().substring(0, 10)
        if (mTaskList[position].flag == 0) {
            textView2.text = "☐"
        } else
            if (mTaskList[position].flag == 1) {
                textView2.text = "☑"
            }
//        textView2.text = mTaskList[position].flag.toString()
        textView3.text = mTaskList[position].title
        textView4.text = mTaskList[position].amount.toString()
        textView5.text = mTaskList[position].dimension
        textView6.text = mTaskList[position].comment

        textView1.text = mTaskList[position].date.toString().substring(0, 10)
        if (mTaskList[position].flag == 0) {
            textView2.text = "☐"
        } else
            if (mTaskList[position].flag == 1) {
                textView2.text = "☑"
            }


        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.JAPANESE)
        val date = mTaskList[position].date
        textView1.text = simpleDateFormat.format(date)

        return view
    }

    class TaskAdapter {
    }
}