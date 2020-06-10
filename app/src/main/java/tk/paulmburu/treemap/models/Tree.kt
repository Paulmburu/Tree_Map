package tk.paulmburu.treemap.models


import com.google.firebase.firestore.GeoPoint

data class Tree constructor(
    val timeStampString: Long = 0,
    val tree_id: String = "",
    val name: String = "",
    val tree_species: String = "",
    val region: String = "",
    val tree_image: String = "",
    val aborist_username: String = "",
    val arborist_email: String = "",
    val tree_geopoint: GeoPoint = GeoPoint(0.0,0.0),
    val treeDescription: String = ""
)