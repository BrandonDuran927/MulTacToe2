package com.brandon.tictactoe.core.domain

import com.brandon.tictactoe.common.Result
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun createRoom() : Flow<Result<Int>>
    suspend fun retrieveRoomRef() : Flow<Result<Map<String, Map<String, Any>>>>
    suspend fun retrieveRoomSpecs(id: Int) :Flow<Result<Map<String, Any>>>
    suspend fun joinRoom(id: Int) : Flow<Result<Int>>
    suspend fun leaveRoom(isPlayerCreator: Boolean, id: Int) : Flow<Result<Unit>>
    suspend fun modifyMoves(id: Int, isPlayerTurn: Boolean, pos: Int, moveValue: Int, isPlayerCreator: Boolean) : Flow<Result<Unit>>

    suspend fun requestGameReset(id: Int, playerNum: Int) : Flow<Result<Unit>>
    suspend fun approveGameReset(id: Int, isApproved: Boolean) : Flow<Result<Unit>>
    suspend fun resetRequester(id: Int, isApproved: Boolean) : Flow<Result<Unit>>

    suspend fun requestPlayAgain(id: Int, playerNum: Int): Flow<Result<Unit>>
    suspend fun playAgainRequester(id: Int, isApproved: Boolean, p1Move: Int): Flow<Result<Int>>
    suspend fun approvePlayAgain(id: Int, isApproved: Boolean, roomCreator: Boolean): Flow<Result<Unit>>

    suspend fun resetMoves(id: Int, isApproved: Boolean, p1Move: Int) : Result<Unit>
}