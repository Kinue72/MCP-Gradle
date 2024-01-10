import java.io.File;
import java.util.Arrays;

import net.minecraft.client.main.Main;

public class Start
{
    public static void main(String[] args)
    {
        Main.main(concat(new String[] {
                "--version",
                "mcp",
                "--accessToken",
                "0",
                "--assetsDir",
                getAssetsDir().getAbsolutePath(),
                "--assetIndex",
                "1.8",
                "--userProperties",
                "{}"
        }, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static File getAssetsDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        File dotMinecraft;

        if (osName.contains("win")) {
            dotMinecraft = new File(userHome, ".minecraft");
        } else if (osName.contains("mac")) {
            dotMinecraft = new File(userHome, "Library/Application Support/minecraft");
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("uni")) {
            dotMinecraft = new File(userHome, ".minecraft");
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        return new File(dotMinecraft, "assets");
    }
}
