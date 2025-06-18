package it.unipi.adrien.koumgang;

import java.text.Normalizer;

public class TextUtils {
    public static String cleanToken(String token) {
        if (token == null) return "";

        // Normalize and remove accents (e.g., é → e, ü → u)
        String normalized = Normalizer.normalize(token, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        return withoutAccents.toLowerCase() // normalize case
                .replaceAll("[^a-zA-Z0-9]", "") // remove punctuation/symbols
                .trim() // remove whitespace
                ;
    }
}
