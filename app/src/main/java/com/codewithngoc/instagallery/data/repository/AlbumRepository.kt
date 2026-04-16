package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý Albums (thư mục ảnh cá nhân)
 */
class AlbumRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    suspend fun getAlbums(): ApiResponse<List<AlbumResponse>> =
        safeApiCall { api.getAlbums() }

    suspend fun createAlbum(request: CreateAlbumRequest): ApiResponse<AlbumResponse> =
        safeApiCall { api.createAlbum(request) }

    suspend fun getAlbumDetail(albumId: Long): ApiResponse<AlbumDetailResponse> =
        safeApiCall { api.getAlbumDetail(albumId) }

    suspend fun updateAlbum(albumId: Long, request: UpdateAlbumRequest): ApiResponse<AlbumResponse> =
        safeApiCall { api.updateAlbum(albumId, request) }

    suspend fun deleteAlbum(albumId: Long): ApiResponse<Unit> =
        safeApiCall { api.deleteAlbum(albumId) }

    /**
     * Thêm bài đăng (postId) vào một album
     */
    suspend fun addPostToAlbum(albumId: Long, postId: Long): ApiResponse<Unit> =
        safeApiCall { api.addPostToAlbum(albumId, AddPostToAlbumRequest(postId)) }

    /**
     * Gỡ bài đăng khỏi album — KHÔNG xóa bài đăng gốc
     */
    suspend fun removePostFromAlbum(albumId: Long, mediaId: Long): ApiResponse<Unit> =
        safeApiCall { api.removePostFromAlbum(albumId, mediaId) }
}
