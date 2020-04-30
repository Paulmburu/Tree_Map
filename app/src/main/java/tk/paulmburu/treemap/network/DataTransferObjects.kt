package tk.paulmburu.treemap.network

import com.google.firebase.firestore.GeoPoint
import tk.paulmburu.treemap.database.DatabaseTrees
import tk.paulmburu.treemap.models.Tree

data class NetworkTreeContainer(val  results : List<Tree>)

// that returns an array of <DatabaseMovieResult>.
fun NetworkTreeContainer.asDatabaseModel(): Array<DatabaseTrees> {
    return results.map {
        DatabaseTrees (
            timeStampString = it.timeStampString,
            tree_id = it.tree_id,
            name = it.name,
            tree_image = it.tree_image,
            aborist_username = it.aborist_username,
            arborist_email = it.arborist_email,
            tree_geopoint = it.tree_geopoint,
            treeDescription = it.treeDescription)
    }.toTypedArray()
}


