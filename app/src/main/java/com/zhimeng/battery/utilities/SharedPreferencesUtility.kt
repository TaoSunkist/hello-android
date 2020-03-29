package com.zhimeng.battery.utilities

//import com.zhimeng.battery.app.TatameApplication
//import com.zhimeng.battery.data.UserStore


//fun <T> SharedPreferences.Editor.putObject(key: String, t: T) {
//    val gson = Gson()
//    val jsonString = gson.toJson(t)
//    val edit = BatteryApplication.CONTEXT!!.getSharedPreferences(UserStore.PREF_USER, Context.MODE_PRIVATE).edit()
//    edit.putString(key, jsonString)
//    edit.apply()
//}
//
//fun <T> SharedPreferences.getObject(key: String, typeToken: TypeToken<T>): T? {
//    val sp = BatteryApplication.CONTEXT!!.getSharedPreferences(UserStore.PREF_USER, Context.MODE_PRIVATE)
//    val jsonString = sp.getString(key, "")
//    val gson = GsonBuilder().setLenient().create()
//    var t: T
//    try {
//        t = gson.fromJson(jsonString, typeToken.type)
//    } catch (e: JsonSyntaxException) {
//        return null
//    }
//    return t
//}
