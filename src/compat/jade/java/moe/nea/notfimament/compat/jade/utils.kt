package moe.nea.notfimament.compat.jade

import moe.nea.notfimament.util.SBData

fun isOnMiningIsland(): Boolean =
	SBData.skyblockLocation?.hasCustomMining ?: false
