package ru.efremovkirill.tryrunner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel: ViewModel() {

    protected val _errorMessage: MutableSharedFlow<String> = MutableSharedFlow()
    val errorMessage = _errorMessage.asSharedFlow()

    val vms = viewModelScope
    val dio = Dispatchers.IO

}