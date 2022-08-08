package org.example.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Key value storage provide api for get, put, delete request.
 */
public class KeyValue {

    // hashmap storage
    private static final Map<String, String> db = new HashMap<>();

    /**
     * Get value by key.
     *
     * @param key the key of the value
     * @return optional value
     */
    public static Optional<String> getValueByKey(String key) {
        return Optional.ofNullable(db.get(key));
    }

    /**
     * Put a key value pair.
     *
     * @param key   key
     * @param value value
     */
    public static void put(String key, String value) {
        db.put(key, value);
    }

    /**
     * Delete a key value pair.
     *
     * @param key the key to be deleted
     * @return true if the key value pair exits then delete, false if the key doesn't exit
     */
    public static boolean delete(String key) {
        // check if the key exists
        Optional<String> value = getValueByKey(key);
        if (value.isPresent()) {
            db.remove(key);
            return true;
        }
        return false;
    }
}
