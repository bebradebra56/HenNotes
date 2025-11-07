package com.hensof.noteplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hensof.noteplay.data.local.dao.NoteDao
import com.hensof.noteplay.data.local.dao.TemplateDao
import com.hensof.noteplay.data.local.entity.Converters
import com.hensof.noteplay.data.local.entity.NoteEntity
import com.hensof.noteplay.data.local.entity.TemplateEntity
import com.hensof.noteplay.data.model.NoteCategory

@Database(
    entities = [NoteEntity::class, TemplateEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HenNotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: HenNotesDatabase? = null

        fun getDatabase(context: Context): HenNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HenNotesDatabase::class.java,
                    "hen_notes_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate templates will be done in repository
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDefaultTemplates(): List<TemplateEntity> {
            return listOf(
                TemplateEntity(
                    title = "Daily Egg Collection",
                    content = "Eggs collected today: ___\nTotal count: ___\nNotes: ___",
                    category = NoteCategory.EGG_PRODUCTION
                ),
                TemplateEntity(
                    title = "Feed Purchase",
                    content = "Feed type: ___\nQuantity: ___ kg\nCost: $ ___\nSupplier: ___",
                    category = NoteCategory.FEED_NUTRITION
                ),
                TemplateEntity(
                    title = "Health Check",
                    content = "Chicken ID/Group: ___\nSymptoms: ___\nTreatment: ___\nVet visit: Yes/No",
                    category = NoteCategory.HEALTH
                ),
                TemplateEntity(
                    title = "Vaccination Record",
                    content = "Date: ___\nVaccine type: ___\nNumber of chickens: ___\nNext dose: ___",
                    category = NoteCategory.HEALTH
                ),
                TemplateEntity(
                    title = "Expense Record",
                    content = "Item: ___\nAmount: $ ___\nCategory: ___\nDate: ___",
                    category = NoteCategory.FINANCE
                ),
                TemplateEntity(
                    title = "Income Record",
                    content = "Source: ___\nAmount: $ ___\nQuantity sold: ___\nDate: ___",
                    category = NoteCategory.FINANCE
                )
            )
        }
    }
}

