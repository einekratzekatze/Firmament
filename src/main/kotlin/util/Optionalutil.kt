package moe.nea.notfimament.util

import java.util.Optional

fun <T : Any> T?.intoOptional(): Optional<T> = Optional.ofNullable(this)
