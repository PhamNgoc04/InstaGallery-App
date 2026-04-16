package com.codewithngoc.instagallery.ui.features.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.ConversationResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationResponse>>(emptyList())
    val conversations: StateFlow<List<ConversationResponse>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.getConversationsFlow().collect { list ->
                _conversations.value = list
                if (list.isNotEmpty()) _isLoading.value = false
            }
        }
        loadConversations() // Fetch from remote
    }

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = _conversations.value.isEmpty()
            _error.value = null
            
            when (val res = chatRepository.refreshConversations()) {
                is ApiResponse.Success -> {
                    // Mọi thứ đã lưu vào Room, flow ở init sẽ tự update UI
                }
                is ApiResponse.Error -> _error.value = res.formatMsg()
                is ApiResponse.Exception -> _error.value = res.exception.message
            }
            _isLoading.value = false
        }
    }
}
