package moe.nea.notfimament.util.mc

import java.util.Spliterator
import java.util.Spliterators
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

val Container.indices get() = 0 until containerSize
val Container.iterableView
	get() = object : Iterable<ItemStack> {
		override fun spliterator(): Spliterator<ItemStack> {
			return Spliterators.spliterator(iterator(), containerSize.toLong(), 0)
		}

		override fun iterator(): Iterator<ItemStack> {
			return object : Iterator<ItemStack> {
				var i = 0
				override fun hasNext(): Boolean {
					return i < containerSize
				}

				override fun next(): ItemStack {
					if (!hasNext()) throw NoSuchElementException()
					return getItem(i++)
				}
			}
		}
	}
