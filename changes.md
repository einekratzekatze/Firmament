# Changes Made to Disguise/Disable Firmament

## Renaming: firmament → notfimament

### Core Identity
- `src/main/kotlin/Firmament.kt`: `MOD_ID = "firmament"` → `"notfimament"`
- `src/main/resources/fabric.mod.json`: `"name": "Firmament"` → `"Notfimament"`
- `src/main/resources/fabric.mod.json`: `"description"` updated
- `src/main/resources/fabric.mod.json`: Removed GitHub/Modrinth links
- `src/gametest/resources/fabric.mod.json`: id and name updated to notfimament

### File Renames
- `src/main/resources/firmament.mixins.json` → `notfimament.mixins.json`
- `src/main/resources/firmament.accesswidener` → `notfimament.accesswidener`
- `src/main/resources/assets/firmament/` → `assets/notfimament/`

### Build Configuration
- `build.gradle.kts` line 307: accesswidener path updated
- `gradle.properties`: `archives_base_name=Firmament` → `Notfimament`
- `gradle.properties`: `maven_group=moe.nea.firmament` → `moe.nea.notfimament`
- `settings.gradle.kts`: `rootProject.name = "Firmament"` → `"Notfimament"`
- `build.gradle.kts`: lang asset paths updated to `assets/notfimament/lang`

### Package Rename (moe.nea.firmament → moe.nea.notfimament)
**All source directories renamed:**
- `src/*/java/moe/nea/firmament/` → `moe/nea/notfimament/`
- `javaplugin/src/main/java/moe/nea/firmament/` → `moe/nea/notfimament/`
- `testagent/src/main/java/moe/nea/firmament/` → `moe/nea/notfimament/`

**All package declarations and imports updated in:**
- All `.kt` and `.java` files in `src/`
- All `.kt` and `.java` files in `javaplugin/`, `symbols/`, `testagent/`
- `build.gradle.kts`, `testagent/build.gradle.kts`

### Namespace Identifiers (firmament: → notfimament:)
Changed `Identifier.parse("firmament:...")` to `notfimament:` in:
- `CustomGlobalArmorOverrides.kt`
- `CustomModelOverrideParser.kt`
- `HeadModelChooser.kt`
- `ItemRarityCosmetics.kt`
- `InventoryButton.kt`
- `SlotLocking.kt`
- `StorageOverlayScreen.kt`
- `FirmamentReiPlugin.kt`
- `PickaxeAbility.kt`
- `FirmButtonComponent.kt`
- `MoulConfigUtils.kt`
- `DrawContextExt.kt`
- `RenderCircleProgress.kt`
- `IsSlotProtectedEvent.kt`
- `ExportedTestConstantMeta.kt`
- `LegacyItemExporter.kt`

### Logger/Coroutine Names
- `Firmament.kt`: Logger name `"Firmament"` → `"Notfimament"`
- `Firmament.kt`: Coroutine name `"Firmament"` → `"Notfimament"`

### Data Directory
- `Firmament.kt`: `DATA_DIR = ".firmament"` → `".notfimament"`

### User-Agent / HTTP
- `HttpUtil.kt`: User-agent updated

### Serialization Descriptors
- `GenericInputButton.kt`: `"Firmament:GenericInputButton"` → `"Notfimament:GenericInputButton"`
- `FirmamentRootPredicateSerializer.kt`: `"FirmamentModelRootPredicate"` → `"NotfimamentModelRootPredicate"`

### API/Error Messages
- `FirmamentAPI.java`: Error messages updated to reference "notfimament"
- `FirmamentExtension.java`: Entrypoint name updated
- `SectionBuilderRiser.java`: Error message updated

### Other References
- `RepoModResourcePack.kt`: `getModContainer("firmament")` → `"notfimament"`
- `CustomCapes.kt`: Cape name references

## Deleted Files
- `src/main/kotlin/features/misc/ModAnnouncer.kt` - Sent mod list to servers

## Disabled (Lite Mode)

### Mixins
- `notfimament.mixins.json` → `notfimament.mixins.json.disabled`
- `fabric.mod.json`: mixins array emptied

### Early Initialization
- `EarlyRiser.java`: `run()` method emptied (was calling HandledScreenRiser, SectionBuilderRiser)

### Client Initialization
- `Firmament.kt`: `onClientInitialize()` emptied (was initializing RepoManager, SBData, HypixelStaticData, commands, events, etc.)

### Feature Manager
- `FeatureManager.kt`: `subscribeEvents()` returns immediately

### Tests
- `ItemTypeTest.kt`: `@Disabled("Lite mode")`
- `TimestampTest.kt`: `@Disabled("Lite mode")`

## To Restore

### Re-enable Mixins
1. Rename `notfimament.mixins.json.disabled` back to `notfimament.mixins.json`
2. In `fabric.mod.json`, change `"mixins": []` to `"mixins": ["notfimament.mixins.json"]`

### Re-enable Features
1. Remove `return` from `FeatureManager.subscribeEvents()`
2. Restore `EarlyRiser.run()` body
3. Restore `Firmament.onClientInitialize()` body

### Restore Original Identity
Revert all renaming changes (use git)
