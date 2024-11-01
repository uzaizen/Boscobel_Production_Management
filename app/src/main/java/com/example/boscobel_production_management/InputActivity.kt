package com.example.boscobel_production_management

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import android.view.View
import androidx.core.net.ParseException
import com.example.boscobel_production_management.databinding.ActivityInputBinding
import com.example.boscobel_production_management.databinding.ActivityMainBinding
import com.example.boscobel_production_management.databinding.ContentInputBinding
import io.realm.Realm
import com.example.boscobel_production_management.EXTRA_TASK
import java.text.SimpleDateFormat
import java.util.*

class InputActivity : AppCompatActivity() {
    //
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mTask: Task? = null
    private lateinit var binding: ActivityInputBinding
    private lateinit var contentBinding: ContentInputBinding


    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                contentBinding.dateButton.text = dateString
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }


    private val mOnDoneClickListener = View.OnClickListener {
        addTask()
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_input)

        Log.d("uztest", "InputActivity Created")
/*
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.setBackgroundColor(resources.getColor(com.google.android.material.R.color.design_default_color_secondary_variant))
            toolbar.setTitleTextColor(resources.getColor(R.color.white))
        } else {
            Log.e("InputActivity", "Toolbar is null")
        }



        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // デフォルトのタイトルを非表示

 */
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ContentInputBinding を初期化
        contentBinding = ContentInputBinding.bind(binding.root.findViewById(R.id.contentInput))


        // ActionBarを設定する
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // UI部品の設定
        contentBinding.dateButton.setOnClickListener(mOnDateClickListener)


        contentBinding.doneButton.setOnClickListener(mOnDoneClickListener)


        // EXTRA_TASKからTaskのidを取得して、 idからTaskのインスタンスを取得する
        val intent = intent
        val taskId = intent.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        mTask = realm.where(Task::class.java).equalTo("id", taskId).findFirst()
        realm.close()

        if (mTask == null) {
            // 新規作成の場合
//            val a:Int =0  //No Meaning
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

        } else {
            // 更新の場合
            contentBinding.titleEditText.setText(mTask!!.title)
            contentBinding.amountEditText.setText(mTask!!.amount.toString())
            contentBinding.dimensionEditText.setText(mTask!!.dimension.toString())
            if (mTask!!.flag == 0) {contentBinding.check1.isChecked = false}
            if (mTask!!.flag == 1) {contentBinding.check1.isChecked = true}
            contentBinding.commentEditText.setText(mTask!!.comment.toString())

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            contentBinding.dateButton.text = dateString
//            times_button.text = timeString
        }
    }

    private fun addTask() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mTask == null) {
            // 新規作成の場合
            mTask = Task()

            val taskRealmResults = realm.where(Task::class.java).findAll()

            val identifier: Int =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mTask!!.id = identifier
        }

        val title = contentBinding.titleEditText.text.toString()
        val amount = contentBinding.amountEditText.text.toString()
        val dimension = contentBinding.dimensionEditText.text.toString()
        var comment:String = contentBinding.commentEditText.text.toString()

//        Log.d("uztest", "comment="+comment)
//        val sort1 = date_button.text.toString()
//        val sort2 = flag_edit_text.text.toString()

        mTask!!.title = title
        mTask!!.amount = amount.toInt()
//        mTask!!.date = sort1.toDate()!!
        mTask!!.dimension = dimension
        mTask!!.comment = comment.toString()
        if (contentBinding.check1.isChecked == true) mTask!!.flag=1
        if (contentBinding.check1.isChecked == false) mTask!!.flag=0
//        mTask!!.flag = sort2.toInt()

        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mTask!!.date = date

        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()
    }

    fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm:ss"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException){
                null
            }
        }
        return date
    }
}
