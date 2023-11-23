package es.unex.giiis.asee.tiviclone.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord

@Database(entities = [VideoRecord::class], version = 1)
abstract class TotalEmergencyDatabase : RoomDatabase() {

    abstract fun videoDAO(): VideoRecordDAO

    companion object {
        private var INSTANCE: TotalEmergencyDatabase? = null

        fun getInstance(context: Context): TotalEmergencyDatabase? {
            if(INSTANCE == null){
                synchronized(TotalEmergencyDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context,
                        TotalEmergencyDatabase::class.java, "totalemergency.db"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}