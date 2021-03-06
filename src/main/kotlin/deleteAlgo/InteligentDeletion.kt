package deleteAlgo

import Configuration
import LocalDBStorage
import StorageFile

class InteligentDeletion:DeleteAlgorithm {
    override fun identifier(): String {
        return "INTELLIGENT"
    }

    override fun deleteOldVersions(configuration: Configuration,localDBStorage: LocalDBStorage) {
        localDBStorage.paths.forEach { storageFile: StorageFile ->
            if (storageFile.versions.size > configuration.keepVersions) {

            }
        }
    }
}
