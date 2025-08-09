package dev.gaddal.echojournal.core.database.local_data_source

import android.database.sqlite.SQLiteFullException
import dev.gaddal.echojournal.core.database.dao.AudioLogDao
import dev.gaddal.echojournal.core.database.mappers.toAudioLog
import dev.gaddal.echojournal.core.database.mappers.toAudioLogEntity
import dev.gaddal.echojournal.core.database.mappers.toAudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogId
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.audio_log.LocalAudioLogDataSource
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalAudioLogDataSource(
    private val audioLogDao: AudioLogDao
) : LocalAudioLogDataSource {
    override fun getAudioLogs(): Flow<List<AudioLog>> {
        return audioLogDao.getAudioLogs()
            .map { audioLogsEntities ->
                audioLogsEntities.map { it.toAudioLog() }
            }
    }

    override fun getAudioLogsWithTopics(): Flow<List<AudioLogWithTopics>> {
        return audioLogDao.getAudioLogsWithTopics()
            .map { list -> list.map { it.toAudioLogWithTopics() } }
    }

    override fun getAudioLogById(id: AudioLogId): Flow<AudioLog?> {
        return audioLogDao.getAudioLogById(id).map { it?.toAudioLog() }
    }

    override suspend fun upsertAudioLog(audioLog: AudioLog): Result<AudioLogId, DataError> {
        return try {
            val entity = audioLog.toAudioLogEntity()
            val audioLogId = audioLogDao.upsertAudioLog(entity)
            Result.Success(audioLogId.toInt())
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteAudioLogById(id: AudioLogId) {
        audioLogDao.deleteAudioLogById(id)
    }
}
