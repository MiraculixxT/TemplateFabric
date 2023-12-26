package de.miraculixx.template.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

// Only use on client side mods
@Config(name = "firstTemplate")
public class AlwaysSnowAutoConfig implements ConfigData {
    // Wiki: https://shedaniel.gitbook.io/cloth-config/auto-config/creating-a-config-class

    public boolean value1 = true;
    public String value2 = "Hey";
}
