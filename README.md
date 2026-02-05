# Notfimament

A fork of [Firmament](https://github.com/FirmamentMC/Firmament) that is compatible with [Taunahi](https://github.com/Flavor5/Taunahi).

## Why this fork?

Taunahi detects Firmament by scanning for `moe.nea.firmament` classes and refuses to load alongside it. This fork renames all packages to `moe.nea.notfimament` to bypass that detection.

## Known Issues

These issues from upstream are also present in this fork:
- drawAlignedBox draws incorrect ([#387](https://github.com/FirmamentMC/Firmament/issues/387))
- chat copying is broken ([#388](https://github.com/FirmamentMC/Firmament/issues/388))
- check the 3d line rendering functions ([#389](https://github.com/FirmamentMC/Firmament/issues/389))
- check slice rendering ([#390](https://github.com/FirmamentMC/Firmament/issues/390))
- rework cape rendering ([#391](https://github.com/FirmamentMC/Firmament/issues/391))

See [upstream milestone](https://github.com/FirmamentMC/Firmament/milestone/2) for status.

## Building

Requires Java 21.

```bash
# Clone
git clone https://github.com/einekratzekatze/NotFimament.git
cd NotFimament

# Build
./gradlew build

# Output jar is in build/libs/
```

## Installation

Same as Firmament. Requires:
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)

For item list features:
- [RoughlyEnoughItems](https://modrinth.com/mod/rei)
- [Architectury](https://modrinth.com/mod/architectury-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config)

## Merging Upstream Updates

If this fork is outdated and you want to pull in new changes from Firmament yourself, see [scripts/README.md](scripts/README.md) for instructions.

The transformation script handles the package rename automatically:
```bash
./scripts/transform-upstream.sh
```

## License

GPL-3.0-or-later (same as upstream Firmament)
