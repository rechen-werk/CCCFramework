package eu.rechenwerk.ccc.internals.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation  class Repeat(val field: String)
