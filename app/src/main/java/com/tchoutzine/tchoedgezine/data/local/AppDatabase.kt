package com.tchoutzine.tchoedgezine.data.local

import androidx.room.*
import com.tchoutzine.tchoedgezine.data.model.Consultation
import com.tchoutzine.tchoedgezine.data.model.ConsultationType

@Database(entities = [Consultation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consultationDao(): ConsultationDao
}

class Converters {
    @TypeConverter fun fromType(t: ConsultationType) = t.name
    @TypeConverter fun toType(s: String) = ConsultationType.valueOf(s)
}
