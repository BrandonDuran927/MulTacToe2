package com.brandon.tictactoe.ingame.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brandon.tictactoe.common.Result
import com.brandon.tictactoe.core.domain.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(private val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(GameStateUi())
    val state = _state.asStateFlow()

    private var roomListenerJob: Job? = null
    private var onResetListenerJob: Job? = null

    private val moves = listOf(
        // HORIZONTAL
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),

        // VERTICAL
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),

        // DIAGONAL
        listOf(2, 4, 6),
        listOf(0, 4, 8)
    )

    init {
        fetchRoom()
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.OnHomeScreen -> {
                viewModelScope.launch {
                    when (repository.leaveRoom(action.isPlayerCreator, action.roomID).first()) {
                        is Result.Success -> {
                            if (_state.value.playAgainIsActive && _state.value.roomID != null) {
                                when (repository.approvePlayAgain(
                                    _state.value.roomID!!,
                                    false,
                                    _state.value.roomCreator
                                )
                                    .first()) {
                                    is Result.Success -> {
                                        _state.update {
                                            it.copy(
                                                pendingRequestPlayAgain = false,
                                                playAgainIsActive = false
                                            )
                                        }
                                    }

                                    is Result.Failure -> println("error occurred")
                                }
                            }
                            onCleared()
                            _state.update {
                                it.copy(
                                    isWaitingForChallenger = false,
                                    isLeave = false,
                                    navigationEvent = NavigationEvent.ToHome,
                                    winner = 0,
                                    pendingRequestPlayAgain = false
                                )
                            }
                        }

                        is Result.Failure -> {
                            _state.update { it.copy(isError = true) }
                        }
                    }
                }
            }

            is GameAction.OnRoomIdCreatorChange -> {
                _state.update {
                    it.copy(
                        roomID = action.roomID,
                        roomCreator = action.isPlayerCreator,
                        p1Move = action.p1Move
                    )
                }
                // BECAUSE THIS IS THE FIRST ONE EXECUTED
                fetchRoom()
            }

            is GameAction.OnMoveChange -> {
                viewModelScope.launch {
                    val roomID = _state.value.roomID ?: 0
                    val isPlayerTurn = _state.value.isPlayerTurn
                    val isPlayerCreator = action.isPlayerCreator
                    val moveValue = action.moveValue
                    val pos = action.pos
                    if (roomID != 0) {
                        when (repository.modifyMoves(
                            id = roomID,
                            isPlayerTurn = isPlayerTurn,
                            moveValue = moveValue,
                            pos = pos,
                            isPlayerCreator = isPlayerCreator
                        ).first()) {
                            is Result.Success -> println("GameViewModel.GameAction.OnMoveChange: Result.Success")

                            is Result.Failure -> {
                                _state.update { it.copy(isError = true) }
                            }
                        }
                    }
                }
            }

            is GameAction.OnApproveResetRequestOrNot -> {
                viewModelScope.launch {
                    _state.value.roomID?.let {
                        when (repository.approveGameReset(
                            _state.value.roomID!!,
                            action.hasConfirmRequestReset
                        ).first()) {
                            is Result.Success -> {
                                _state.update { it.copy(isOtherPlayerRequestingReset = false) }
                            }

                            is Result.Failure -> {
                                _state.update {
                                    it.copy(
                                        isError = true,
                                        isOtherPlayerRequestingReset = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            GameAction.OnGameResetAttempt -> {
                viewModelScope.launch {
                    if (_state.value.roomID != null) {
                        when (repository.requestGameReset(
                            _state.value.roomID!!,
                            if (_state.value.roomCreator) 1 else 2
                        ).first()) {
                            is Result.Success -> {
                                _state.update {
                                    it.copy(
                                        playerReset = false,
                                        pendingRequestReset = true,
                                        thisPlayerReset = true
                                    )
                                }
                            }

                            is Result.Failure -> println("GameAction.OnGameResetAttempt Failed")
                        }
                    }
                }
            }

            GameAction.OnPlayerReset -> {
                _state.update { it.copy(playerReset = true) }
            }

            GameAction.OnResetRequest -> {
                _state.update { it.copy(playerReset = false) }
            }

            GameAction.OnWaitingForChallenger -> {
                _state.update { it.copy(isWaitingForChallenger = false) }
            }

            GameAction.OnClickLeave -> {
                _state.update { it.copy(isLeave = true) }
            }

            GameAction.OnResetLeave -> {
                _state.update { it.copy(isLeave = false) }
            }

            GameAction.OnEmptyMessageReset -> {
                _state.update { it.copy(messageReset = "", messagePlayAgain = "") }
            }

            GameAction.OnResetState -> {
                roomListenerJob?.cancel()
                onResetListenerJob?.cancel()
                _state.update { GameStateUi() }
            }

            GameAction.OnResetMoves -> {
                viewModelScope.launch {
                    if (state.value.roomID != null) {
                        val result = repository.resetMoves(state.value.roomID!!, true, state.value.p1Move)
                        when(result) {
                            is Result.Success -> println("Reset moves successful")
                            is Result.Failure -> println(result.exception.message.toString())
                        }
                    }
                }
            }

            GameAction.OnPlayAgain -> {
                viewModelScope.launch {
                    val roomID = _state.value.roomID
                    if (roomID != null) {
                        when (_state.value.playAgainIsActive) {
                            true -> { // note: it means the other player accepted it
                                when (repository.approvePlayAgain(
                                    _state.value.roomID!!,
                                    true,
                                    _state.value.roomCreator
                                )
                                    .first()) {
                                    is Result.Success -> {
                                        _state.update { GameStateUi().copy(pendingRequestPlayAgain = false, roomCreator = it.roomCreator, roomID = it.roomID) }
                                    }

                                    is Result.Failure -> println("error occurred")
                                }
                            }

                            false -> {  // note: it means the user player is has the ability to request to play again since there is no active request for play again
                                when (repository.requestPlayAgain(
                                    _state.value.roomID!!,
                                    if (_state.value.roomCreator) 1 else 2
                                ).first()) {
                                    is Result.Success -> {
                                        _state.update {
                                            it.copy(
                                                pendingRequestPlayAgain = true,
                                                winner = 0,
                                            )
                                        }
                                    }

                                    is Result.Failure -> _state.update { it.copy(isError = true) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: Reset the game if the player is waiting for other player when the other player leave while the admin stay (use reset functionality)


    private fun fetchRoom() {
        val roomID = _state.value.roomID

        roomListenerJob?.cancel()

        roomListenerJob = viewModelScope.launch {
            if (roomID != null) {
                repository.retrieveRoomSpecs(roomID)
                    .catch {
                        _state.update { it.copy(isError = true) }
                    }
                    .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                val roomCreator = _state.value.roomCreator
                                val doesRoomCreatorExist = filterRetrievePlayer1(result.data)
                                val roomNotFull =
                                    result.data.size != 8 && roomCreator
                                val playerResetRequester = // REQUESTER OF RESET GAME
                                    filterRetrievePlayerResetRequester(result.data)
                                val playerPlayAgainRequester =  // REQUESTER OF PLAY AGAIN
                                    filterRetrievePlayerPlayAgainRequester(result.data)
                                when {
                                    !doesRoomCreatorExist && !roomCreator -> {
                                        _state.update {
                                            it.copy(
                                                navigationEvent = NavigationEvent.ToHome,
                                                isLeave = false
                                            )
                                        }
                                        roomListenerJob?.cancel()
                                    }

                                    doesRoomCreatorExist -> {
                                        val playerTurn =
                                            result.data["Player 1"].toString().toInt()
                                        val moves = filterRetrieveMoves(
                                            result.data
                                        )
                                        val winner = determineWinner(moves)

                                        val resetGameValue =
                                            filterRetrieveOnReset(result.data)  // VALUE OF 'ON RESET'
                                        val playAgainValue =
                                            filterRetrieveOnPlayAgain(result.data)  // VALUE OF 'ON PLAY AGAIN'

                                        _state.update { it.copy(playAgainIsActive = playerPlayAgainRequester != 0) }
                                        val isResetRequestFromThisPlayer = when {
                                            _state.value.roomCreator && playerResetRequester == 1 -> true
                                            !_state.value.roomCreator && playerResetRequester == 2 -> true
                                            playerResetRequester == 0 -> null
                                            else -> false
                                        }

                                        when (winner) {
                                            1 -> _state.update { it.copy(winner = 1) }
                                            2 -> _state.update { it.copy(winner = 2) }
                                            else -> _state.update { it.copy(winner = 0) }
                                        }

                                        when (playAgainValue) {
                                            0 -> {
                                                if (_state.value.playAgainIsActive) {
                                                    _state.update {
                                                        it.copy(
                                                            pendingRequestPlayAgain = false,
                                                            playAgainIsActive = false,
                                                            winner = 0,
                                                            messagePlayAgain = "Player did not accept to play again"
                                                        )
                                                    }
                                                }
                                            }

                                            2 -> {
                                                if (_state.value.playAgainIsActive && roomID != 0) {
                                                    when (val data = repository.playAgainRequester(roomID, true, _state.value.p1Move)
                                                        .first()) {
                                                        is Result.Success -> {
                                                            println("Player 1 move: ${data.data}")
                                                            _state.update { GameStateUi()
                                                                .copy(
                                                                    pendingRequestPlayAgain = false,
                                                                    roomCreator = it.roomCreator,
                                                                    roomID = it.roomID,
                                                                    p1Move = data.data
                                                                )
                                                            }
                                                        }

                                                        is Result.Failure -> println("error occurred")
                                                    }
                                                }
                                            }
                                        }

                                        _state.update { it.copy(thisPlayerReset = isResetRequestFromThisPlayer) }
                                        _state.value.thisPlayerReset?.let { isThisPlayerReset ->
                                            when (resetGameValue) {
                                                0 -> {
                                                    if (isThisPlayerReset) {
                                                        _state.update {
                                                            it.copy(
                                                                pendingRequestReset = false,
                                                                messageReset = "Other player did not accept the reset"
                                                            )
                                                        }
                                                        repository.resetRequester(
                                                            roomID,
                                                            isApproved = false
                                                        ).first()
                                                    }
                                                }

                                                1 -> {
                                                    if (!isThisPlayerReset) {
                                                        _state.update {
                                                            it.copy(isOtherPlayerRequestingReset = true)
                                                        }
                                                    }
                                                }

                                                2 -> {
                                                    if (isThisPlayerReset) {
                                                        _state.update {
                                                            it.copy(pendingRequestReset = false)
                                                        }
                                                        repository.resetRequester(
                                                            id = roomID,
                                                            isApproved = true
                                                        ).first()
                                                    }
                                                }
                                            }
                                        }

                                        _state.update {
                                            it.copy(
                                                playerMoves = moves,
                                                isWaitingForChallenger = roomNotFull,
                                                isPlayerTurn = if (roomCreator) playerTurn == 1 else playerTurn != 1
                                            )
                                        }
                                    }
                                }
                            }

                            is Result.Failure -> _state.update { it.copy(isError = true) }
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomListenerJob?.cancel()
    }

    private fun determineWinner(board: List<Int>): Int {
        return when {
            moves.any { combination -> combination.all { board[it] == 1 } } -> 1
            moves.any { combination -> combination.all { board[it] == 2 } } -> 2
            else -> 0
        }
    }

    /*
       Checks whether the player 1 still exists
     */
    private fun filterRetrievePlayer1(rawRoomRef: Map<String, Any>): Boolean {
        rawRoomRef.mapKeys { (keys, _) ->
            if (keys == "ID") {
                return true
            }
        }
        return false
    }

    /*
       Retrieve the Player requester value
     */
    private fun filterRetrievePlayerResetRequester(rawRoomRef: Map<String, Any>): Int {
        rawRoomRef.mapKeys { (keys, values) ->
            if (keys == "Player reset requester") {
                return values.toString().toInt()
            }
        }
        return 0
    }

    private fun filterRetrievePlayerPlayAgainRequester(rawRoomRef: Map<String, Any>): Int {
        rawRoomRef.mapKeys { (keys, values) ->
            if (keys == "Player play again requester") {
                return values.toString().toInt()
            }
        }
        return 0
    }

    private fun filterRetrieveOnReset(rawRoomRef: Map<String, Any>): Int {
        rawRoomRef.mapKeys { (keys, values) ->
            if (keys == "On reset") {
                return values.toString().toInt()
            }
        }
        return 0
    }

    private fun filterRetrieveOnPlayAgain(rawRoomRef: Map<String, Any>): Int {
        rawRoomRef.mapKeys { (keys, values) ->
            if (keys == "On play again") {
                return values.toString().toInt()
            }
        }
        return 0
    }

    private fun filterRetrieveMoves(
        rawRoomRef: Map<String, Any>
    ): List<Int> {
        val retrieveMoves = rawRoomRef["Moves"] as List<*>
        val movesAsList = retrieveMoves.mapNotNull {
            it?.toString()?.toInt()
        }
        return movesAsList
    }
}
