# Third-party software and license notices

This template redistributes the following runtime components. Their original licenses remain in effect. The bundled Java runtime also contains its upstream `THIRD_PARTY_README` and `ASSEMBLY_EXCEPTION` files in `runtime/`.

## OpenJDK 8 runtime

The bundled runtime is Ubuntu OpenJDK 8, version `8u504-ga-1ubuntu1~24.04.3`, for Linux x86_64. OpenJDK is distributed under GPL version 2 with the Classpath Exception. The GPL text is in `licenses/GPL-2.txt`; the OpenJDK Assembly Exception is in `runtime/ASSEMBLY_EXCEPTION`; additional notices are in `runtime/THIRD_PARTY_README`.

Corresponding Ubuntu source package: https://packages.ubuntu.com/noble/openjdk-8-jdk (select **Download Source Package**; this package version matches the bundled runtime).

## LWJGL 2 and LWJGL Util

Bundled version: `2.9.4+legacyfabric.15`. LWJGL 2 is under a BSD license. Project and license: https://legacy.lwjgl.org/ and https://legacy.lwjgl.org/license.php.html. Binary redistribution requires retaining the copyright, conditions, and disclaimer in the distribution documentation.

## JInput and JUtils

JInput and JUtils are Java Game Technology Group libraries distributed under BSD-style licenses. JInput's project and license information: https://github.com/jinput/jinput. JUtils project: https://github.com/jinput/jutils. Their source files carry the applicable copyright and license notices; the BSD license terms require reproducing those notices, conditions, and disclaimers with binary distributions.

## OpenAL Soft native library

Bundled version: `1.24.1`, Linux x86_64. OpenAL Soft is under GNU LGPL version 2 or later. The license text is in `licenses/LGPL-2.txt`. Corresponding source: https://github.com/kcat/openal-soft/tree/1.24.1. This native library is dynamically loaded by LWJGL.

These notices cover only the bundled runtime and support libraries. Minecraft and its assets, the game JAR, and any user-added mods or skins are not included in this template. The Minecraft JAR linked in the README is hosted by a third party and is not an official Mojang/Microsoft download.
