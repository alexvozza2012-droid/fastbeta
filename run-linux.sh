#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BUNDLED_JAVA="$SCRIPT_DIR/runtime/bin/java"
USERNAME=Player

while [ "$#" -gt 0 ]; do
    case "$1" in
        --username)
            if [ "$#" -lt 2 ]; then
                echo "Usage: sh run-linux.sh [--username NAME]" >&2
                exit 2
            fi
            USERNAME=$2
            shift 2
            ;;
        --help)
            echo "Usage: sh run-linux.sh [--username NAME]"
            exit 0
            ;;
        *)
            echo "Unknown option: $1" >&2
            echo "Usage: sh run-linux.sh [--username NAME]" >&2
            exit 2
            ;;
    esac
done

if [ -x "$BUNDLED_JAVA" ] && "$BUNDLED_JAVA" -version >/dev/null 2>&1; then
    JAVA="$BUNDLED_JAVA"
else
    JAVA=$(command -v java || true)
    if [ -z "$JAVA" ] || ! "$JAVA" -version 2>&1 | grep -q '1\.8\.'; then
        echo "Java 8 is unavailable (the bundled runtime cannot start and the system Java version is incompatible)." >&2
        exit 1
    fi
fi

# On USB, keep saves beside the game. From read-only media, prepare a
# writable copy in the user's data directory.
GAME_DIR="$SCRIPT_DIR/data"
if [ ! -w "$GAME_DIR" ]; then
    GAME_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/minecraft-beta-1.7.3"
    mkdir -p "$GAME_DIR"
    if [ ! -d "$GAME_DIR/.minecraft" ]; then
        cp -a "$SCRIPT_DIR/data/." "$GAME_DIR/"
    fi
fi

NATIVES="$SCRIPT_DIR/natives/linux"
GAME_JAR="$SCRIPT_DIR/lib/beta_1.7.3.jar"
LOCAL_SKIN="$SCRIPT_DIR/skins/skin.png"
JARMODS_DIR="$SCRIPT_DIR/jarmods"
HAS_JARMODS=no

for mod_jar in "$JARMODS_DIR"/*.jar "$JARMODS_DIR"/*.zip; do
    if [ -f "$mod_jar" ]; then
        HAS_JARMODS=yes
        break
    fi
done

if [ -f "$LOCAL_SKIN" ] || [ "$HAS_JARMODS" = yes ]; then
    mkdir -p "$GAME_DIR/cache"
    MODDED_JAR="$GAME_DIR/cache/beta_1.7.3-mods.jar"
    "$JAVA" -Djava.awt.headless=true -cp "$SCRIPT_DIR/build" \
        JarAssembler "$GAME_JAR" "$JARMODS_DIR" \
        "${LOCAL_SKIN:-}" "$MODDED_JAR"
    GAME_JAR="$MODDED_JAR"
fi

CLASSPATH="$GAME_JAR:$SCRIPT_DIR/lib/lwjgl.jar:$SCRIPT_DIR/lib/lwjgl_util.jar:$SCRIPT_DIR/lib/jinput.jar:$SCRIPT_DIR/lib/jutils.jar"

exec "$JAVA" \
    -Djavax.accessibility.assistive_technologies= \
    -Duser.home="$GAME_DIR" \
    -Djava.library.path="$NATIVES" \
    -Dorg.lwjgl.librarypath="$NATIVES" \
    -Dorg.lwjgl.opengl.Display.allowSoftwareOpenGL=true \
    -Xms256m -Xmx512m \
    -cp "$CLASSPATH" \
    net.minecraft.client.Minecraft \
    "$USERNAME" "-"
