package es.unex.giiis.asee.tiviclone.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.unex.giiis.asee.tiviclone.data.model.Contact
import es.unex.giiis.asee.tiviclone.data.model.User
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord

@Database(entities = [User::class, VideoRecord::class, Contact::class], version = 1)
abstract class TotalEmergencyDatabase : RoomDatabase() {

    abstract fun videoDAO(): VideoRecordDAO
    abstract fun userDao(): UserDAO
    abstract fun contactDAO(): ContactDAO

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