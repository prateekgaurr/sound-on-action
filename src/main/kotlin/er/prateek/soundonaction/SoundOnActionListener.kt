package er.prateek.soundonaction

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.compiler.CompilationStatusListener
import com.intellij.openapi.compiler.CompileContext

class SoundOnActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val soundManager = ApplicationManager.getApplication().getService(SoundManager::class.java)
        
        // Register file system listener
        val connection = project.messageBus.connect()
        connection.subscribe(com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    when (event) {
                        is VFileCreateEvent -> {
                            soundManager.playSound(SoundManager.SoundType.FILE_CREATE)
                        }
                        is VFileDeleteEvent -> {
                            soundManager.playSound(SoundManager.SoundType.FILE_DELETE)
                        }
                    }
                }
            }
        })
    }
}

class BuildCompilationStatusListener : CompilationStatusListener {
    override fun compilationFinished(aborted: Boolean, errors: Int, warnings: Int, compileContext: CompileContext) {
        val soundManager = ApplicationManager.getApplication().getService(SoundManager::class.java)
        
        if (aborted || errors > 0) {
            soundManager.playSound(SoundManager.SoundType.BUILD_FAILED)
        } else {
            soundManager.playSound(SoundManager.SoundType.BUILD_SUCCESS)
        }
    }
}
