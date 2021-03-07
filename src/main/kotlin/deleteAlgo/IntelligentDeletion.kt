package deleteAlgo

import Configuration
import LocalDBStorage
import StorageFile
import Version
import backends.Backend

class IntelligentDeletion:DeleteAlgorithm {
    override fun identifier(): String {
        return "INTELLIGENT"
    }

    override fun deleteOldVersions(configuration: Configuration,localDBStorage: LocalDBStorage,backend: Backend):LocalDBStorage {
        localDBStorage.paths.forEach { storageFile: StorageFile ->
            if (storageFile.versions.size > configuration.keepVersions) {
                println("--------")
                println(storageFile.name)
                val week: MutableList<Version> = mutableListOf()
                val month: MutableList<Version> = mutableListOf()
                val year: MutableList<Version> = mutableListOf()
                val other: MutableList<Version> = mutableListOf()
                val ms = System.currentTimeMillis()
                storageFile.versions.forEach { version ->
                     when {
                        version.timestamp>= ms+(604806L*10000L) -> {
                            week.add(version)
                        }
                        version.timestamp>= ms+(2419200L*10000L) -> {
                            month.add(version)
                        }
                        version.timestamp>= ms+(29030499L*10000L) -> {
                            year.add(version)
                        }
                        else -> {
                            other.add(version)
                        }
                    }
                }
                other.sortByDescending { version -> version.timestamp  }
                other.forEachIndexed { index, version ->
                    if (index>configuration.keepVersions) {
                        if (!(week.size ==0 && other.lastIndex ==index)) {
                            backend.rm(storageFile.name,version.version)
                        }
                    }
                }
                week.sortByDescending { version -> version.timestamp  }
                week.forEachIndexed { index, version ->
                    if (index>configuration.keepVersions) {
                        if (!(month.size ==0 && week.lastIndex ==index)) {
                            backend.rm(storageFile.name,version.version)
                        }
                    }
                }
                month.sortByDescending { version -> version.timestamp  }
                month.forEachIndexed { index, version ->
                    if (index>configuration.keepVersions) {
                        if (!(year.size ==0 && month.lastIndex ==index)) {
                            backend.rm(storageFile.name,version.version)
                        }
                    }
                }
            }
        }
    }
}
