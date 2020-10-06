package xyz.wagyourtail.jsmacros.api.classes;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.access.IHorseScreen;
import xyz.wagyourtail.jsmacros.access.IInventory;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;

/**
 * @author Wagyourtail
 *
 */
public class Inventory {
    private HandledScreen<?> inventory;
    private Map<String, int[]> map;
    private ClientPlayerInteractionManager man;
    private int wID;
    private ClientPlayerEntity player;

    public Inventory() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof HandledScreen) {
            this.inventory = (HandledScreen<?>) mc.currentScreen;
        } else {
            this.inventory = new InventoryScreen(mc.player);
        }
        this.player = mc.player;
        this.man = mc.interactionManager;
        this.wID = this.inventory.getScreenHandler().syncId;
    }

    /**
     * Clicks a slot with a mouse button.
     * 
     * @param slot
     * @param mousebutton
     * @return
     */
    public Inventory click(int slot, int mousebutton) {
        SlotActionType act = mousebutton == 2 ? SlotActionType.CLONE : SlotActionType.PICKUP;
        man.clickSlot(wID, slot, mousebutton, act, player);
        return this;
    }

    /**
     * Does a drag-click with a mouse button. (the slots don't have to be in order or even adjacent, but when vanilla minecraft calls the underlying function they're always sorted...)
     * 
     * @param slots
     * @param mousebutton
     * @return
     */
    public Inventory dragClick(int[] slots, int mousebutton) {
        mousebutton = mousebutton == 0 ? 1 : 5;
        man.clickSlot(wID, -999, mousebutton - 1, SlotActionType.QUICK_CRAFT, player); // start drag click
        for (int i : slots) {
            man.clickSlot(wID, i, mousebutton, SlotActionType.QUICK_CRAFT, player);
        }
        man.clickSlot(wID, -999, mousebutton + 1, SlotActionType.QUICK_CRAFT, player);
        return this;
    }
    
    /**
     * @since 1.2.5
     * 
     * @return the index of the selected hotbar slot.
     */
    public int getSelectedHotbarSlotIndex() {
        return player.inventory.selectedSlot;
    }
    
    /**
     * @since 1.2.5
     * 
     * @param index
     */
    public void setSelectedHotbarSlotIndex(int index) {
        if (PlayerInventory.isValidHotbarIndex(index))
            player.inventory.selectedSlot = index;
    }

    /**
     * closes the inventory, (if the inventory/container is visible it will close the gui). also drops any "held on mouse" items.
     * 
     * @return
     */
    public Inventory closeAndDrop() {
        ItemStack held = player.inventory.getCursorStack();
        if (!held.isEmpty()) man.clickSlot(wID, -999, 0, SlotActionType.PICKUP, player);
        player.closeHandledScreen();
        this.inventory = null;
        return this;
    }

    /**
     * Closes the inventory, and open gui if applicable.
     */
    public void close() {
        player.closeHandledScreen();
    }

    /**
     * simulates a shift-click on a slot.
     * 
     * @param slot
     * @return
     */
    public Inventory quick(int slot) {
        man.clickSlot(wID, slot, 0, SlotActionType.QUICK_MOVE, player);
        return this;
    }

    /**
     * @return the held (by the mouse) item.
     */
    public ItemStackHelper getHeld() {
        return new ItemStackHelper(player.inventory.getCursorStack());
    }

    /**
     * 
     * @param slot
     * @return the item in the slot.
     */
    public ItemStackHelper getSlot(int slot) {
        return new ItemStackHelper(this.inventory.getScreenHandler().getSlot(slot).getStack());
    }

    /**
     * @return the size of the container/inventory.
     */
    public int getTotalSlots() {
        return this.inventory.getScreenHandler().slots.size();
    }

    /**
     * Splits the held stack into two slots. can be alternatively done with {@link Inventory#dragClick(int[], int)} if this one has issues on some servers.
     * 
     * @param slot1
     * @param slot2
     * @return
     * @throws Exception
     */
    public Inventory split(int slot1, int slot2) throws Exception {
        if (slot1 == slot2) throw new Exception("must be 2 different slots.");
        if (!getSlot(slot1).isEmpty() || !getSlot(slot2).isEmpty()) throw new Exception("slots must be empty.");
        man.clickSlot(wID, slot1, 1, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot2, 0, SlotActionType.PICKUP, player);
        return this;
    }

    /**
     * does that double click on a slot thingy to move all of a type into/out-of a chest.
     * 
     * @param slot
     * @return
     */
    public Inventory grabAll(int slot) {
        man.clickSlot(wID, slot, 0, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot, 0, SlotActionType.PICKUP_ALL, player);
        return this;
    }

    /**
     * swaps the items in two slots.
     * 
     * @param slot1
     * @param slot2
     * @return
     */
    public Inventory swap(int slot1, int slot2) {
        boolean is1 = getSlot(slot1).isEmpty();
        boolean is2 = getSlot(slot2).isEmpty();
        if (is1 && is2) return this;
        if (!is1) man.clickSlot(wID, slot1, 0, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot2, 0, SlotActionType.PICKUP, player);
        if (!is2) man.clickSlot(wID, slot1, 0, SlotActionType.PICKUP, player);
        return this;
    }

    /**
     * @since 1.1.3
     * 
     * @return the id of the slot under the mouse.
     */
    public int getSlotUnderMouse() {
        MinecraftClient mc = MinecraftClient.getInstance();
        double x = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
        double y = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
        
        Slot s = ((IInventory)this.inventory).jsmacros_getSlotUnder(x, y);
        if (s == null) return -999;
        return this.inventory.getScreenHandler().slots.indexOf(s);
    }
    
    /**
     * @since 1.1.3
     * 
     * @return the part of the mapping the slot is in.
     */
    public String getType() {
        return jsMacros.getScreenName(this.inventory);
    }

    /**
     * @since 1.1.3
     * 
     * @return the inventory mappings different depending on the type of open container/inventory.
     */
    public Map<String, int[]> getMap() {
        if (map == null) {
            map = getMapInternal();
        }
        return map;
    }
    
    /**
     * @since 1.1.3
     * 
     * @param slotNum
     * @return returns the part of the mapping the slot is in.
     */
    public String getLocation(int slotNum) {
        if (map == null) {
            map = getMapInternal();
        }
        for (String k : map.keySet()) {
           for (int i : map.get(k)) {
                if (i == slotNum) {
                    return k;
                }
            }
        }
        return null;
    }
    
    private Map<String, int[]> getMapInternal() {
        Map<String, int[]> map = new HashMap<>();
        int slots = getTotalSlots();
        if (this.inventory instanceof InventoryScreen || (this.inventory instanceof CreativeInventoryScreen && ((CreativeInventoryScreen) this.inventory).getSelectedTab() == ItemGroup.INVENTORY.getIndex())) {
            if (this.inventory instanceof CreativeInventoryScreen) {
                map.put("delete", new int[] {--slots});
            } 
            map.put("hotbar", jsMacros.range(slots - 10, slots - 1)); // range(36, 45);
            map.put("offhand", new int[] { slots - 1 }); // range(45, 46);
            map.put("main", jsMacros.range(slots - 10 - 27, slots - 10)); // range(9, 36);
            map.put("boots", new int[] { slots - 10 - 27 - 1 }); // range(8, 9);
            map.put("leggings", new int[] { slots - 10 - 27 - 2 }); // range(7, 8);
            map.put("chestplate", new int[] { slots - 10 - 27 - 3 }); // range(6, 7);
            map.put("helmet", new int[] { slots - 10 - 27 - 4 }); // range(5, 6);
            map.put("crafting_in", jsMacros.range(slots - 10 - 27 - 4 - 4, slots - 10 - 27 - 4)); // range(1, 5);
            map.put("craft_out", new int[] { slots - 10 - 27 - 4 - 4 - 1 });
        } else {
            map.put("hotbar", jsMacros.range(slots - 9, slots));
            map.put("main", jsMacros.range(slots - 9 - 27, slots - 9));
            if (inventory instanceof CreativeInventoryScreen) {
                map.remove("main");
                map.put("creative", jsMacros.range(slots - 9));
            } else if (inventory instanceof GenericContainerScreen || inventory instanceof Generic3x3ContainerScreen || inventory instanceof HopperScreen || inventory instanceof ShulkerBoxScreen) {
                map.put("container", jsMacros.range(slots - 9 - 27));
            } else if (inventory instanceof BeaconScreen) {
                map.put("slot", new int[] { slots - 9 - 27 - 1 });
            } else if (inventory instanceof BlastFurnaceScreen || inventory instanceof FurnaceScreen || inventory instanceof SmokerScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("fuel", new int[] { slots - 9 - 27 - 2 });
                map.put("input", new int[] { slots - 9 - 27 - 3 });
            } else if (inventory instanceof BrewingStandScreen) {
                map.put("fuel", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
                map.put("output", jsMacros.range(slots - 9 - 27 - 2));
            } else if (inventory instanceof CraftingScreen) {
                map.put("input", jsMacros.range(slots - 9 - 27 - 9, slots - 9 - 27));
                map.put("output", new int[] { slots - 9 - 27 - 10 });
            } else if (inventory instanceof EnchantmentScreen) {
                map.put("lapis", new int[] { slots - 9 - 27 - 1 });
                map.put("item", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof LoomScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("pattern", new int[] { slots - 9 - 27 - 2 });
                map.put("dye", new int[] { slots - 9 - 27 - 3 });
                map.put("banner", new int[] { slots - 9 - 27 - 4 });
            } else if (inventory instanceof StonecutterScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof HorseScreen) {
                HorseBaseEntity h = (HorseBaseEntity) ((IHorseScreen)this.inventory).jsmacros_getEntity();
                if (h.canBeSaddled()) map.put("saddle", new int[] {0});
                if (h.hasArmorSlot()) map.put("armor", new int[] {1});
                if (h instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity) h).hasChest()) {
                    map.put("container", jsMacros.range(2, slots - 9 - 27));
                }
            } else if (inventory instanceof AnvilScreen || inventory instanceof SmithingScreen || inventory instanceof GrindstoneScreen || inventory instanceof CartographyTableScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", jsMacros.range(slots - 9 - 27 - 1));
            }
        }

        return map;
    }

    /**
     * @since 1.2.3
     * 
     * @return
     */
    public String getContainerTitle() {
        return this.inventory.getTitle().getString();
    }
    
    public HandledScreen<?> getRawContainer() {
        return this.inventory;
    }
    
    public String toString() {
        return String.format("Inventory:{\"Type\": \"%s\"}", this.getType());
    }

}