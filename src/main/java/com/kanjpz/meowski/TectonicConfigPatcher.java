package com.kanjpz.meowski;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TectonicConfigPatcher {

    // Add/adjust any key here — value is written exactly as-is into the JSON.
    private static final Map<String, String> DESIRED_VALUES = new LinkedHashMap<>();
    static {
        DESIRED_VALUES.put("ultrasmooth", "true");
        DESIRED_VALUES.put("vertical_scale", "0.85");
        DESIRED_VALUES.put("flat_terrain_skew", "0.3");
        DESIRED_VALUES.put("ocean_depth", "-0.3");
        DESIRED_VALUES.put("deep_ocean_depth", "-0.5");
    }

    public static void applyDesiredSettings() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("tectonic.json");

        if (!Files.exists(configPath)) {
            // Tectonic hasn't written its config yet — shouldn't happen if
            // ordering="AFTER" is set correctly, but bail out safely if it does.
            System.out.println("[Meowskis] tectonic.json not found yet, skipping config patch.");
            return;
        }

        try {
            String content = Files.readString(configPath, StandardCharsets.UTF_8);
            boolean changed = false;

            for (Map.Entry<String, String> entry : DESIRED_VALUES.entrySet()) {
                String key = entry.getKey();
                String newValue = entry.getValue();

                // Matches:  "key": <value>,   or   "key": <value>  (no trailing comma, e.g. last field in a block)
                Pattern pattern = Pattern.compile(
                        "(\"" + Pattern.quote(key) + "\"\\s*:\\s*)([^,\\n\\r]+)"
                );
                Matcher matcher = pattern.matcher(content);

                if (matcher.find()) {
                    String currentValue = matcher.group(2).trim();
                    // Preserve a trailing comma if the original had one.
                    String replacement = matcher.group(1) + newValue
                            + (currentValue.endsWith(",") ? "," : "");
                    String updated = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
                    if (!updated.equals(content)) {
                        content = updated;
                        changed = true;
                    }
                } else {
                    System.out.println("[Meowskis] Could not find tectonic key '" + key + "' to patch — skipping.");
                }
            }

            if (changed) {
                Files.writeString(configPath, content, StandardCharsets.UTF_8);
                System.out.println("[Meowskis] Applied Meowskis terrain defaults to tectonic.json.");
            }

        } catch (IOException e) {
            System.out.println("[Meowskis] Failed to patch tectonic.json: " + e.getMessage());
        }
    }
}