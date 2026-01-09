package com.example.reader.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.realm.FeedbackRealm
import com.example.reader.data.realm.RealmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val realmRepository: RealmRepository
) : ViewModel() {

    private val _feedbackList = MutableStateFlow<List<FeedbackRealm>>(emptyList())
    val feedbackList: StateFlow<List<FeedbackRealm>> = _feedbackList.asStateFlow()

    init {
        loadFeedback()
    }

    private fun loadFeedback() {
        viewModelScope.launch {
            realmRepository.getAllFeedback().collect { feedbacks ->
                _feedbackList.value = feedbacks
            }
        }
    }

    fun saveFeedback(feedbackText: String, sentimentIndex: Int) {
        viewModelScope.launch {
            realmRepository.saveFeedback(feedbackText, sentimentIndex)
        }
    }

    fun deleteFeedback(feedback: FeedbackRealm) {
        viewModelScope.launch {
            realmRepository.deleteFeedback(feedback)
        }
    }
}

