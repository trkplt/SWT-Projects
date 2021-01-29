package org.iMage.plugins;

/**
 * Abstract parent class for plug-ins for JMJRST
 *
 * @author Dominik Fuchss
 */
public abstract class PluginForJmjrst implements Comparable<PluginForJmjrst> {

    /**
     * Returns the name of this plug-in
     *
     * @return the name of the plug-in
     */
    public abstract String getName();

    /**
     * Returns the number of parameters of this plug-in
     *
     * @return the number of parameters of this plug-in
     */
    public abstract int getNumberOfParameters();

    /**
     * JMJRST pushes the main application to every subclass - so plug-ins are allowed to look at Main
     * as well.
     *
     * @param main JMJRST main application
     */
    public abstract void init(org.jis.Main main);

    /**
     * Runs plug-in
     */
    public abstract void run();

    /**
     * Returns whether the plug-in can be configured or not
     *
     * @return true if the plug-in can be configured.
     */
    public abstract boolean isConfigurable();

    /**
     * Open a configuration dialogue.
     */
    public abstract void configure();

    @Override
    public int compareTo(PluginForJmjrst otherPlugin) {
        if (this.equals(otherPlugin)) {
            return 0;
        }

        String thisName = this.getName();
        String otherName = otherPlugin.getName();

        if (thisName.equals(otherName)) {
            return (this.getNumberOfParameters() < otherPlugin.getNumberOfParameters() ? -1 : 1);
        }

        return thisName.compareTo(otherName);
    }

    @Override
    public int hashCode() {
        final int multiplier = 31;
        int result = 17;

        result = multiplier * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = multiplier * result + this.getNumberOfParameters();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        PluginForJmjrst plugin = (PluginForJmjrst) obj;
        return this.getName().equals(plugin.getName())
                && this.getNumberOfParameters() == plugin.getNumberOfParameters();
    }
}
