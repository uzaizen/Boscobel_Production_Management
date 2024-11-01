package com.example.boscobel_production_management

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
//import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MotionEventCompat
import com.example.boscobel_production_management.databinding.ActivityMainBinding

import com.example.boscobel_production_management.InputActivity
import com.example.boscobel_production_management.R
import com.example.boscobel_production_management.Task
import com.example.boscobel_production_management.TaskAdapter
import io.realm.kotlin.where

const val EXTRA_TASK = "com.example.boscobel_production_management.TASK"
public var menuswitch = 0

class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Log.d("uztest","fab clicked")
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        Log.d("uztest","phase0")

        // Realmの設定
        mRealm = Realm.getDefaultInstance()

        Log.d("uztest","phase0.5")
        mRealm.addChangeListener(mRealmListener)

        Log.d("uztest","phase1.0")

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this)

        Log.d("uztest","phase1.5")

        //ListViewをタップしたときの処理
        binding.listView1.setOnItemClickListener { parent, _, position, _ ->
            //入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        Log.d("uztest","phase2")

        //ListViewを長押ししたときの処理
        binding.listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            //ダイアログを表示する
            val builder = AlertDialog.Builder(this)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            Log.d("uztest","phase3")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        reloadListView()
    }


    private fun reloadListView() {
        //Realmデータベースから、「すべてのデータを取得して新しい日時順に並べた結果」を取得
//        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("sort1", Sort.ASCENDING)

        val taskRealmResults1 = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        val taskRealmResults2 = mRealm.where(Task::class.java).equalTo("flag",0.toInt()).findAll().sort("date", Sort.ASCENDING)

        val taskRealmResults3 = mRealm.where(Task::class.java).equalTo("flag",1.toInt()).findAll().sort("date", Sort.DESCENDING)

        val taskRealmResults4 = mRealm.where(Task::class.java).findAll().sort("date",Sort.DESCENDING).sort("title", Sort.ASCENDING)

//        val taskRealmResults5 = mRealm.where(Task::class.java).findAll().sum("amount")

        //上記の結果をTaskListとしてセットする
        when (menuswitch) {
            0 ->
                mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults1)
            1 ->
                mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults2)
            2 ->
                mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults3)
            3 ->
                mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults4)
            /*            4 ->
                            mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults5)

             */

        }
        //TaskのListView用のアダプタに渡す
        binding.listView1.adapter = mTaskAdapter

        //表示を更新するために、アダプタにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var returnVal = true
        when (item.itemId) {
            R.id.sort1_settings -> menuswitch = 0
            R.id.sort2_settings -> menuswitch = 1
            R.id.sort3_settings -> menuswitch = 2
            R.id.sort4_settings -> menuswitch = 3
//            R.id.sort5_settings -> menuswitch = 4
            else ->
                returnVal = super.onOptionsItemSelected(item)
        }
        reloadListView()
        return returnVal
    }
}