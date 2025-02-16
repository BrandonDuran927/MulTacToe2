package com.brandon.tictactoe.core.domain

import com.brandon.tictactoe.common.Result
import kotlinx.coroutines.flow.Flow


interface FirebaseRemoteDataSource {
    fun createRoom() : Flow<Result<Int>>
    fun retrieveRoomRef() : Flow<Result<Map<String, Map<String, Any>>>>
    fun retrieveRoomSpecs(id: Int) : Flow<Result<Map<String, Any>>>
    fun joinRoom(id: Int) : Flow<Result<Int>>
    fun leaveRoom(isPlayerCreator: Boolean, id: Int) : Flow<Result<Unit>>
    fun modifyMoves(id: Int, isPlayerTurn: Boolean, pos: Int, moveValue: Int, isPlayerCreator: Boolean) : Flow<Result<Unit>>

    fun requestGameReset(id: Int, playerNum: Int) : Flow<Result<Unit>>
    fun approveGameReset(id: Int, isApproved: Boolean) : Flow<Result<Unit>>
    fun resetRequester(id: Int, isApproved: Boolean) : Flow<Result<Unit>>

    fun requestPlayAgain(id: Int, playerNum: Int): Flow<Result<Unit>>
    fun playAgainRequester(id: Int, isApproved: Boolean, p1Move: Int): Flow<Result<Int>>
    fun approvePlayAgain(id: Int, isApproved: Boolean, roomCreator: Boolean): Flow<Result<Unit>>

    suspend fun resetMoves(id: Int, isApproved: Boolean, p1Move: Int) : Result<Unit>
}


