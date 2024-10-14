package eu.rechenwerk

import eu.rechenwerk.ccc.external.*
import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many

@Validated
@Level(4) fun level4_2(
    n: Int,
    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
): String {
    return lawns.map {
        lawn -> lawn.mow().path
    }.joinToString("\n")
}


@Validator(4) fun validator4_2(
    n: Int,
    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
): Boolean {
    return lawns
        .map { lawn -> lawn.mow() }
        .all { lawn -> lawn.validate() }
}