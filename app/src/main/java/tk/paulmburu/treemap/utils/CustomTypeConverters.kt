package tk.paulmburu.treemap.utils

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson


class CustomTypeConverters {
    @TypeConverter
    fun stringToGeoPoint(data: String?): GeoPoint {
        return Gson().fromJson(data, GeoPoint::class.java)
    }

    @TypeConverter
    public fun geoPointToString(geoPoint: GeoPoint?): String {
        return Gson().toJson(geoPoint)
    }
}