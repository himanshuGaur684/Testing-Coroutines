package gaur.himanshu.testingcoroutines.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun databaseInitialization()

    suspend fun fetchUsers(): List<String>

    fun insertDatabase(list: List<String>)

    suspend fun emit(value:Int)

    suspend fun getScores(): Flow<Int>

}



