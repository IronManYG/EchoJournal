package dev.gaddal.echojournal.core.domain.logs.audio_log

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface AudioLogRepository {

    fun getAudioLogs(): Flow<List<AudioLog>>

    fun getAudioLogById(id: AudioLogId): Flow<AudioLog?>

    suspend fun upsertAudioLog(audioLog: AudioLog): EmptyResult<DataError>

    suspend fun deleteAudioLogById(id: AudioLogId)
}