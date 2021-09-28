package ru.ivanmurzin.antileaker.utils

import android.util.Log
import java.io.File

class FileManager(private val searchDir: File) {

    fun clearDirectory(dirName: String): Boolean {
        var path: String? = getDirectoryPath(dirName) // получаю путь до нужной папки
            ?: return false // если не получилось найти хотя бы одну такую папку, то возвращаю false

        while (path != null) { // пока существуют папки с заданным именем, удаляю файлы
            wipeFilesRecursively(path) // удаляю файлы директории с затиранием
            path = getDirectoryPath(dirName) // ищю другие папки с тем же именем, чтобы удаить все
        }

        return true
    }

    private fun getDirectoryPath(dirName: String): String? {
        Log.i(MY_FILE_LOGGER, "Directory: ${searchDir.absolutePath}")
        searchDir.walkTopDown().forEach { // рекурентно прохожусь по всем файлам
            if (it.name.lowercase()
                    .contains(dirName.lowercase())
            ) { // ищу папку с ключевым словом
                Log.i(MY_FILE_LOGGER, "Found dir:\n${it.absolutePath}")
                return it.absolutePath // возвращаю результат
            }
        }
        return null // null если ничего не нашлось
    }

    private fun wipeFilesRecursively(fileName: String) {
        val file = File(fileName) // создаю файл
        if (file.isDirectory) { // если это директория
            file.listFiles()?.forEach { // рекурентно спускаюсь по всем файлам
                wipeFilesRecursively(it.absolutePath)
            }
            file.deleteRecursively() // после того, как обработал все внутренние файлы, удаляю директорию
        } else { // если это файл
            /** Расширение .trash необходимо для того, чтобы информация не кэшировалась в иконке **/
            val trashFile = File("${file.absolutePath}.trash") // создаю файл .trash
            file.writeBytes(byteArrayOf(0)) // перезаписываю его нулевым байтом на случай ошибки переименования
            file.renameTo(trashFile) // переименовываю файл
            trashFile.writeBytes(byteArrayOf(0)) // на всякий слючай снова перезаписываю файл нулевым байтом
        }
    }
}