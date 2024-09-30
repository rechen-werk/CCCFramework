package eu.rechenwerk.ccc.internal

import java.io.File

fun packages(): List<String> {
    val packageNames: MutableSet<String> = HashSet()
    val resources = Thread.currentThread().contextClassLoader.getResources("")
    while (resources.hasMoreElements()) {
        val resource = resources.nextElement()
        val rootDirectory = File(resource.file)
        if (rootDirectory.exists() && rootDirectory.isDirectory) {
            scanDirectory(rootDirectory, "", packageNames)
        }
    }
    return packageNames.filter { it != "META-INF" }.toList()
}

private fun scanDirectory(directory: File, currentPackage: String, packageNames: MutableSet<String>) {
    val files = directory.listFiles() ?: return

    files.forEach { file ->
        if (file.isDirectory) {
            val subPackage = if (currentPackage.isEmpty()) file.name else currentPackage + "." + file.name
            packageNames.add(subPackage)
            scanDirectory(file, subPackage, packageNames)
        }
    }
}