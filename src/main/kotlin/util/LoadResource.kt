
package moe.nea.notfimament.util

import java.io.InputStream
import kotlin.io.path.inputStream
import kotlin.jvm.optionals.getOrNull
import net.minecraft.resources.Identifier
import moe.nea.notfimament.repo.RepoDownloadManager


fun Identifier.openFirmamentResource(): InputStream {
    val resource = MC.resourceManager.getResource(this).getOrNull()
    if (resource == null) {
        if (namespace == "neurepo")
            return RepoDownloadManager.repoSavedLocation.resolve(path).inputStream()
        error("Could not read resource $this")
    }
    return resource.open()
}

