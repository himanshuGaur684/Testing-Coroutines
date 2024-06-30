package gaur.himanshu.testingcoroutines.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ColdDataSource : DataSource {
    override suspend fun emit(value: Int) {

    }

    override suspend fun counts(): Flow<Int> {
        return flowOf(1,2,3,4,5,6)
    }
}


