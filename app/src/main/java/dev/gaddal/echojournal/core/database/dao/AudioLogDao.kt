package dev.gaddal.echojournal.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.gaddal.echojournal.core.database.entity.AudioLogEntity
import dev.gaddal.echojournal.core.database.entity.AudioLogWithTopicsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioLogDao {

    @Upsert
    suspend fun upsertAudioLog(audioLog: AudioLogEntity)

    @Query("SELECT * FROM audio_logs")
    fun getAudioLogs(): Flow<List<AudioLogEntity>>

    @Transaction
    @Query("SELECT * FROM audio_logs WHERE archived = 0")
    fun getAudioLogsWithTopics(): Flow<List<AudioLogWithTopicsRelation>>

    @Query("SELECT * FROM audio_logs WHERE id = :id")
    fun getAudioLogById(id: Int): Flow<AudioLogEntity?>

    @Query("SELECT * FROM audio_logs WHERE archived = 1")
    fun getArchivedAudioLogs(): Flow<List<AudioLogEntity>>

    @Query("DELETE FROM audio_logs WHERE id = :id")
    suspend fun deleteAudioLogById(id: Int)

    @Query("DELETE FROM audio_logs")
    suspend fun deleteAllAudioLogs()
}