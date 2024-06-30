package gaur.himanshu.testingcoroutines.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class HotDataSource : DataSource {
    val sharedFlow = MutableSharedFlow<Int>()

    override suspend fun emit(value:Int) {
        sharedFlow.emit(value)
    }

    override suspend fun counts(): Flow<Int> {
        return sharedFlow
    }
}