package tk.paulmburu.treemap.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

data class Tree constructor(
    val tree_id: String,
    val name: String,
    val tree_image: String,
    val aborist_username: String,
    val arborist_email: String,
    val tree_geopoint: GeoPoint,
    val timeStampString: Long,
    val treeDescription: String
)