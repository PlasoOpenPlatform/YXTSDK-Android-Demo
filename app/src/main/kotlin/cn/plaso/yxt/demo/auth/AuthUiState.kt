package cn.plaso.yxt.demo.auth

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Failed(val message: String) : AuthUiState
    data object Success : AuthUiState
}
