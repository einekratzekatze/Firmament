package moe.nea.notfimament.test.util.skyblock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import net.minecraft.network.chat.Component
import moe.nea.notfimament.test.testutil.ItemResources
import moe.nea.notfimament.util.skyblock.AbilityUtils
import moe.nea.notfimament.util.unformattedString

class AbilityUtilsTest {

	fun List<AbilityUtils.ItemAbility>.stripDescriptions() = map {
		it.copy(descriptionLines = it.descriptionLines.map { Component.literal(it.unformattedString) })
	}

	@Test
	fun testUnpoweredDrill() {
		Assertions.assertEquals(
			listOf(
				AbilityUtils.ItemAbility(
					"Pickobulus",
					false,
					AbilityUtils.AbilityActivation.RIGHT_CLICK,
					null,
					listOf(
						"Throw your pickaxe to create an",
						"explosion mining all ores in a 3 block",
						"radius."
					).map(Component::literal),
					48.seconds
				)
			),
			AbilityUtils.getAbilities(ItemResources.loadItem("titanium-drill")).stripDescriptions()
		)
	}

	@Test
	fun testPoweredPickaxe() {
		Assertions.assertEquals(
			listOf(
				AbilityUtils.ItemAbility(
					"Mining Speed Boost",
					true,
					AbilityUtils.AbilityActivation.RIGHT_CLICK,
					null,
					listOf(
						"Grants +200% ⸕ Mining Speed for",
						"10s."
					).map(Component::literal),
					2.minutes
				)
			),
			AbilityUtils.getAbilities(ItemResources.loadItem("diamond-pickaxe")).stripDescriptions()
		)
	}

	@Test
	fun testAOTV() {
		Assertions.assertEquals(
			listOf(
				AbilityUtils.ItemAbility(
					"Instant Transmission", true, AbilityUtils.AbilityActivation.RIGHT_CLICK, 23,
					listOf(
						"Teleport 12 blocks ahead of you and",
						"gain +50 ✦ Speed for 3 seconds."
					).map(Component::literal),
					null
				),
				AbilityUtils.ItemAbility(
					"Ether Transmission",
					false,
					AbilityUtils.AbilityActivation.SNEAK_RIGHT_CLICK,
					90,
					listOf(
						"Teleport to your targeted block up",
						"to 61 blocks away.",
						"Soulflow Cost: 1"
					).map(Component::literal),
					null
				)
			),
			AbilityUtils.getAbilities(ItemResources.loadItem("aspect-of-the-void")).stripDescriptions()
		)
	}
}
