package com.untamedears.itemexchange.rules;

import com.untamedears.itemexchange.ItemExchangePlugin;
import com.untamedears.itemexchange.rules.interfaces.ModifierData;
import com.untamedears.itemexchange.utility.ModifierStorage;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Stream;
import vg.civcraft.mc.civmodcore.nbt.NBTSerialization;

public final class ModifierRegistrar {

	private final ModifierStorage modifiers = new ModifierStorage();

	/**
	 * Registers a modifier.
	 *
	 * @param modifier The modifier to register.
	 */
	public void registerModifier(ModifierData modifier) {
		if (modifier == null) {
			throw new IllegalArgumentException("Cannot register a null modifier.");
		}
		if (this.modifiers.get(modifier) != null) {
			throw new IllegalArgumentException("That modifier is already registered.");
		}
		if (!Modifier.isFinal(modifier.getClass().getModifiers())) {
			throw new IllegalArgumentException("That modifier is not final.");
		}
		NBTSerialization.registerNBTSerializable(modifier.getClass());
		this.modifiers.put(modifier);
		ItemExchangePlugin.commandManager().registerCommand(modifier);
	}

	/**
	 * Deregisters a modifier from use.
	 *
	 * @param modifier The modifier to deregister.
	 */
	public void deregisterModifier(ModifierData modifier) {
		if (modifier == null) {
			return;
		}
		NBTSerialization.unregisterNBTSerializable(modifier.getClass());
		this.modifiers.remove(modifier);
		ItemExchangePlugin.commandManager().deregisterCommand(modifier);
	}

	/**
	 * Determines whether a modifier has been registered.
	 *
	 * @param clazz The class of the modifier to check.
	 * @return Returns a template modifier if the class is registered.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ModifierData> T getModifier(Class<T> clazz) {
		if (clazz == null) {
			return null;
		}
		return (T) this.modifiers.stream()
				.filter(template -> clazz == template.getClass())
				.findFirst().orElse(null);
	}

	/**
	 * Retrieves all registered modifiers in order.
	 *
	 * @return Returns a stream of all registered modifiers in order.
	 */
	public Stream<ModifierData> getModifiers() {
		return this.modifiers.stream().filter(Objects::nonNull).sorted();
	}

	/**
	 * De-registers all registered modifiers.
	 */
	public void reset() {
		for (ModifierData modifier : this.modifiers) {
			NBTSerialization.unregisterNBTSerializable(modifier.getClass());
			ItemExchangePlugin.commandManager().deregisterCommand(modifier);
		}
		this.modifiers.clear();
	}

}
