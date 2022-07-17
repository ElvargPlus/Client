package com.runescape.cache.assets

import mu.KotlinLogging
import net.runelite.client.ui.ClientUI
import java.awt.BorderLayout
import java.awt.Font
import java.io.File
import java.util.*
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel

private val logger = KotlinLogging.logger {}

object AssetLoader {

    var clientProperties = Properties()
    val propPath = File("./client.properties")

    fun initCache() {
        loadProperties()
        if(openChooser()) {
            openFileSelection()
        }
    }

    private fun loadProperties() {
        if(!propPath.exists()) {
            logger.info { "Client properties not found making file." }
            propPath.createNewFile()
        }

        clientProperties.load(propPath.inputStream())
    }

    fun save() {
        clientProperties.store(propPath.outputStream(), "Properties")
        logger.info { "Client properties saved." }
    }

    private fun openFileSelection() {

        val frame = JFrame()
        frame.iconImage = ClientUI.ICON

        val fileChooser = JFileChooser(".")
        fileChooser.dialogTitle = "ElvargPlus Cache Chooser"
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.showOpenDialog(null)
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val loc = "${fileChooser.selectedFile}" + File.separator
            clientProperties.setProperty("cache", loc)
            save()
        }

    }

    fun getCache() = File(clientProperties.getProperty("cache") + "/Client/cache/")

    fun getClientFolder() = File(clientProperties.getProperty("cache") + "/Client/")

    private fun openChooser() : Boolean {
        if(clientProperties.getProperty("cache") == null) {
            return true
        }
        if(!File(clientProperties.getProperty("cache")).exists()) {
            return true
        }
        return false
    }


}