package eu.rechenwerk.ccc.internal.model

internal data class LevelInput(
    val level: Int,
    val examples: List<ExampleInput>,
    val solutions: List<ExampleSolution>
) {
}