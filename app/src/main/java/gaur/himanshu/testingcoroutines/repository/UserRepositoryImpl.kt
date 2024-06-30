package gaur.himanshu.testingcoroutines.repository

import gaur.himanshu.testingcoroutines.datasource.DataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepositoryImpl(val dataSource: DataSource) : UserRepository {

    val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var list: MutableList<String>

    override fun databaseInitialization() {
        scope.launch {
            list = mutableListOf()
        }
    }

    override suspend fun fetchUsers(): List<String> = withContext(Dispatchers.IO) {
        this@UserRepositoryImpl.list
    }

    override fun insertDatabase(list: List<String>) {
        scope.launch {
            repeat(100000 * list.size) {}
            this@UserRepositoryImpl.list.addAll(list)
        }
    }

    override suspend fun emit(value: Int) {
        dataSource.emit(value)
    }

    override suspend fun getScores(): Flow<Int> {
        return dataSource.counts()
    }
    
}