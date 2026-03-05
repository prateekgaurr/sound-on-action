package er.prateek.soundonaction

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

@Service
class SoundManager {
    private val logger = Logger.getInstance(SoundManager::class.java)
    
    // Sound arrays for each event type
    private val fileCreateSounds = arrayOf(
        "/sounds/create_1.wav",
        "/sounds/create_2.wav"
    )
    
    private val fileDeleteSounds = arrayOf(
        "/sounds/delete_1.wav",
        "/sounds/delete_2.wav",
        "/sounds/delete_3.wav",
        "/sounds/delete_4.wav",
        "/sounds/delete_5.wav",
        "/sounds/delete_6.wav",
        "/sounds/delete_7.wav",
        "/sounds/delete_8.wav",
        "/sounds/delete_9.wav"
    )
    
    private val buildSuccessSounds = arrayOf(
        "/sounds/success_1.wav",
        "/sounds/success_2.wav",
        "/sounds/success_3.wav",
        "/sounds/success_4.wav",
        "/sounds/success_5.wav"
    )
    
    private val buildFailedSounds = arrayOf(
        "/sounds/failed_1.wav",
        "/sounds/failed_2.wav",
        "/sounds/failed_3.wav",
        "/sounds/failed_4.wav",
        "/sounds/failed_5.wav",
        "/sounds/failed_6.wav",
        "/sounds/failed_7.wav",
        "/sounds/failed_8.wav",
        "/sounds/failed_9.wav"
    )
    
    // Current indices for rotation
    private var fileCreateIndex = 0
    private var fileDeleteIndex = 0
    private var buildSuccessIndex = 0
    private var buildFailedIndex = 0
    
    enum class SoundType {
        FILE_CREATE,
        FILE_DELETE,
        BUILD_SUCCESS,
        BUILD_FAILED
    }
    
    fun playSound(soundType: SoundType) {
        val (soundArray, currentIndex) = when (soundType) {
            SoundType.FILE_CREATE -> Pair(fileCreateSounds, fileCreateIndex)
            SoundType.FILE_DELETE -> Pair(fileDeleteSounds, fileDeleteIndex)
            SoundType.BUILD_SUCCESS -> Pair(buildSuccessSounds, buildSuccessIndex)
            SoundType.BUILD_FAILED -> Pair(buildFailedSounds, buildFailedIndex)
        }
        
        val soundPath = soundArray[currentIndex]
        
        // Update index for next time (rotate through array)
        when (soundType) {
            SoundType.FILE_CREATE -> fileCreateIndex = (fileCreateIndex + 1) % fileCreateSounds.size
            SoundType.FILE_DELETE -> fileDeleteIndex = (fileDeleteIndex + 1) % fileDeleteSounds.size
            SoundType.BUILD_SUCCESS -> buildSuccessIndex = (buildSuccessIndex + 1) % buildSuccessSounds.size
            SoundType.BUILD_FAILED -> buildFailedIndex = (buildFailedIndex + 1) % buildFailedSounds.size
        }
        
        // Play sound in a separate thread to avoid blocking
        Thread {
            try {
                val audioInputStream = javaClass.getResourceAsStream(soundPath)
                if (audioInputStream == null) {
                    logger.warn("Sound file not found: $soundPath")
                    return@Thread
                }
                
                val bufferedInputStream = BufferedInputStream(audioInputStream)
                val audioStream = AudioSystem.getAudioInputStream(bufferedInputStream)
                val clip = AudioSystem.getClip()
                
                clip.addLineListener { event ->
                    if (event.type == LineEvent.Type.STOP) {
                        clip.close()
                    }
                }
                
                clip.open(audioStream)
                clip.start()
                
            } catch (e: Exception) {
                logger.error("Error playing sound: $soundPath", e)
            }
        }.start()
    }
}
