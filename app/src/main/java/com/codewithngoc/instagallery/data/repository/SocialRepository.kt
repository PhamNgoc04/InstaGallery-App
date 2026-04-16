package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý các tương tác mạng xã hội: Follow Requests, Block, Mute
 */
class SocialRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    // ── Follow Requests (Private Account) ─────────────────────

    /**
     * Lấy danh sách yêu cầu theo dõi đang chờ duyệt
     */
    suspend fun getFollowRequests(): ApiResponse<List<FollowRequestResponse>> =
        safeApiCall { api.getFollowRequests() }

    /**
     * Chấp nhận hoặc từ chối một yêu cầu theo dõi
     * @param action "accept" hoặc "reject"
     */
    suspend fun handleFollowRequest(followerId: Long, action: String): ApiResponse<Unit> =
        safeApiCall { api.handleFollowRequest(followerId, action) }

    // ── Block / Mute ───────────────────────────────────────────

    /**
     * Chặn (block) hoặc bỏ chặn người dùng — toggle
     */
    suspend fun toggleBlock(userId: Long): ApiResponse<BlockMuteResponse> =
        safeApiCall { api.toggleBlock(userId) }

    /**
     * Tắt tiếng (mute) hoặc bỏ mute người dùng — toggle
     */
    suspend fun toggleMute(userId: Long): ApiResponse<BlockMuteResponse> =
        safeApiCall { api.toggleMute(userId) }
}
