package com.example.fisheatentracker;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigButtonClicked;

@Singleton
public class FishEatenTrackerConfigButtonListener
{
    private final FishEatenTrackerPlugin plugin;

    @Inject
    public FishEatenTrackerConfigButtonListener(FishEatenTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event)
    {
        if (!"fisheatentracker".equals(event.getGroup()))
        {
            return;
        }

        if ("resetCounter".equals(event.getKey()))
        {
            plugin.resetCounter();
        }
    }
}