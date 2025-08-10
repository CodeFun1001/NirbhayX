package com.img.nirbhayx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.SafetyCategory
import com.img.nirbhayx.data.SafetyTip
import com.img.nirbhayx.data.SafetyTipsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SafetyTipsViewModel(
    private val repository: SafetyTipsRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<SafetyCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showBookmarksOnly = MutableStateFlow(false)
    val showBookmarksOnly = _showBookmarksOnly.asStateFlow()

    val allTips = repository.getAllTips()
    val bookmarkedTips = repository.getBookmarkedTips()

    val filteredTips = combine(
        allTips,
        bookmarkedTips,
        selectedCategory,
        showBookmarksOnly
    ) { all, bookmarked, category, bookmarksOnly ->
        var tips = if (bookmarksOnly) bookmarked else all

        category?.let { cat ->
            tips = tips.filter { it.category == cat }
        }

        tips.sortedBy { it.priority }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: SafetyCategory?) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleBookmarksFilter() {
        _showBookmarksOnly.value = !_showBookmarksOnly.value
    }

    fun toggleBookmark(tip: SafetyTip) {
        viewModelScope.launch {
            repository.isBookmarked(tip.id).first().let { isBookmarked ->
                repository.toggleBookmark(tip.id, isBookmarked)
            }
        }
    }

    fun isBookmarked(tipId: String): Flow<Boolean> {
        return repository.isBookmarked(tipId)
    }

    init {
        viewModelScope.launch {
            repository.initializeTips()
        }
    }
}
