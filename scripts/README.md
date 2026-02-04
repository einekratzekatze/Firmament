# Notfimament Upstream Merge Guide

This guide explains how to merge updates from the upstream Firmament repository.

## What Changed

Notfimament is a renamed fork of Firmament with these changes:

| Original | Renamed |
|----------|---------|
| `moe.nea.firmament` | `moe.nea.notfimament` |
| `firmament` (mod id) | `notfimament` |
| `Firmament` (display name) | `Notfimament` |
| `firmament.mixins.json` | `notfimament.mixins.json` |
| `firmament.accesswidener` | `notfimament.accesswidener` |

Additionally, `ModAnnouncer.kt` was deleted (it sent mod lists to servers).

## Merging Upstream Changes

### Option 1: Cherry-Pick + Transform (Recommended for small updates)

```bash
# 1. Add upstream remote (only needed once)
git remote add upstream https://git.nea.moe/nea/Firmament.git

# 2. Fetch upstream
git fetch upstream

# 3. Cherry-pick the commit(s) you want
git cherry-pick <commit-hash>

# 4. There WILL be conflicts. For each conflict:
#    - Accept the upstream changes
#    - Then run the transform script

# 5. Run the transformation script
chmod +x scripts/transform-upstream.sh
./scripts/transform-upstream.sh

# 6. Review and commit
git diff
git add -A
git commit --amend
```

### Option 2: Merge + Transform (For larger updates)

```bash
# 1. Add upstream remote (only needed once)
git remote add upstream https://git.nea.moe/nea/Firmament.git

# 2. Fetch upstream
git fetch upstream

# 3. Create a merge branch
git checkout -b merge-upstream

# 4. Merge upstream (expect conflicts!)
git merge upstream/mc-1.21.11

# 5. For conflicts, generally accept upstream version ("theirs")
#    Use this for bulk resolution:
git checkout --theirs .
git add -A

# 6. Run the transformation script
./scripts/transform-upstream.sh

# 7. Review changes
git diff HEAD~1

# 8. Commit the merge
git commit
```

### Option 3: Manual Transform (For single files)

If you just need to fix one file:

```bash
# Replace in a single file
sed -i 's/moe\.nea\.firmament/moe.nea.notfimament/g' path/to/file.kt
sed -i 's/moe\/nea\/firmament/moe\/nea\/notfimament/g' path/to/file.kt
```

## After Merging

1. **Test the build:**
   ```bash
   ./gradlew build
   ```

2. **Check for missed renames:**
   ```bash
   grep -r "moe\.nea\.firmament" --include="*.kt" --include="*.java" --include="*.json"
   ```

3. **Re-delete ModAnnouncer if it was re-added:**
   ```bash
   rm -f src/main/kotlin/features/misc/ModAnnouncer.kt
   ```

## Common Issues

### Build fails with "class not found"

The transformation script missed a rename. Search for the old name:
```bash
grep -r "firmament" --include="*.kt" --include="*.java"
```

### Mixin errors

Check that `notfimament.mixins.json` has the correct package:
```json
{
  "plugin": "moe.nea.notfimament.init.MixinPlugin",
  "package": "moe.nea.notfimament.mixins"
}
```

### ServiceLoader errors

Check files in `META-INF/services/` have updated class names.

## Files to Watch

These files commonly need manual attention after merging:

- `src/main/resources/fabric.mod.json` - mod id and entrypoints
- `src/main/resources/notfimament.mixins.json` - mixin package
- `src/main/resources/notfimament.accesswidener` - accesswidener name
- `gradle.properties` - maven_group and archives_base_name
- `settings.gradle.kts` - rootProject.name
- `build.gradle.kts` - various paths
