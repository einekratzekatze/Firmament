package moe.nea.notfimament.features

import moe.nea.notfimament.events.FirmamentEvent
import moe.nea.notfimament.events.subscription.Subscription
import moe.nea.notfimament.events.subscription.SubscriptionList
import moe.nea.notfimament.util.ErrorUtil
import moe.nea.notfimament.util.compatloader.ICompatMeta

object FeatureManager {

	fun subscribeEvents() {
		SubscriptionList.allLists.forEach { list ->
			if (ICompatMeta.shouldLoad(list.javaClass.name))
				ErrorUtil.catch("Error while loading events from $list") {
					list.provideSubscriptions {
						subscribeSingleEvent(it)
					}
				}
		}
	}

	private fun <T : FirmamentEvent> subscribeSingleEvent(it: Subscription<T>) {
		it.eventBus.subscribe(false, "${it.owner.javaClass.simpleName}:${it.methodName}", it.invoke)
	}
}
