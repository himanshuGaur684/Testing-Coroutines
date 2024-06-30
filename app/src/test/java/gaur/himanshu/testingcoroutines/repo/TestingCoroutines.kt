package gaur.himanshu.testingcoroutines.repo

import gaur.himanshu.testingcoroutines.datasource.ColdDataSource
import gaur.himanshu.testingcoroutines.datasource.HotDataSource
import gaur.himanshu.testingcoroutines.repository.UserRepositoryImpl
import gaur.himanshu.testingcoroutines.viewmodel.UserViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestingCoroutines {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun test_helloWorld() = runTest {

        val repo = UserRepositoryImpl(ColdDataSource())
        assertEquals("hello world", repo.helloWorld())


    }

    @Test
    fun test_repo() = runTest {

        val repo = UserRepositoryImpl(ColdDataSource(), StandardTestDispatcher(testScheduler))

        repo.databaseInitialization() // now

        repo.insertDatabase(listOf("a")) // now
        repo.insertDatabase(listOf("b")) // now
        repo.insertDatabase(listOf("c")) // now
        repo.insertDatabase(listOf("d")) // now
        repo.insertDatabase(listOf("e", "f", "g", "h")) // 100

        advanceUntilIdle()

        assertEquals(listOf("a", "b", "c", "d", "e", "f", "g", "h"), repo.fetchUsers().sorted())


    }


    @Test
    fun test_coldFlow() = runTest {
        val repo = UserRepositoryImpl(ColdDataSource())

        val list = repo.getScores().toList()

        assertEquals(list.get(0), 1)

    }

    @Test
    fun test_hotFlow() = runTest {
        val repo = UserRepositoryImpl(HotDataSource())

        val list = mutableListOf<Int>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repo.getScores().collectLatest {
                list.add(it)
            }
        }
        repo.emit(1)
        assertEquals(list.get(0), 1)
    }

    @Test
    fun test_viewmodel() = runTest {

        val viewModel = UserViewModel()

        StandardTestDispatcher()
        UnconfinedTestDispatcher()

        viewModel.getUser()


        assertEquals(listOf("a", "b"), viewModel.list.value)

    }


}


class MainDispatcherRule(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }

}
