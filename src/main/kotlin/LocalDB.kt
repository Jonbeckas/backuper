class LocalDB {
    init {

    }
}

data class LocalDBStorage (val paths:MutableList<StorageFile>)
data class StorageFile(val name:String, val versions:MutableList<Version>){
    companion object {
        fun  findFileByVersion(localDBStorage: LocalDBStorage, name:String):StorageFile?  {
            val element =  localDBStorage.paths.filter { el -> el.name == name }
            return  if (element.isNotEmpty()) {element[0]} else null
        }
    }
}
data class Version(val timestamp:Long,val version:String)
