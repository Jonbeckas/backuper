package deleteAlgo

import Configuration
import LocalDBStorage
import backends.Backend

interface DeleteAlgorithm {
    fun identifier():String
    fun deleteOldVersions(configuration: Configuration,localDBStorage: LocalDBStorage,backend: Backend):LocalDBStorage
}
