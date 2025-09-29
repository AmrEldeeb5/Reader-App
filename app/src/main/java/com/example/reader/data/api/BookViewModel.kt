package com.example.reader.data.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.model.Book
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val _books = MutableLiveData<List<Book>>(emptyList())
    val books: MutableLiveData<List<Book>> = _books


    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                _books.value = RetrofitInstance.api.getBooks("novels").items
            } catch (e: Exception) {
                // Handle the error appropriately, e.g., log it or show a message to the user
            }
        }
    }
}