package moe.nea.notfimament.api.v1;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Methods you can call to get information about firmaments current state.
 */
@ApiStatus.NonExtendable
public abstract class FirmamentAPI {
	private static @Nullable FirmamentAPI INSTANCE;

	/**
	 * @return the canonical instance of the {@link FirmamentAPI}.
	 */
	public static FirmamentAPI getInstance() {
		if (INSTANCE != null)
			return INSTANCE;
		try {
			return INSTANCE = (FirmamentAPI) Class.forName("moe.nea.notfimament.impl.v1.FirmamentAPIImpl")
				.getField("INSTANCE")
				.get(null);
		} catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
			throw new RuntimeException("Notfimament API implementation class found, but could not load api instance.", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find Notfimament API, check FabricLoader.getInstance().isModLoaded(\"notfimament\") first.");
		}
	}

	/**
	 * @return list-view of registered extensions
	 */
	public abstract List<? extends FirmamentExtension> getExtensions();

	/**
	 * Obtain a reference to the currently hovered item widget, which may be either in the item list or placed in a UI.
	 * This widget may or may not also be present in the Widgets on the current screen.
	 *
	 * @return the currently hovered firmament item widget.
	 */
	public abstract Optional<FirmamentItemWidget> getHoveredItemWidget();
}
