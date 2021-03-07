package backends

import Configuration
import ch.swaechter.smbjwrapper.SmbFile
import org.apache.commons.io.IOUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class LocalBackend :Backend{

    lateinit var remoteFolder:String
    override fun checkValidConfig(config: Configuration): Boolean {
        val f = File(config.remotePath)
        if (f.exists()) {
            f.mkdirs()
        }
        this.remoteFolder = config.remotePath
        return config.remotePath!="" && f.isDirectory && File(config.localPath).isDirectory
    }

    override fun getBackendIdentifier(): String {
        return "local"
    }

    override fun connect(config: Configuration): Boolean {
        remoteFolder = config.remotePath;
        return true
    }

    override fun uploadFile(file: InputStream, path: String, prefix: String): Boolean {
        val rf = File("${this.remoteFolder}/$prefix/${path.replace(":","")}")
        rf.mkdirs()
        Files.copy(file,rf.toPath(),StandardCopyOption.REPLACE_EXISTING)
        return true
    }

    override fun readFile(path: String, prefix: String): OutputStream? {
        val f = File("${this.remoteFolder}/$prefix/${path.replace(":","/")}");
        return if (f.exists()) {
            val outputStream = ByteArrayOutputStream()
            val input = f.inputStream();
            IOUtils.copy(input, outputStream)
            input.close()
            outputStream
        } else {
            null
        }
    }

    override fun createDir(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun listDirs(path: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun rm(path: String,prefix: String): Boolean {
        val f =  File("${this.remoteFolder}/$prefix/${path.replace(":","/")}")
        val b = f.delete()
        var parent = f.parentFile
        while(parent.isDirectory && parent.exists() && parent.listFiles().size==0) {
            parent.delete()
            parent = parent.parentFile
        }
        return b
    }
}
