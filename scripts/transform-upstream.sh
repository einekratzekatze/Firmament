#!/bin/bash
# Transform upstream Firmament to Notfimament
# This script renames all firmament references to notfimament
#
# Usage: ./scripts/transform-upstream.sh
#
# Run this after merging/cherry-picking upstream changes to fix package names.

set -e

echo "=== Notfimament Transformation Script ==="
echo ""

# Check we're in the right directory
if [ ! -f "build.gradle.kts" ]; then
    echo "ERROR: Run this script from the project root directory"
    exit 1
fi

# 1. Rename directories (if they exist with old names)
echo "[1/6] Renaming directories..."

rename_dir() {
    if [ -d "$1" ]; then
        mkdir -p "$(dirname "$2")"
        mv "$1" "$2"
        echo "  Renamed: $1 -> $2"
    fi
}

# Find and rename all firmament directories to notfimament
find . -type d -path "*/moe/nea/firmament*" 2>/dev/null | sort -r | while read dir; do
    newdir=$(echo "$dir" | sed 's|/firmament|/notfimament|g')
    if [ "$dir" != "$newdir" ]; then
        mkdir -p "$(dirname "$newdir")"
        mv "$dir" "$newdir"
        echo "  Renamed: $dir -> $newdir"
    fi
done

# 2. Rename files (firmament.* -> notfimament.*)
echo "[2/6] Renaming files..."

find . -type f \( -name "firmament.*" -o -name "Firmament-*" \) 2>/dev/null | while read file; do
    newfile=$(echo "$file" | sed 's/firmament\./notfimament./g' | sed 's/Firmament-/Notfimament-/g')
    if [ "$file" != "$newfile" ]; then
        mv "$file" "$newfile"
        echo "  Renamed: $file -> $newfile"
    fi
done

# 3. Replace in source files (.kt, .java)
echo "[3/6] Updating source files (.kt, .java)..."

find . -type f \( -name "*.kt" -o -name "*.java" \) \
    ! -path "./.gradle/*" \
    ! -path "./build/*" \
    ! -path "./.git/*" \
    -print0 2>/dev/null | xargs -0 sed -i \
    -e 's/moe\.nea\.firmament/moe.nea.notfimament/g' \
    -e 's/moe\/nea\/firmament/moe\/nea\/notfimament/g'

echo "  Done"

# 4. Replace in config files (.json, .properties, .kts)
echo "[4/6] Updating config files..."

find . -type f \( -name "*.json" -o -name "*.properties" -o -name "*.kts" \) \
    ! -path "./.gradle/*" \
    ! -path "./build/*" \
    ! -path "./.git/*" \
    -print0 2>/dev/null | xargs -0 sed -i \
    -e 's/moe\.nea\.firmament/moe.nea.notfimament/g' \
    -e 's/moe\/nea\/firmament/moe\/nea\/notfimament/g' \
    -e 's/"firmament"/"notfimament"/g' \
    -e 's/Firmament/Notfimament/g' \
    -e 's/firmament\./notfimament./g'

echo "  Done"

# 5. Replace in resource files (.accesswidener, etc)
echo "[5/6] Updating resource files..."

find . -type f \( -name "*.accesswidener" -o -name "*.mixins.json" \) \
    ! -path "./.gradle/*" \
    ! -path "./build/*" \
    ! -path "./.git/*" \
    -print0 2>/dev/null | xargs -0 sed -i \
    -e 's/moe\.nea\.firmament/moe.nea.notfimament/g' \
    -e 's/moe\/nea\/firmament/moe\/nea\/notfimament/g' \
    -e 's/firmament\./notfimament./g'

echo "  Done"

# 6. Update META-INF services
echo "[6/6] Updating META-INF services..."

find . -path "*/META-INF/services/*" -type f \
    ! -path "./.gradle/*" \
    ! -path "./build/*" \
    -print0 2>/dev/null | xargs -0 sed -i \
    -e 's/moe\.nea\.firmament/moe.nea.notfimament/g'

echo "  Done"

echo ""
echo "=== Transformation Complete ==="
echo ""
echo "Next steps:"
echo "  1. Review changes: git diff"
echo "  2. Test build: ./gradlew build"
echo "  3. If there are issues, check for missed renames"
echo ""
