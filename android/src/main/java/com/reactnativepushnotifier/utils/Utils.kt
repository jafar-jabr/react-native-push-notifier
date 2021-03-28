package com.reactnativepushnotifier.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    private const val TAG = "Utils"
    fun timestampToUTC(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val date = Date((timestamp + calendar.timeZone.getOffset(timestamp)) * 1000)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    /**
     * send a JS event
     */
    fun sendEvent(context: ReactContext?, eventName: String?, body: Any?) {
        context?.getJSModule(RCTDeviceEventEmitter::class.java)?.emit(eventName!!, body)
                ?: Log.d(TAG, "Missing context - cannot send event!")
    }

    @Throws(JSONException::class)
    fun jsonObjectToWritableMap(jsonObject: JSONObject): WritableMap {
        val iterator = jsonObject.keys()
        val writableMap = Arguments.createMap()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject[key]
            if (value is Float || value is Double) {
                writableMap.putDouble(key, jsonObject.getDouble(key))
            } else if (value is Number) {
                writableMap.putInt(key, jsonObject.getInt(key))
            } else if (value is String) {
                writableMap.putString(key, jsonObject.getString(key))
            } else if (value is JSONObject) {
                writableMap.putMap(key, jsonObjectToWritableMap(jsonObject.getJSONObject(key)))
            } else if (value is JSONArray) {
                writableMap.putArray(key, jsonArrayToWritableArray(jsonObject.getJSONArray(key)))
            } else if (value === JSONObject.NULL) {
                writableMap.putNull(key)
            }
        }
        return writableMap
    }

    @Throws(JSONException::class)
    fun jsonArrayToWritableArray(jsonArray: JSONArray): WritableArray {
        val writableArray = Arguments.createArray()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray[i]
            if (value is Float || value is Double) {
                writableArray.pushDouble(jsonArray.getDouble(i))
            } else if (value is Number) {
                writableArray.pushInt(jsonArray.getInt(i))
            } else if (value is String) {
                writableArray.pushString(jsonArray.getString(i))
            } else if (value is JSONObject) {
                writableArray.pushMap(jsonObjectToWritableMap(jsonArray.getJSONObject(i)))
            } else if (value is JSONArray) {
                writableArray.pushArray(jsonArrayToWritableArray(jsonArray.getJSONArray(i)))
            } else if (value === JSONObject.NULL) {
                writableArray.pushNull()
            }
        }
        return writableArray
    }

    fun mapToWritableMap(value: Map<String?, Any?>): WritableMap {
        val writableMap = Arguments.createMap()
        for ((key, value1) in value) {
            mapPutValue(key, value1, writableMap)
        }
        return writableMap
    }

    private fun listToWritableArray(objects: List<Any>): WritableArray {
        val writableArray = Arguments.createArray()
        for (`object` in objects) {
            arrayPushValue(`object`, writableArray)
        }
        return writableArray
    }

    fun arrayPushValue(value: Any?, array: WritableArray) {
        if (value == null || value === JSONObject.NULL) {
            array.pushNull()
            return
        }
        val type = value.javaClass.name
        when (type) {
            "java.lang.Boolean" -> array.pushBoolean((value as Boolean?)!!)
            "java.lang.Long" -> {
                val longVal = value as Long
                array.pushDouble(longVal.toDouble())
            }
            "java.lang.Float" -> {
                val floatVal = value as Float
                array.pushDouble(floatVal.toDouble())
            }
            "java.lang.Double" -> array.pushDouble(value as Double)
            "java.lang.Integer" -> array.pushInt(value as Int)
            "java.lang.String" -> array.pushString(value as String?)
            "org.json.JSONObject$1" -> try {
                array.pushMap(jsonObjectToWritableMap(value as JSONObject))
            } catch (e: JSONException) {
                array.pushNull()
            }
            "org.json.JSONArray$1" -> try {
                array.pushArray(jsonArrayToWritableArray(value as JSONArray))
            } catch (e: JSONException) {
                array.pushNull()
            }
            else -> if (MutableList::class.java.isAssignableFrom(value.javaClass)) {
                array.pushArray(listToWritableArray(value as List<Any>))
            } else if (MutableMap::class.java.isAssignableFrom(value.javaClass)) {
                array.pushMap(mapToWritableMap(value as Map<String?, Any?>))
            } else {
                Log.d(TAG, "utils:arrayPushValue:unknownType:$type")
                array.pushNull()
            }
        }
    }

    fun mapPutValue(key: String?, value: Any?, map: WritableMap) {
        if (value == null || value === JSONObject.NULL) {
            map.putNull(key!!)
            return
        }
        val type = value.javaClass.name
        when (type) {
            "java.lang.Boolean" -> map.putBoolean(key!!, (value as Boolean?)!!)
            "java.lang.Long" -> {
                val longVal = value as Long
                map.putDouble(key!!, longVal.toDouble())
            }
            "java.lang.Float" -> {
                val floatVal = value as Float
                map.putDouble(key!!, floatVal.toDouble())
            }
            "java.lang.Double" -> map.putDouble(key!!, value as Double)
            "java.lang.Integer" -> map.putInt(key!!, value as Int)
            "java.lang.String" -> map.putString(key!!, value as String?)
            "org.json.JSONObject$1" -> try {
                map.putMap(key!!, jsonObjectToWritableMap(value as JSONObject))
            } catch (e: JSONException) {
                map.putNull(key!!)
            }
            "org.json.JSONArray$1" -> try {
                map.putArray(key!!, jsonArrayToWritableArray(value as JSONArray))
            } catch (e: JSONException) {
                map.putNull(key!!)
            }
            else -> if (MutableList::class.java.isAssignableFrom(value.javaClass)) {
                map.putArray(key!!, listToWritableArray(value as List<Any>))
            } else if (MutableMap::class.java.isAssignableFrom(value.javaClass)) {
                map.putMap(key!!, mapToWritableMap(value as Map<String?, Any?>))
            } else {
                Log.d(TAG, "utils:mapPutValue:unknownType:$type")
                map.putNull(key!!)
            }
        }
    }

    /**
     * Convert a ReadableMap to a WritableMap for the purposes of re-sending back to JS
     * TODO This is now a legacy util - internally uses RN functionality
     *
     * @param map ReadableMap
     * @return WritableMap
     */
    fun readableMapToWritableMap(map: ReadableMap?): WritableMap {
        val writableMap = Arguments.createMap()
        // https://github.com/facebook/react-native/blob/master/ReactAndroid/src/main/java/com/facebook/react/bridge/WritableNativeMap.java#L54
        writableMap.merge(map!!)
        return writableMap
    }

    /**
     * Convert a ReadableMap into a native Java Map
     * TODO This is now a legacy util - internally uses RN functionality
     *
     * @param readableMap ReadableMap
     * @return Map
     */
    fun recursivelyDeconstructReadableMap(readableMap: ReadableMap): Map<String, Any> {
        // https://github.com/facebook/react-native/blob/master/ReactAndroid/src/main/java/com/facebook/react/bridge/ReadableNativeMap.java#L216
        return readableMap.toHashMap()
    }

    /**
     * Convert a ReadableArray into a native Java Map
     * TODO This is now a legacy util - internally uses RN functionality
     *
     * @param readableArray ReadableArray
     * @return List<Object>
    </Object> */
    fun recursivelyDeconstructReadableArray(readableArray: ReadableArray): List<Any> {
        // https://github.com/facebook/react-native/blob/master/ReactAndroid/src/main/java/com/facebook/react/bridge/ReadableNativeArray.java#L175
        return readableArray.toArrayList()
    }

    /**
     * We need to check if app is in foreground otherwise the app will crash.
     * http://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
     *
     * @param context Context
     * @return boolean
     */
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return false
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName == packageName) {
                val reactContext: ReactContext
                reactContext = try {
                    context as ReactContext
                } catch (exception: ClassCastException) {
                    // Not react context so default to true
                    return true
                }
                return reactContext.lifecycleState == LifecycleState.RESUMED
            }
        }
        return false
    }

    fun getResId(ctx: Context, resName: String): Int {
        val resourceId = ctx
                .resources
                .getIdentifier(resName, "string", ctx.packageName)
        if (resourceId == 0) {
            Log.e(TAG, "resource $resName could not be found")
        }
        return resourceId
    }
}
