package backends

import Configuration
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface Backend {
    fun checkValidConfig(config:Configuration):Boolean
    fun getBackendIdentifier():String
    fun connect(config:Configuration):Boolean
    fun uploadFile(file: InputStream,path:String,prefix:String):Boolean
    fun readFile(path:String,prefix: String):OutputStream?
    fun createDir(path:String):Boolean
    fun listDirs(path:String):List<String>
    fun rm(path:String,prefix:String):Boolean
}
