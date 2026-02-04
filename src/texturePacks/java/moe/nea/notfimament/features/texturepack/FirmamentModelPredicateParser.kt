
package moe.nea.notfimament.features.texturepack

import com.google.gson.JsonElement

interface FirmamentModelPredicateParser {
    fun parse(jsonElement: JsonElement): FirmamentModelPredicate?
}
