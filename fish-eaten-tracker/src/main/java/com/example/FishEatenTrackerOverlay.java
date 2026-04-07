package com.example.fisheatentracker;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class FishEatenTrackerOverlay extends OverlayPanel
{
    private final FishEatenTrackerPlugin plugin;
    private final FishEatenTrackerConfig config;

    @Inject
    public FishEatenTrackerOverlay(FishEatenTrackerPlugin plugin, FishEatenTrackerConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showOverlay())
        {
            return null;
        }

        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fish eaten")
                .right(String.valueOf(plugin.getFishEatenCount()))
                .build());

        return super.render(graphics);
    }
}