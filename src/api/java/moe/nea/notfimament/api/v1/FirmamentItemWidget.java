package moe.nea.notfimament.api.v1;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface FirmamentItemWidget {
	/**
	 * non-exhaustive enum for the placement position of an item widget
	 */
	@ApiStatus.NonExtendable
	interface Placement {
		String name();

		Placement ITEM_LIST = () -> "ITEM_LIST";
		Placement RECIPE_SCREEN = () -> "RECIPE_SCREEN";
	}

	/**
	 * @return where in the UI the item widget is placed
	 */
	Placement getPlacement();

	/**
	 * get the currently displayed {@link ItemStack}. this empty stack might be empty. care should be taken not to
	 * mutate the item stack instance, without {@link ItemStack#copy copying} it first.
	 *
	 * @return the currently displayed {@link ItemStack}, may be empty.
	 */
	ItemStack getItemStack();

	/**
	 * @return a SkyBlock id, potentially processed to reflect more details about the item stack, such as which specific
	 * {@code PET} it is. this is sometimes referred to as the "neu id".
	 */
	String getSkyBlockId();
}
