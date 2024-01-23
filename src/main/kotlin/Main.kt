fun main() {
    val note1 = NoteService.add("Заметка", "test")
    NoteService.createComment(note1, "Добавляем комментарий в заметку")
    NoteService.createComment(note1, "Test comment 2")
    NoteService.createComment(note1, "New comment")

    NoteService.edit(note1, "Обновленый заголовок", "заменили test на рабочий вариант")

    NoteService.deleteComment(3)

    val note2 = NoteService.add("New task", "debug for program")
    NoteService.createComment(note2, "Какой-то комментарий к заметке ДВА")

    val note3 = NoteService.add("New cats", "Покормить кота")

    val filterNote = NoteService.get(sort = 1)
    println(filterNote)

    println(NoteService.getById(3))
    println(NoteService.getComments(2))
    NoteService.restoreComment(3)
}