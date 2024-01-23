import java.lang.RuntimeException

data class Note(
    val id: Int,
    val ownerId: Int,
    var title: String,
    var text: String,
    var date: Long,
    var comments: Int,
    var readComments: Int = 0,
    var viewUrl: String = "",
    var privacyView: String = "",
    var canComment: Boolean = true,
    var textWiki: String = ""
) : Comparable<Note> {
    override fun compareTo(other: Note): Int {
        return (this.date - other.date).toInt()
    }
}

data class Comment(
    val id: Int,
    val noteId: Int,
    var message: String,
    val date: Long
) : Comparable<Comment> {
    override fun compareTo(other: Comment): Int {
        return (this.date - other.date).toInt()
    }
}

class NoteNotFoundException(message: String) : RuntimeException(message)

object NoteService {
    private val notes = mutableMapOf<Int, Note>()
    private val commentsNote = mutableMapOf<Int, MutableMap<Int, Comment>>()
    private val deleteComments = mutableMapOf<Int, MutableMap<Int, Comment>>()
    private var lastId: Int = 0
    private var lastIdComment: Int = 0

    fun clear() {
        with(notes.iterator()) {
            forEach { remove() }
        }
        with(commentsNote.iterator()) {
            forEach { remove() }
        }
        with(deleteComments.iterator()) {
            forEach { remove() }
        }
        lastId = 0
        lastIdComment = 0
    }

    fun add(
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
        privacyView: String = "",
        privacyComment: String = ""
    ): Int {
        lastId += 1
        val note = Note(lastId, 1, title, text, System.currentTimeMillis(), 0)
        addNoteComment(note.id, note, notes)
        return note.id
    }

    fun createComment(noteId: Int, message: String): Int {
        if (getById(noteId) == null) {
            throw NoteNotFoundException("не найден пост с id= $noteId")
        }
        lastIdComment += 1
        val comment = Comment(lastIdComment, noteId, message, System.currentTimeMillis())
        val saveComment = commentsNote.getOrDefault(noteId, mutableMapOf<Int, Comment>())
        addNoteComment(comment.id, comment, saveComment)
        commentsNote.put(noteId, saveComment)
        return comment.id
    }

    private fun <I, V> addNoteComment(ind: I, value: V, collect: MutableMap<I, V>) {
        collect.put(ind, value)
    }

    fun delete(noteId: Int): Int {
        if (notes.contains(noteId)) {
            notes.remove(noteId)
            if (commentsNote.contains(noteId)) commentsNote.remove(noteId)
            return 1
        }
        return 180
    }

    fun deleteComment(commentId: Int): Int {
        for (comments in commentsNote) {
            for (comment in comments.value) {
                if (comment.value.id == commentId) {
                    val commentsDel = deleteComments.getOrDefault(comments.key, mutableMapOf<Int, Comment>())
                    commentsDel.put(commentId, comment.value)
                    deleteComments.put(comments.key, commentsDel)
                    comments.value.remove(commentId)
                    return 1
                }
            }
        }
        return 180
    }

    fun edit(
        noteId: Int,
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
        privacyView: String = "",
        privacyComment: String = ""
    ): Int {
        if (notes.contains(noteId)) {
            val note = notes.get(noteId)
            if (note != null) {
                note.title = title
                note.text = text
                return 1
            }
        }
        return 180
    }

    fun editComment(commentId: Int, message: String): Int {
        if (message.length < 2) return 180
        for (comments in commentsNote) {
            for (comment in comments.value) {
                if (comment.value.id == commentId) {
                    comment.value.message = message
                    return 1
                }
            }
        }
        return 180
    }

    fun get(noteIds: String = "", count: Int = 0, sort: Int = 0): List<Note> {
        val noteList = mutableListOf<Note>()
        val noteIdList = noteIds.split(" ")
        for (note in notes) {
            if (count != 0 && noteList.size > count) break
            if (noteIds != "") {
                for (filterKey in noteIdList) {
                    try {
                        if (Integer.parseInt(filterKey) == note.key) noteList += note.value
                    } catch (e: RuntimeException) {
                    }
                }

            } else noteList += note.value
        }
        if (sort == 0) noteList.sorted() else noteList.sortDescending()
        return noteList
    }

    fun getById(noteId: Int): Note? {
        return notes?.get(noteId) ?: null
    }

    fun getComments(noteId: Int, sort: Int = 0, count: Int = 0): List<Comment> {
        val commentList = mutableListOf<Comment>()
        val comments = commentsNote?.get(noteId) ?: null
        if (comments != null) {
            for (comment in comments) {
                if (count != 0 && commentList.size > count) break
                commentList += comment.value
            }
        }
        if (sort == 0) commentList.sorted() else commentList.sortDescending()
        return commentList
    }

    fun restoreComment(commentId: Int): Int {
        for (comments in deleteComments) {
            if (comments.value.contains(commentId)) {
                val commentRestore = comments.value[commentId]
                val saveComment = commentsNote.getOrDefault(commentRestore?.noteId, mutableMapOf<Int, Comment>())
                if (commentRestore != null) {
                    addNoteComment(commentId, commentRestore, saveComment)
                    commentsNote.put(commentRestore.noteId, saveComment)
                }
                comments.value.remove(commentId)
                return 1
            }
        }
        return 183
    }
}
