package dev.gaddal.echojournal.core.domain.logs.audio_log

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface LocalAudioLogDataSource {

    fun getAudioLogs(): Flow<List<AudioLog>>

    fun getAudioLogsWithTopics(): Flow<List<AudioLogWithTopics>>

    fun getAudioLogById(id: AudioLogId): Flow<AudioLog?>

    suspend fun upsertAudioLog(audioLog: AudioLog): Result<AudioLogId, DataError>

    suspend fun deleteAudioLogById(id: AudioLogId)
}

typealias AudioLogId = Int
