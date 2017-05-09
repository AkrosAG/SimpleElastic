package ch.akros.cc.elastic.util;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Patrick on 10.05.2017.
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    public static boolean isJsonValid(final String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }
}
