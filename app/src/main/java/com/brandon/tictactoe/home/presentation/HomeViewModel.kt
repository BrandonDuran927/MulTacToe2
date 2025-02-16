package com.brandon.tictactoe.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brandon.tictactoe.common.Result
import com.brandon.tictactoe.core.domain.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(HomeStateUi())
    val state = _state
        .onStart { fetchRoom() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeStateUi()
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnClickJoin -> {
                _state.update { it.copy(isJoin = true) }
            }

            HomeAction.OnJoin -> {
                if (_state.value.listOfRooms.isEmpty()) {
                    _state.update { it.copy(isError = true) }
                } else {
                    viewModelScope.launch {
                        when (val result = repository.retrieveRoomRef().first()) {
                            is Result.Success -> {
                                val listOfRoom = _state.value.listOfRooms
                                val userInputID = _state.value.joinRoomID
                                val filteredRoomRef = filterRemoveMoves(result.data)
                                val mapValuesToInt = filteredRoomRef.mapNotNull { map ->
                                    map.value.mapValues { (_, value) -> value.toString().toInt() }
                                }
                                val roomNotFull = roomChecker(
                                    mapValuesToInt,
                                    userInputID
                                )
                                val roomExist =
                                    if (userInputID.isNotEmpty()) listOfRoom.contains(userInputID.toInt()) else false
                                if (roomExist && roomNotFull) {
                                    when (val data = repository.joinRoom(userInputID.toInt()).first()) {
                                        is Result.Success -> _state.update {
                                            it.copy(
                                                isError = false,
                                                isJoin = false,
                                                roomID = userInputID.toInt(),
                                                joinRoomID = "",
                                                isPlayerCreator = false,
                                                navigationEvent = NavigationEvent.ToInGame(
                                                    userInputID,
                                                    false,
                                                    data.data
                                                )
                                            )
                                        }

                                        is Result.Failure -> _state.update {
                                            it.copy(
                                                isError = true,
                                                isJoin = false,
                                                joinRoomID = ""
                                            )
                                        }
                                    }
                                } else {
                                    _state.update {
                                        it.copy(
                                            isError = true,
                                            isJoin = false,
                                            joinRoomID = ""
                                        )
                                    }
                                }
                            }

                            is Result.Failure -> {
                                _state.update { it.copy(isError = true) }

                            }
                        }
                    }
                }
            }

            is HomeAction.OnRoomCreated -> {
                _state.update { it.copy(isLoading = true) }

                viewModelScope.launch {
                    when (val generatedID = repository.createRoom().first()) {
                        is Result.Success -> {
                            delay(2000L)
                            _state.update {
                                it.copy(
                                    roomID = generatedID.data,
                                    isLoading = false,
                                    isError = false,
                                    isPlayerCreator = true,
                                    navigationEvent = NavigationEvent.ToInGame(
                                        generatedID.data.toString(),
                                        true,
                                        0
                                    )
                                )
                            }
                        }

                        is Result.Failure -> _state.update { it.copy(isError = false) }
                    }
                }
            }

            HomeAction.OnResetRoomID -> {
                viewModelScope.launch {
                    if (_state.value.roomID != null) {
                        _state.update { it.copy(roomID = null, joinRoomID = "", isError = false) }
                    }
                }
            }

            HomeAction.OnResetJoin -> {
                _state.update { it.copy(isJoin = false, joinRoomID = "") }
            }

            is HomeAction.OnJoinRoomIdChange -> {
                _state.update { it.copy(joinRoomID = action.joinRoomId) }
            }

            HomeAction.OnResetError -> {
                _state.update { it.copy(isError = false) }
            }

            HomeAction.OnResetState -> _state.update { HomeStateUi() }
        }
    }

    private fun fetchRoom() {
        viewModelScope.launch {
            repository.retrieveRoomRef().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val filteredRoomRef = filterRemoveMoves(result.data)
                        _state.update {
                            it.copy(listOfRooms = filteredRoomRef.mapNotNull { map ->
                                map.value["ID"]?.toString()?.toInt()
                            })
                        }
                    }

                    is Result.Failure -> {
                        _state.update { it.copy(isError = true) }
                    }
                }
            }
        }
    }

    private fun filterRemoveMoves(rawRoomRef: Map<String, Map<String, Any>>): Map<String, Map<String, Any>> {
        val filteredRoomRef = rawRoomRef.mapValues { (_, roomData) ->
            roomData.filterKeys { it != "Moves" }
        }

        return filteredRoomRef
    }

    private fun filterRoomId(roomRef: List<Map<String, Any>>?): List<Int> {
        val tmp1 = roomRef?.map { map ->
            map["ID"].toString().toInt()
        } ?: emptyList()
        return tmp1
    }

    private fun roomChecker(roomRef: List<Map<String, Int>>?, id: String): Boolean {
        val retrievedRooms = filterRoomId(roomRef)
        if (retrievedRooms.contains(id.toInt())) {
            roomRef?.forEach { map ->
                if (map.getValue("ID") == id.toInt()) {
                    return map.size != 7
                }
            }
        }
        return false
    }
}
