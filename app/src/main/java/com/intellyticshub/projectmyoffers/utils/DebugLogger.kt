package com.intellyticshub.projectmyoffers.utils

import android.os.Environment
import java.io.File

class DebugLogger {
    private val directory = Environment.getExternalStorageDirectory()

    private val debugLogFolderName = "ProjectMyOffers"
    fun writeLog(fileName: String, logData: String) {
        val debugLogFolder = File(directory, debugLogFolderName)
        if (!debugLogFolder.exists()) debugLogFolder.mkdir()
        val logFile = File(debugLogFolder, fileName)
        if (!logFile.exists()) logFile.createNewFile()
        logFile.writeText(logData)
    }
}