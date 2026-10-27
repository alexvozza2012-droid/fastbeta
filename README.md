# Portable Minecraft Beta 1.7.3 — Linux launcher

A small launcher in English for running a personal copy of Minecraft Beta 1.7.3 on Linux x86_64. It supports portable game data, a local skin, and JAR/ZIP mods that work as overlays on the original game JAR.

## Project status

The repository contains the launcher source and documentation. Download the prepared template ZIP from **Releases** to get the launcher, Java 8 runtime, LWJGL 2/JInput libraries, Linux x86_64 native libraries, and compiled project tool. **The only required game component left out of that ZIP is the Minecraft Beta 1.7.3 JAR.** The ZIP also excludes game assets, mods, and third-party skins.

## Requirements for preparing a personal copy

- Linux x86_64 with OpenGL support and any graphics/audio system libraries required by the distribution.
- A legitimate copy of Minecraft Beta 1.7.3.
- Linux x86_64 with OpenGL and the graphics/audio system libraries required by the distribution.
- JDK 8 only if you want to rebuild the included helper tool; the compiled helper and Java 8 runtime are already included.

### Minecraft Beta 1.7.3 game JAR

The SourceForge [Minecraft Versions project lists `beta_1.7.3.jar`](https://sourceforge.net/projects/minecraftversions/files/Beta/beta_1.7.3.jar/download). This is a third-party archive, not an official Mojang/Microsoft download; verify that you have the right to use the file and download it at your own discretion. Place your game JAR at `lib/beta_1.7.3.jar`.

The launcher looks for the game at `lib/beta_1.7.3.jar`. The compatible Java libraries, native libraries, Java 8 runtime, and compiled helper are already in the template. Worlds and settings are stored in `data/.minecraft/` when that folder is writable. When launched from read-only media, data is copied to the user's data directory.

The template ZIP already contains the required empty folders. Put your own files in these locations:

| Folder | What goes there |
| --- | --- |
| `lib/` | Add only `beta_1.7.3.jar`; LWJGL 2, LWJGL Util, JInput, and JUtils JARs are included |
| `natives/linux/` | Linux x86_64 LWJGL, OpenAL, and JInput native libraries are included |
| `runtime/bin/` | Java 8 runtime is included; executable is named `java` |
| `build/` | The compiled `JarAssembler.class` tool is included |
| `jarmods/` | Compatible mod `.jar` or `.zip` overlays |
| `skins/` | Optional `skin.png`, 64 × 32 pixels |
| `data/.minecraft/saves/` | Optional world folders to import |

The compiled helper is already included. If you modify it, rebuild it with JDK 8:

```sh
mkdir -p build
javac -source 8 -target 8 -d build tools/JarAssembler.java
```

Once the required personal game files and libraries are in place, launch from the project folder:

```sh
sh run-linux.sh
```

To choose a player name:

```sh
sh run-linux.sh --username Player
```

## Skins and mods

- Skin: place a classic 64 × 32 pixel PNG at `skins/skin.png`.
- Mods: place a JAR or ZIP archive compatible with JAR overlay merging in `jarmods/`. This is not a universal mod loader; mods requiring ModLoader, Babric, or another loader will not work just by placing them there.

Back up worlds in `data/.minecraft/saves/` before trying mods.

## Architecture and limitations

The Java 8 runtime in the original build and this script target Linux x86_64. Compatibility with every Linux distribution is not guaranteed; system graphics/audio libraries may also be required. ARM, Windows, and macOS are not supported by this version.

## Rights and attribution

Minecraft and its assets belong to their respective owners. Java, LWJGL, and each mod retain their own licenses and attribution requirements. This project is not affiliated with or endorsed by Mojang or Microsoft. Anyone preparing a complete distribution must verify and comply with the licenses and permissions for every included component.

See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) for details and source links for the bundled Java runtime and support libraries.

No reuse license has been assigned to the code in this repository. Publishing it on GitHub allows people to view the project, but does not automatically grant permission to reuse its code.
