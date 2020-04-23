package tk.paulmburu.treemap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class DatabaseTrees constructor(
    @PrimaryKey
    val name: String,
    val tree_image: String,
    val hero: String,
    val hero_email: String,
    val tree_geopoint: LatLng,
    val time_planted: String
)

@Entity
data class User constructor(
    @PrimaryKey
    val hero_name: String,
    val hero_email: String,
    val tree_geopoint: LatLng,
    val timePlanted: String
)
