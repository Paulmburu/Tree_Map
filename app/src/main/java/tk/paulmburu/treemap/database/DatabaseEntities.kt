package tk.paulmburu.treemap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import tk.paulmburu.treemap.utils.CustomTypeConverters

@Entity
data class DatabaseTrees constructor(
    @PrimaryKey
    val timeStampString: Long = 0,
    val tree_id: String = "",
    val name: String = "",
    val tree_image: String = "",
    val aborist_username: String = "",
    val arborist_email: String = "",
     val tree_geopoint: GeoPoint ,
    val treeDescription: String = ""
)

@Entity
data class User constructor(
    @PrimaryKey
    val hero_name: String,
    val hero_email: String,
    val tree_geopoint: LatLng,
    val timePlanted: String
)
