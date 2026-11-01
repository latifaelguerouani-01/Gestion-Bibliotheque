package com.biblio.util;

import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MessageUtil {
    private MessageUtil() {}

    public static String get(HttpSession session, String key) {
        String language = "fr";
        if (session != null) {
            Object selected = session.getAttribute("langue");
            if (selected == null) selected = session.getAttribute("language");
            if (selected != null) language = selected.toString().toLowerCase(Locale.ROOT);
        }

        if (language.startsWith("en")) language = "en";
        else if (language.startsWith("ar")) language = "ar";
        else language = "fr";

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                "messages", new Locale(language), new UTF8Control()
            );
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    private static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, java.io.IOException {
            String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
            try (java.io.InputStream in = loader.getResourceAsStream(resourceName)) {
                if (in == null) return null;
                try (java.io.InputStreamReader reader =
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)) {
                    return new java.util.PropertyResourceBundle(reader);
                }
            }
        }
    }
}
