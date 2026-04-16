package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý tìm kiếm và khám phá nội dung
 */
class SearchRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    // ── Explore ────────────────────────────────────────────────

    suspend fun getExplore(page: Int = 1, limit: Int = 20): ApiResponse<ExploreResponse> =
        safeApiCall { api.getExplore(page, limit) }

    suspend fun getTrendingTags(): ApiResponse<List<TagResult>> =
        safeApiCall { api.getTrendingTags() }

    // ── Search ─────────────────────────────────────────────────

    /**
     * Tìm kiếm global — users, posts, tags
     * @param type null = all, "users" | "posts" | "tags"
     */
    suspend fun search(query: String, type: String? = null): ApiResponse<SearchResponse> =
        safeApiCall { api.search(query, type) }

    suspend fun getSearchHistory(): ApiResponse<List<SearchHistoryItem>> =
        safeApiCall { api.getSearchHistory() }

    suspend fun clearSearchHistory(): ApiResponse<Unit> =
        safeApiCall { api.clearSearchHistory() }
}
