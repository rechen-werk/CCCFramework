package eu.rechenwerk.ccc.internal.dtos

data class LevelInfoDto(
    val nrOfLevels: Int,
    val currentLevel: Int,
    val selectedLevel: Int,
    val gameFinished: Boolean,
    val lastLevel: Boolean
)
