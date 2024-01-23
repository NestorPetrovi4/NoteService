import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class NoteServiceTest {
    @Before
    fun clearBeforeTest() {
        NoteService.clear()
    }

    @Test
    fun add() {
        assertEquals(NoteService.add("Заметка1", "тест добавления заметки"), 1)
    }

    @Test(expected = NoteNotFoundException::class)
    fun addCommentForNotNote() {
        NoteService.createComment(12, "Нет такой заметки")
    }

    @Test
    fun createCommentRealNote(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.createComment(noteId, "комментарий 1")
        NoteService.createComment(noteId, "комментарий 2")
        NoteService.createComment(noteId, "комментарий 3")
        assertEquals(NoteService.createComment(noteId, "комментарий 4"), 4)
    }

    @Test
    fun deleteRealNote(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        assertEquals(NoteService.delete(noteId), 1)
    }

    @Test
    fun deleteFalseNote(){
        NoteService.add("Заметка1", "тест добавления заметки")
        assertEquals(NoteService.delete(4) != 1, true)
    }

    @Test
    fun deleteRealComment(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.createComment(noteId, "комментарий 1")
        NoteService.createComment(noteId, "комментарий 2")
        NoteService.createComment(noteId, "комментарий 3")
        assertEquals(NoteService.deleteComment(3), 1)
    }

    @Test
    fun deleteFalseComment(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.createComment(noteId, "комментарий 1")
        assertEquals(NoteService.deleteComment(3) != 1, true)
    }

    @Test
    fun getAllRealNotes(){
        NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.add("Заметка2", "тест добавления заметки")
        NoteService.add("Заметка3", "тест добавления заметки")
        NoteService.add("Заметка4", "тест добавления заметки")
        NoteService.add("Заметка5", "тест добавления заметки")
        val lisNote = NoteService.get()
        assertEquals(lisNote.size, 5)
    }

    @Test
    fun restoreRealComment(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.createComment(noteId, "комментарий 1")
        NoteService.createComment(noteId, "комментарий 2")
        val commentId = NoteService.createComment(noteId, "комментарий 3")
        NoteService.deleteComment(commentId)
        assertEquals(NoteService.restoreComment(commentId), 1)
    }

    @Test
    fun restoreFalseComment(){
        val noteId = NoteService.add("Заметка1", "тест добавления заметки")
        NoteService.createComment(noteId, "комментарий 1")
        NoteService.createComment(noteId, "комментарий 2")
        NoteService.createComment(noteId, "комментарий 3")
        assertEquals(NoteService.restoreComment(5) != 1, true)
    }

}