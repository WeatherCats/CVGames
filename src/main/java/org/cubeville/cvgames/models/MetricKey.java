package org.cubeville.cvgames.models;

import java.util.Arrays;

public class MetricKey {
    public String arenaName, gameName, metricName;

    public MetricKey(String arenaName, String gameName, String metricName) {
        this.arenaName = arenaName;
        this.gameName = gameName;
        this.metricName = metricName;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new String[] { arenaName, gameName, metricName });
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MetricKey)) return false;
        MetricKey mk = (MetricKey) o;
        return mk.metricName.equalsIgnoreCase(metricName)
                && mk.gameName.equalsIgnoreCase(gameName)
                && mk.arenaName.equalsIgnoreCase(arenaName);
    }
}
