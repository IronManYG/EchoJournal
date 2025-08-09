package dev.gaddal.echojournal.core.data.logs.audio_log

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogId
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.audio_log.LocalAudioLogDataSource
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

class OfflineFirstAudioLogRepository(
    private val localAudioLogDataSource: LocalAudioLogDataSource
) : AudioLogRepository {
    override fun getAudioLogs(): Flow<List<AudioLog>> {
        return localAudioLogDataSource.getAudioLogs()
    }

    override fun getAudioLogsWithTopics(): Flow<List<AudioLogWithTopics>> {
        return localAudioLogDataSource.getAudioLogsWithTopics()
    }

    override fun getAudioLogById(id: AudioLogId): Flow<AudioLog?> {
        return localAudioLogDataSource.getAudioLogById(id)
    }

    override suspend fun upsertAudioLog(audioLog: AudioLog): Result<AudioLogId, DataError> {
        return localAudioLogDataSource.upsertAudioLog(audioLog)
    }

    override suspend fun deleteAudioLogById(id: AudioLogId) {
        localAudioLogDataSource.deleteAudioLogById(id)
    }
}
