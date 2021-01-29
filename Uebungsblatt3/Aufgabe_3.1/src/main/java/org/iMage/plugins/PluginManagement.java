package org.iMage.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Knows all available plug-ins and is responsible for using the service loader API to detect them.
 *
 * @author Dominik Fuchss
 */
public final class PluginManagement {

    /**
     * No constructor for utility class.
     */
    private PluginManagement() {
        throw new IllegalAccessError();
    }

    /**
     * Return an {@link Iterable} Object with all available {@link PluginForJmjrst PluginForJmjrsts}
     * sorted alphabetically according to their name. In case of equally named Plugins sort them by
     * the number of parameters they have (less first).
     *
     * @return an {@link Iterable} Object containing all available plug-ins
     */
    public static Iterable<PluginForJmjrst> getPlugins() {
        ServiceLoader<PluginForJmjrst> loader = ServiceLoader.load(PluginForJmjrst.class);

        List<PluginForJmjrst> list = new ArrayList<>();
        for (PluginForJmjrst plugin : loader) {
            list.add(plugin);
        }

        list.sort(null);
        return list;
    }

}
