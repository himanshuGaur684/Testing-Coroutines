package gaur.himanshu.testingcoroutines

import gaur.himanshu.testingcoroutines.datasource.ColdDataSource
import gaur.himanshu.testingcoroutines.datasource.HotDataSource
import gaur.himanshu.testingcoroutines.repository.UserRepositoryImpl
import gaur.himanshu.testingcoroutines.viewmodel.UserViewModel
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RemoteRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_repository() = runTest(UnconfinedTestDispatcher()) {
        val repository = UserRepositoryImpl(ColdDataSource())
        repository.databaseInitialization()
        repository.insertDatabase(listOf("a"))
        repository.insertDatabase(listOf("b"))
        repository.insertDatabase(listOf("c"))
        repository.insertDatabase(listOf("d"))
        repository.insertDatabase(listOf("e", "f", "g", "h"))

        val data = repository.fetchUsers()
        advanceUntilIdle()
        TestCase.assertEquals(listOf("a", "b", "c", "d", "e", "f", "g", "h"), data.sorted())
    }

    @Test
    fun test_coldFlow() = runTest {
        val repository = UserRepositoryImpl(ColdDataSource())
        val list = repository.getScores().toList()
        TestCase.assertEquals(1, list.get(0))
    }

    @Test
    fun test_hotFlow() = runTest {
        val repository = UserRepositoryImpl(HotDataSource())
        val list = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository.getScores().collectLatest {
                list.add(it)
            }
        }
        repository.emit(1)
        repository.emit(2)
        repository.emit(3)
        repository.emit(4)
        repository.emit(5)
        repository.emit(6)
        TestCase.assertEquals(listOf(1, 2, 3, 4, 5, 6), list)

    }


    @Test
    fun test_viewModel() = runTest {
        val viewModel = UserViewModel()
        val users = viewModel.getUser()
        TestCase.assertEquals(listOf("a", "b"), viewModel.list.value)
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val testDispatchers: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatchers)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
