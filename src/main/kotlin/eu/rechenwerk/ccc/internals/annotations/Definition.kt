package eu.rechenwerk.ccc.internals.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Definition(val level: Int)
