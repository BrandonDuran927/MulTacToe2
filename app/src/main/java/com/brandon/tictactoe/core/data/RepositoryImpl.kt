package com.brandon.tictactoe.core.data

import com.brandon.tictactoe.common.Result
import com.brandon.tictactoe.core.domain.FirebaseRemoteDataSource
import com.brandon.tictactoe.core.domain.Repository
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val remoteDataSource: FirebaseRemoteDataSource) : Repository {
    override suspend fun createRoom(): Flow<Result<Int>> {
        return remoteDataSource.createRoom()
    }

    override suspend fun retrieveRoomRef(): Flow<Result<Map<String, Map<String, Any>>>> {
        return remoteDataSource.retrieveRoomRef()
    }

    override suspend fun retrieveRoomSpecs(id: Int): Flow<Result<Map<String, Any>>> {
        return remoteDataSource.retrieveRoomSpecs(id)
    }

    override suspend fun joinRoom(id: Int): Flow<Result<Int>> {
        return remoteDataSource.joinRoom(id)
    }

    override suspend fun leaveRoom(isPlayerCreator: Boolean, id: Int): Flow<Result<Unit>> {
        return remoteDataSource.leaveRoom(isPlayerCreator, id)
    }

    override suspend fun modifyMoves(
        id: Int,
        isPlayerTurn: Boolean,
        pos: Int,
        moveValue: Int,
        isPlayerCreator: Boolean
    ): Flow<Result<Unit>> {
        return remoteDataSource.modifyMoves(id, isPlayerTurn, pos, moveValue, isPlayerCreator)
    }

    override suspend fun requestGameReset(id: Int, playerNum: Int): Flow<Result<Unit>> {
        return remoteDataSource.requestGameReset(id, playerNum)
    }

    override suspend fun approveGameReset(id: Int, isApproved: Boolean): Flow<Result<Unit>> {
        return remoteDataSource.approveGameReset(id, isApproved)
    }

    override suspend fun resetRequester(id: Int, isApproved: Boolean): Flow<Result<Unit>> {
        return remoteDataSource.resetRequester(id, isApproved)
    }

    override suspend fun requestPlayAgain(id: Int, playerNum: Int): Flow<Result<Unit>> {
        return remoteDataSource.requestPlayAgain(id, playerNum)
    }

    override suspend fun playAgainRequester(id: Int, isApproved: Boolean, p1Move: Int): Flow<Result<Int>> {
        return remoteDataSource.playAgainRequester(id, isApproved, p1Move)
    }

    override suspend fun approvePlayAgain(id: Int, isApproved: Boolean, roomCreator: Boolean): Flow<Result<Unit>> {
        return remoteDataSource.approvePlayAgain(id, isApproved, roomCreator)
    }

    override suspend fun resetMoves(id: Int, isApproved: Boolean, p1Move: Int): Result<Unit> {
        return remoteDataSource.resetMoves(id, isApproved, p1Move)
    }
}