package eu.rechenwerk.ccc.internals.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class Output(val level: Int)
