package com.example.boscobel_production_management

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Task : RealmObject(), Serializable {
    var date: Date = Date()     // 日時
    var title: String = ""      // タイトル
    var amount : Int = 0       //数量
    var dimension : String = ""   //単位
    var flag : Int = 0  //Done or NotDone flag
    var comment: String = " "

    // idをプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}