package dev.gaddal.echojournal.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    /**
     * (Optional) Additional fields: color code, icon, description, etc.
     */
    val colorHex: String? = null
)
