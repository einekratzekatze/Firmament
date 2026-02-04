package moe.nea.notfimament.events.subscription

import java.util.ServiceLoader
import kotlin.streams.asSequence
import moe.nea.notfimament.Firmament

interface SubscriptionList {
    fun provideSubscriptions(addSubscription: (Subscription<*>) -> Unit)

    companion object {
        val allLists by lazy {
            ServiceLoader.load(SubscriptionList::class.java)
                .stream()
                .asSequence()
                .mapNotNull {
                    kotlin.runCatching { it.get() }
                        .getOrElse { ex ->
                            Firmament.logger.error("Could not load subscriptions from ${it.type()}", ex)
                            null
                        }
                }
                .toList()
        }
        init {
        	require(allLists.isNotEmpty())
        }
    }
}
