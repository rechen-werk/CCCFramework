package eu.rechenwerk.ccc.internals.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Level(val value: Int)
