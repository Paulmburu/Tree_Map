package tk.paulmburu.moviesreview.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import tk.paulmburu.treemap.database.DatabaseTrees
import tk.paulmburu.treemap.utils.CustomTypeConverters

@Dao
interface TreesDao {

    // SQL @Query getTrees() function that returns a List of DatabaseTreesResult livedata.
    @Query("select * from databasetrees")
    fun getTrees(): LiveData<List<DatabaseTrees>>

    // SQL @Insert insertAll() that replaces on conflict (or upsert).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tree: DatabaseTrees)

    // SQL @Query deleteMovies() function that deletes all movies
    @Query("DELETE FROM databasetrees")
    fun deleteAllTrees()
}

// An abstract MoviesDatabase class that extends RoomDatabase.
@Database(entities = [DatabaseTrees::class], version = 1)
@TypeConverters(CustomTypeConverters::class)
abstract class TreesDatabase : RoomDatabase() {
    abstract val treesDao: TreesDao
}


private lateinit var INSTANCE: TreesDatabase

// getDatabase() returns the MoviesDatabase INSTANCE
fun getDatabase(context: Context): TreesDatabase {
    synchronized(TreesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                TreesDatabase::class.java,
                "trees").build()

        }
    }
    return INSTANCE

}