package backends

import Configuration
import ch.swaechter.smbjwrapper.SmbConnection
import ch.swaechter.smbjwrapper.SmbFile
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2CreateOptions
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.auth.AuthenticationContext
import org.apache.commons.io.IOUtils
import ch.swaechter.smbjwrapper.SmbDirectory
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.*


class SmbBackend:Backend {
    private lateinit var connection:SmbConnection
    lateinit var prefix: String
    override fun checkValidConfig(config: Configuration): Boolean {
        return config.password!=""&& config.username!=""&& config.remotePath!=""&&config.server!=""&& File(config.localPath).isDirectory
    }

    override fun getBackendIdentifier(): String {
        return "smb"
    }

    override fun connect(config: Configuration): Boolean {
        return try {
            val authenticationContext = AuthenticationContext(config.username,config.password.toCharArray(),"")
            val subString = config.remotePath.subSequence(0,config.remotePath.indexOf("\\")) as String? ?: "backuper"
            this.prefix = config.remotePath.subSequence(subString.length+1,config.remotePath.length) as String
            this.connection= SmbConnection(config.server, subString, authenticationContext)
            true
        } catch (e:Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun uploadFile(file: InputStream,path:String,preprefix: String): Boolean {
        try {
            if (preprefix!="") SmbDirectory(this.connection,"${this.prefix}/$preprefix").ensureExists()
            if (File(path).parent != null) createDirRecursive(File(path).parent,preprefix)
            val smbFile = SmbFile(connection,"${this.prefix}/$preprefix/${path.replace(":","")}")
            val outputStream = smbFile.outputStream
            IOUtils.copy(file,outputStream)
            file.close()
            outputStream.close()
            return true
        }catch (e:Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun createDirRecursive(path:String,preprefix: String) {
        try {
            val preDir = SmbDirectory(this.connection, this.prefix)
            preDir.ensureExists()
            val file = File(path);
            if (file.isDirectory && file.parent != null) {
                createDirRecursive(file.parent,preprefix);
            }
            val s = "${this.prefix}${File.separator}${file.path.replace(":","")}"
            val smbDirectory = SmbDirectory(this.connection, "${this.prefix}${File.separator}$preprefix${File.separator}${file.path.replace(":","")}")
            smbDirectory.ensureExists()
        } catch (e:Exception) {
            e.printStackTrace()
        }

    }

    override fun readFile(path: String,preprefix:String): OutputStream? {
        try {
            val smbFile = SmbFile(connection,"${this.prefix}/$preprefix/$path")
            val inputStream = smbFile.inputStream
            val outputStream = ByteArrayOutputStream()
            IOUtils.copy(inputStream, outputStream)
            inputStream.close()
            return outputStream
        }catch (e:Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun createDir(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun listDirs(path: String):List<String> {
        val dir = SmbDirectory(this.connection,path).directories
        val list = mutableListOf<String>()
        dir.forEach {
            list.add(it.path)
        }
        return list
    }

    override fun rm(path: String,preprefix: String): Boolean {
        TODO("Not yet implemented")
    }
}
