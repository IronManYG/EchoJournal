package dev.gaddal.echojournal.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index // Import Index annotation
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    indices = [
        Index(value = ["name"], unique = true) // 1. Add Index annotation with unique = true
    ],
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    /**
     * (Optional) Additional fields: color code, icon, description, etc.
     */
    val colorHex: String? = null
)
