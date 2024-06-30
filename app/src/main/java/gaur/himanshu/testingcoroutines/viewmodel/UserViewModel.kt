package gaur.himanshu.testingcoroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel() : ViewModel() {
    private val _list = MutableStateFlow(emptyList<String>())
    val list: StateFlow<List<String>> = _list.asStateFlow()

    fun getUser() {
        viewModelScope.launch {
            val users = listOf("a","b")
            _list.value = users
        }
    }
}