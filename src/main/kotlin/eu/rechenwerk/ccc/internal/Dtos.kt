package eu.rechenwerk.ccc.internal

data class LevelInfoDto(
    val nrOfLevels: Int,
    val currentLevel: Int,
    val selectedLevel: Int,
    val gameFinished: Boolean,
    val lastLevel: Boolean
)

data class ResourceLocationDto(
    val url: String,
    val requestMethod: String
)
