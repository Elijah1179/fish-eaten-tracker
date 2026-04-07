package com.example.fisheatentracker;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("fisheatentracker")
public interface FishEatenTrackerConfig extends Config
{
    @ConfigItem(
            keyName = "showOverlay",
            name = "Show overlay",
            description = "Show the fish eaten overlay"
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "resetCounter",
            name = "Reset counter",
            description = "Reset the fish eaten total"
    )
    default Button resetCounter()
    {
        return new Button();
    }
}