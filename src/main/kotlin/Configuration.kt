data class Configuration(
    val backend: String = "smb",
    val username:String = "",
    val password:String = "",
    val server:String = "",
    val localPath:String = "",
    val remotePath:String = "",
    val deleteAlgo:String ="",
    val keepVersions:Int = 5
    )
