import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/** Applies legacy jar-mod overlays and an optional local skin to a private jar. */
public final class JarAssembler {
    private static final String SKIN_ENTRY = "mob/char.png";

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: JarAssembler base.jar jarmods-dir skin.png|'-' output.jar");
        }

        File baseJar = new File(args[0]);
        File modsDir = new File(args[1]);
        File skinFile = "-".equals(args[2]) ? null : new File(args[2]);
        File outputJar = new File(args[3]);
        Map<String, byte[]> entries = new LinkedHashMap<String, byte[]>();

        readJar(baseJar, entries);
        File[] mods = modsDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase(Locale.ROOT);
            return lower.endsWith(".jar") || lower.endsWith(".zip");
        });
        if (mods == null) mods = new File[0];
        Arrays.sort(mods, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (File mod : mods) {
            readJar(mod, entries);
            System.out.println("Applied JAR mod: " + mod.getName());
        }

        if (skinFile != null && skinFile.isFile()) {
            BufferedImage image = ImageIO.read(skinFile);
            if (image == null || image.getWidth() != 64 || image.getHeight() != 32) {
                throw new IllegalArgumentException("The skin must be a PNG measuring exactly 64x32 pixels.");
            }
            if (!entries.containsKey(SKIN_ENTRY)) {
                throw new IllegalArgumentException("The JAR does not contain the mob/char.png texture.");
            }
            entries.put(SKIN_ENTRY, Files.readAllBytes(skinFile.toPath()));
            System.out.println("Applied local skin: " + skinFile.getAbsolutePath());
        }

        File parent = outputJar.getAbsoluteFile().getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create directory: " + parent);
        }
        File temporary = new File(parent, outputJar.getName() + ".tmp");
        if (temporary.exists() && !temporary.delete()) {
            throw new IllegalStateException("Could not remove temporary file: " + temporary);
        }

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(temporary))) {
            for (Map.Entry<String, byte[]> item : entries.entrySet()) {
                out.putNextEntry(new ZipEntry(item.getKey()));
                out.write(item.getValue());
                out.closeEntry();
            }
        } catch (Exception e) {
            temporary.delete();
            throw e;
        }

        try {
            Files.move(temporary.toPath(), outputJar.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception atomicMoveUnavailable) {
            Files.move(temporary.toPath(), outputJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void readJar(File jarFile, Map<String, byte[]> target) throws Exception {
        try (ZipFile zip = new ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (entry.isDirectory() || isSignatureMetadata(name)) continue;
                if (name.startsWith("/") || name.indexOf('\\') >= 0 || name.contains("../")) {
                    throw new IllegalArgumentException("Invalid path in JAR " + jarFile.getName() + ": " + name);
                }
                try (InputStream in = zip.getInputStream(entry)) {
                    target.put(name, readAll(in));
                }
            }
        }
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[32768];
        int count;
        while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
        return out.toByteArray();
    }

    private static boolean isSignatureMetadata(String name) {
        String upper = name.toUpperCase(Locale.ROOT);
        if (!upper.startsWith("META-INF/")) return false;
        String leaf = upper.substring("META-INF/".length());
        if (leaf.indexOf('/') >= 0) return false;
        return leaf.equals("MANIFEST.MF") || leaf.equals("INDEX.LIST") || leaf.endsWith(".SF")
                || leaf.endsWith(".RSA") || leaf.endsWith(".DSA") || leaf.endsWith(".EC")
                || leaf.startsWith("SIG-");
    }
}
