package eu.rechenwerk

import eu.rechenwerk.ccc.external.*

@Validated
//@Example(3)
@Level(5) fun level5(
    n: Int,
    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
): String {
    return lawns.map {
            lawn -> lawn.mowAdvanced().path
    }.joinToString("\n")
}

@Validator(5) fun validator5(
    n: Int,
    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
): Boolean {
    return lawns
        .map { lawn -> lawn.mowAdvanced() }
        .all { lawn -> lawn.validate() }
}



