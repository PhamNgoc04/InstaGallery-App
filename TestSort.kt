import java.time.LocalDateTime

data class CommentResponse(
    val commentId: Long,
    val parentId: Long?,
    val content: String,
    val createdAt: String
)

fun sortComments(comments: List<CommentResponse>): List<CommentResponse> {
    val repliesMap = comments.filter { it.parentId != null }.groupBy { it.parentId }
    val rootComments = comments.filter { it.parentId == null }.sortedByDescending { it.createdAt }

    val sortedList = mutableListOf<CommentResponse>()

    fun addReplies(comment: CommentResponse) {
        sortedList.add(comment)
        repliesMap[comment.commentId]?.sortedBy { it.createdAt }?.forEach { reply ->
            addReplies(reply)
        }
    }

    rootComments.forEach { root ->
        addReplies(root)
    }

    return sortedList
}

fun main() {
    val comments = listOf(
        CommentResponse(1, null, "day la o dau", "2026-04-04T12:09:25"),
        CommentResponse(2, 1, "hehe", "2026-04-04T12:47:42"),
        CommentResponse(3, 1, "hehe dung r", "2026-04-04T12:58:35"),
        CommentResponse(4, 1, "depj that", "2026-04-04T12:58:50")
    )
    val sorted = sortComments(comments)
    sorted.forEach { println("id: ${it.commentId}, content: ${it.content}") }
}
