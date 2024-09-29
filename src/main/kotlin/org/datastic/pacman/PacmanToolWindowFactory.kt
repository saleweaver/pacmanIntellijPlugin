package org.datastic.pacman

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.JPanel

class PacmanToolWindowFactory : ToolWindowFactory {
    private val logger = Logger.getInstance(PacmanToolWindowFactory::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        logger.info("PacmanToolWindowFactory created")
        val contentFactory = ContentFactory.getInstance()
        val panel = JPanel(BorderLayout())
        val isJcefSupported = JBCefApp.isSupported()

        if (!isJcefSupported) {
            logger.error("JCEF is not supported on this platform.")
            return
        }
        val browser = JBCefBrowser();
        logger.info("Created JBCefBrowser instance")

        val htmlFile = extractResourceToTempDir("/html/pacman.html")
        val htmlFileUrl = htmlFile.toURI().toString()
        logger.warn("HTML File URL: $htmlFileUrl")

        browser.loadURL(htmlFileUrl)
        logger.info("HTML content loaded into browser")

        // Create a Swing panel to hold the browser component
        panel.add(browser.component, BorderLayout.CENTER);
        logger.info("Added browser component to panel")

        // Add the panel to the tool window
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
        logger.info("Added content to tool window")

    }


    private fun extractResourceToTempDir(resourcePath: String): File {
        val inputStream = javaClass.getResourceAsStream(resourcePath)
            ?: throw IOException("Resource not found: $resourcePath")
        val tempFile = File.createTempFile("temp_", ".html")
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }
}