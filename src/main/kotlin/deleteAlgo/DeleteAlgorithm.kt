package deleteAlgo

import Configuration
import LocalDBStorage

interface DeleteAlgorithm {
    fun identifier():String
    fun deleteOldVersions(configuration: Configuration,localDBStorage: LocalDBStorage)
}
