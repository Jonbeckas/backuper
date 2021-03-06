import backends.Backend
import backends.SmbBackend
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipFile
import kotlin.system.exitProcess
import ch.swaechter.smbjwrapper.SmbDirectory




fun main() {
    Main(LocalDB())
}

class Main(val localDB: LocalDB) {
    val dir:File = File(System.getProperty("user.home")+"\\AppData\\Roaming\\backuper\\");
    lateinit var config: Configuration;
    val start = System.currentTimeMillis();
    lateinit var localDBStorage: LocalDBStorage;
    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val configFile = File(dir.absolutePath+"\\config.json")
        if (!configFile.exists()) {
            configFile.writeText(Gson().toJson(Configuration()))
            println("Create config File at ${configFile.absolutePath}!")
            exitProcess(2)
        } else {
            this.config = Gson().fromJson(configFile.readText(),Configuration::class.java)
            if (testForValidConfiguration()) {
                continueMainCycle()
            } else {
                println("Configuration file is not valid!")
                exitProcess(2);
            }
        }

    }

    private fun testForValidConfiguration():Boolean {
        return getBackendByIdentifier(this.config.backend).checkValidConfig(config)
    }

    private fun listAllFilesRecursive(dir:String):List<String> {
        val list = mutableListOf<String>()
        File(dir).list().forEach {
            if (File("$dir/$it").isDirectory) {
                list.addAll(listAllFilesRecursive("$dir/$it"))
            } else {
                list.add("$dir/$it")
            }
        }
        return list;
    }

    private fun continueMainCycle() {
        val backend:Backend = getBackendByIdentifier(this.config.backend)
        println("Connect to backend")
        if (!backend.connect(this.config)) {
            println("Could not connect to Endpoint!")
            exitProcess(3)
        }
        /*backend.listDirs(" ").forEach {
            println(it)
        }*/
        val dbStream = backend.readFile("db.json","")
        if (dbStream==null) {
            println("Created new db file")
            this.localDBStorage = LocalDBStorage(mutableListOf())
        } else {
            println("Loaded DB file from endpoint")
            this.localDBStorage = Gson().fromJson(dbStream.toString(),LocalDBStorage::class.java)
        }
        listAllFilesRecursive(this.config.localPath).forEachIndexed { i: Int, s: String ->
            val f = File(s)
            val sf = StorageFile.findFileByVersion(this.localDBStorage,s)
            if (sf!=null) {
                sf.versions.sortedBy { version -> version.version.toLong() }
                val lastMod = f.lastModified()
                if (sf.versions.last().timestamp < lastMod) {
                    backend.uploadFile(f.inputStream(),f.absolutePath,start.toString())
                    sf.versions.add(Version(lastMod,start.toString()))
                }
            } else {
                backend.uploadFile(f.inputStream(),f.absolutePath,start.toString())
                this.localDBStorage.paths.add(StorageFile(s, mutableListOf(Version(f.lastModified(),this.start.toString()))))
            }

        }
        backend.uploadFile(Gson().toJson(this.localDBStorage).byteInputStream(),"db.json","")
    }

    private fun getBackendByIdentifier(string:String):Backend {
        return when(string) {
            "smb" -> SmbBackend()
            else -> {
                println("No valid Backend in config file!")
                exitProcess(2);
            }
        }
    }


}
