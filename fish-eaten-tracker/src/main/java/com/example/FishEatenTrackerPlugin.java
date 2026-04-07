package com.example.fisheatentracker;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "Fish Eaten Tracker",
        description = "Tracks how many fish you eat",
        tags = {"fish", "food", "tracker", "cox", "raids"}
)
public class FishEatenTrackerPlugin extends Plugin
{
    private static final Set<String> FISH_NAMES = new HashSet<>(Arrays.asList(
            "shrimp",
            "anchovies",
            "sardine",
            "herring",
            "trout",
            "salmon",
            "tuna",
            "lobster",
            "bass",
            "swordfish",
            "monkfish",
            "shark",
            "sea turtle",
            "manta ray",
            "anglerfish",
            "karambwan",

            // Chambers of Xeric fish
            "pysk fish",
            "suphi fish",
            "leckish fish",
            "brawk fish",
            "mycil fish",
            "roqed fish",
            "kyren fish"
    ));

    private static final String CONFIG_GROUP = "fisheatentracker";
    private static final String COUNT_KEY = "fishEatenCount";

    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private FishEatenTrackerConfig config;

    @Inject
    private FishEatenTrackerOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    private int fishEatenCount;
    private int pendingEatItemId = -1;
    private int pendingEatInventoryCount = -1;
    private boolean pendingEat = false;

    @Provides
    FishEatenTrackerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(FishEatenTrackerConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        fishEatenCount = configManager.getRSProfileConfiguration(CONFIG_GROUP, COUNT_KEY, int.class, 0);
        resetPending();
        log.info("Fish Eaten Tracker started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        resetPending();
        log.info("Fish Eaten Tracker stopped");
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        String option = event.getMenuOption();
        if (option == null || !option.equalsIgnoreCase("Eat"))
        {
            return;
        }

        int itemId = event.getItemId();
        if (itemId <= 0 || !isFish(itemId))
        {
            return;
        }

        pendingEat = true;
        pendingEatItemId = itemId;
        pendingEatInventoryCount = getInventoryCount(itemId);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!pendingEat)
        {
            return;
        }

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null || event.getItemContainer() != inventory)
        {
            return;
        }

        int newCount = getInventoryCount(pendingEatItemId);
        if (pendingEatInventoryCount > 0 && newCount < pendingEatInventoryCount)
        {
            fishEatenCount++;
            saveCount();
            resetPending();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (pendingEat)
        {
            resetPending();
        }
    }

    public int getFishEatenCount()
    {
        return fishEatenCount;
    }

    public void resetCounter()
    {
        fishEatenCount = 0;
        saveCount();
        resetPending();
    }

    private void saveCount()
    {
        configManager.setRSProfileConfiguration(CONFIG_GROUP, COUNT_KEY, fishEatenCount);
    }

    private void resetPending()
    {
        pendingEat = false;
        pendingEatItemId = -1;
        pendingEatInventoryCount = -1;
    }

    private int getInventoryCount(int itemId)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null)
        {
            return 0;
        }

        int count = 0;
        for (Item item : inventory.getItems())
        {
            if (item.getId() == itemId)
            {
                count += item.getQuantity();
            }
        }
        return count;
    }

    private boolean isFish(int itemId)
    {
        String name = itemManager.getItemComposition(itemId).getName();
        if (name == null)
        {
            return false;
        }

        return FISH_NAMES.contains(name.toLowerCase().trim());
    }
}