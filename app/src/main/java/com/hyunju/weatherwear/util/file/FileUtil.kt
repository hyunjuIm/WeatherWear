package com.hyunju.weatherwear.util.file

import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
import com.hyunju.weatherwear.model.FileModel
import java.io.File

const val BACK_UP_DB_NAME = "ApplicationDataBase.db"
const val BACK_UP_DB_SHM_NAME = "ApplicationDataBase.db-shm"
const val BACK_UP_DB_WAL_NAME = "ApplicationDataBase.db-wal"

// 백업 파일 생성
fun createBackupFileList(): List<FileModel>? {
    val fileModelList = mutableListOf<FileModel>()

    try {
        fileModelList.add(toDatabaseFileModel(BACK_UP_DB_NAME))
        fileModelList.add(toDatabaseFileModel(BACK_UP_DB_SHM_NAME))
        fileModelList.add(toDatabaseFileModel(BACK_UP_DB_WAL_NAME))
    } catch (e: Exception) {
        return null
    }

    return fileModelList
}

private fun toDatabaseFileModel(name: String) = FileModel(
    name = name,
    file = appContext?.getDatabasePath(name) ?: throw Exception()
)

fun readDatabaseFile(name: String): File {
    val filePath = appContext?.getDatabasePath(name)?.path ?: throw Exception()
    return File(filePath)
}