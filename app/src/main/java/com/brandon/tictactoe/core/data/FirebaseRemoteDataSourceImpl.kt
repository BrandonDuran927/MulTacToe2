package com.brandon.tictactoe.core.data

import android.util.Log
import com.brandon.tictactoe.common.Result
import com.brandon.tictactoe.core.domain.FirebaseRemoteDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseRemoteDataSourceImpl(private val firebaseDatabase: FirebaseDatabase) :
    FirebaseRemoteDataSource {
    override fun createRoom(): Flow<Result<Int>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                val generatedID = (1000..9999).random() // Generate a unique number for every room

                roomRef.child(generatedID.toString()).get().addOnSuccessListener { snapShot ->
                    if (snapShot.exists()) {
                        createRoom()
                    } else {
                        roomRef.child(generatedID.toString())
                            .setValue(
                                mapOf(
                                    "Player 1" to 1,
                                    "ID" to generatedID,
                                    "Moves" to mapOf(
                                        "0" to 0, "1" to 0, "2" to 0,
                                        "3" to 0, "4" to 0, "5" to 0,
                                        "6" to 0, "7" to 0, "8" to 0
                                    ),
                                    // RESET REQUESTER
                                    "Player reset requester" to 0,
                                    "On reset" to 0,

                                    // PLAY AGAIN REQUESTER
                                    "Player play again requester" to 0,
                                    "On play again" to 0
                                )
                            )
                    }
                }
                emit(Result.Success(generatedID))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }


    override fun retrieveRoomRef(): Flow<Result<Map<String, Map<String, Any>>>> {
        return callbackFlow {
            val reference = firebaseDatabase.getReference("rooms")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rawRoomRef = snapshot.getValue<Map<String, Map<String, Any>>>()
                    if (rawRoomRef != null) {
                        trySend(Result.Success(rawRoomRef))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
            }
        }
    }

    override fun retrieveRoomSpecs(id: Int): Flow<Result<Map<String, Any>>> {
        return callbackFlow {
            val reference = firebaseDatabase.getReference("rooms").child(id.toString())
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rawRoomSpecs = snapshot.getValue<Map<String, Any>>()
                    if (rawRoomSpecs != null) {
                        trySend(Result.Success(rawRoomSpecs))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
            }
        }
    }

    override fun joinRoom(id: Int): Flow<Result<Int>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                val randomMoveGenerator: Int = Random.nextInt(0, 2)
                val myMap = mapOf(
                    "Player 1" to if (randomMoveGenerator == 1) 0 else 1,
                    "Player 2" to if (randomMoveGenerator == 1) 1 else 0
                )
                roomRef.child(id.toString()).updateChildren(
                    myMap
                )

                emit(Result.Success(myMap["Player 1"]!!))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun leaveRoom(isPlayerCreator: Boolean, id: Int): Flow<Result<Unit>> {
        return flow {
            try {
                if (isPlayerCreator) {
                    firebaseDatabase.getReference().child("rooms").child(id.toString())
                        .child("ID").removeValue()
                        .await()
                    firebaseDatabase.getReference("rooms").child(id.toString()).removeValue()
                        .await()
                } else {
                    firebaseDatabase.getReference().child("rooms").child(id.toString())
                        .child("Player 2").removeValue().await()
                }
                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun modifyMoves(
        id: Int,
        isPlayerTurn: Boolean,
        pos: Int,
        moveValue: Int,
        isPlayerCreator: Boolean
    ): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                if (isPlayerTurn && isPlayerCreator) {
                    roomRef.child("$id/Moves/$pos")
                        .setValue(moveValue).await()
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player 1" to 0,
                            "Player 2" to 1,
                        )
                    )
                } else if (isPlayerTurn) {
                    roomRef.child("$id/Moves/$pos")
                        .setValue(moveValue).await()
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player 1" to 1,
                            "Player 2" to 0,
                        )
                    )
                }
                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun requestPlayAgain(id: Int, playerNum: Int): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                roomRef.child(id.toString()).updateChildren(
                    mapOf("On play again" to 1)
                ).await()

                if (playerNum == 1) {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player play again requester" to 1
                        )
                    ).await()
                } else {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player play again requester" to 2
                        )
                    ).await()
                }

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun requestGameReset(id: Int, playerNum: Int): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                roomRef.child(id.toString()).updateChildren(
                    mapOf(
                        "On reset" to 1
                    )
                ).await()

                if (playerNum == 1) {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player reset requester" to 1
                        )
                    ).await()
                } else {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player reset requester" to 2
                        )
                    ).await()
                }

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun approveGameReset(id: Int, isApproved: Boolean): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                if (isApproved) {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "On reset" to 2,
                        )
                    ).await()
                } else {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "On reset" to 0
                        )
                    ).await()
                }
                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun resetRequester(id: Int, isApproved: Boolean): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                if (isApproved) {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player reset requester" to 0,
                            "Moves" to mapOf(
                                "0" to 0, "1" to 0, "2" to 0,
                                "3" to 0, "4" to 0, "5" to 0,
                                "6" to 0, "7" to 0, "8" to 0
                            )
                        )
                    ).await()
                } else {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player reset requester" to 0
                        )
                    ).await()
                }

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun playAgainRequester(id: Int, isApproved: Boolean, p1Move: Int): Flow<Result<Int>> {  // Player requester
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                val myMap = mapOf(
                    "Player 1" to if (p1Move == 1) 0 else 1,
                    "Player 2" to if (p1Move == 1) 1 else 0
                )
                if (isApproved) {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Moves" to mapOf(
                                "0" to 0, "1" to 0, "2" to 0,
                                "3" to 0, "4" to 0, "5" to 0,
                                "6" to 0, "7" to 0, "8" to 0
                            ),
                            "Player 1" to myMap.getValue("Player 1"),
                            "Player 2" to myMap.getValue("Player 2"),

                            // RESET REQUESTER
                            "Player reset requester" to 0,
                            "On reset" to 0,

                            // PLAY AGAIN REQUESTER
                            "Player play again requester" to 0,
                            "On play again" to 0
                        )
                    ).await()
                } else {
                    roomRef.child(id.toString()).updateChildren(
                        mapOf(
                            "Player play again requester" to 0
                        )
                    ).await()
                }

                emit(Result.Success(myMap["Player 1"]!!))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun approvePlayAgain(id: Int, isApproved: Boolean, roomCreator: Boolean): Flow<Result<Unit>> {
        return flow {
            try {
                val roomRef = firebaseDatabase.getReference("rooms")
                when(isApproved) {
                    true -> {
                        roomRef.child(id.toString()).updateChildren(
                            mapOf(
                                "On play again" to 2,
                            )
                        ).await()
                    }
                    false -> {
                        if (roomCreator) {
                            firebaseDatabase.getReference().child("rooms").child(id.toString())
                                .child("ID").removeValue()
                                .await()
                            firebaseDatabase.getReference("rooms").child(id.toString()).removeValue()
                                .await()
                        } else {
                            roomRef.child(id.toString()).updateChildren(
                                mapOf(
                                    "On play again" to 0
                                )
                            ).await()
                        }
                    }
                }
                emit(Result.Success(Unit))
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override suspend fun resetMoves(id: Int, isApproved: Boolean, p1Move: Int): Result<Unit> {
        return try {
            val roomRef = firebaseDatabase.getReference("rooms")

            val updateMap = mapOf(
                "Player 1" to 0,
                "Moves" to mapOf(
                    "0" to 0, "1" to 0, "2" to 0,
                    "3" to 0, "4" to 0, "5" to 0,
                    "6" to 0, "7" to 0, "8" to 0
                )
            )

            roomRef.child(id.toString()).updateChildren(updateMap).await()

            // Logging
            println("Reset moves is triggered for room ID: $id")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error resetting moves: ${e.message}")
            Result.Failure(e)
        }
    }
}