package edu.cmu.tartan.item;

import edu.cmu.tartan.GameInterface;
import edu.cmu.tartan.properties.Inspectable;
import edu.cmu.tartan.properties.Valuable;
import edu.cmu.tartan.properties.Visible;
import edu.cmu.tartan.room.Room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the main class for game items. Items are things that can be used in the game
 * <p>
 * Project: LG Exec Ed SDET Program
 * 2018 Jeffrey S. Gennari
 * Versions:
 * 1.0 March 2018 - initial version
 */
public class Item implements Comparable, Inspectable, Visible, Valuable, Serializable {
	/**
	 * Version for serialization
	 */
	private static final long serialVersionUID = 1L;

	protected static final transient GameInterface gameInterface = GameInterface.getInterface();
	
	private static final String UNKNOWN = "unknown"; 

    // every item is visible by default
    private boolean visible = true;
    private Integer value = null;

    /**
     * Items are referenced by descriptions
     */
    private String description=null;
    protected String detailDescription= null;

    /**
     * Items can have a list of unique aliases
     */
    private String[] aliases;

    /*
     *  items can open rooms, call elevators, etc (e.g., an ItemButton instance)
     */
    protected transient Room relatedRoom;
    
    /*
     *  items can also affect other items, 
     *  like setting other items breakable (like a junction box)
     */
    protected Item relatedItem; 

    private String inspectMessage;

	private static Map<String, Map<String, Item>>itemMapbyUser;
	private static Map<String, Item>itemMap;
    /**
     * Create a new item
     * @param description short description
     * @param detailDescription long description
     * @param a alias list
     */
    public Item(String description, String detailDescription, String[] a) {
        this.description = description;
        this.detailDescription = detailDescription;
        this.aliases = a;
        this.relatedRoom = null;
        this.relatedItem = null;
        this.inspectMessage = null;
        this.value = null;
    }

    /**
     * Initialize default items. These are the items initially available
     */
    private static void makeItems(String userId) {
    	itemMap.put(StringForItems.SHOVEL, new ItemShovel(StringForItems.SHOVEL, StringForItems.METAL_SHOVEL, new String[]{StringForItems.SHOVEL}));
    	itemMap.put(StringForItems.BRICK, new ItemBrick(StringForItems.BRICK, StringForItems.CLAY_BRICK, new String[]{StringForItems.BRICK}));
    	itemMap.put(StringForItems.FOOD, new ItemFood(StringForItems.FOOD, StringForItems.FOOD, new String[]{StringForItems.FOOD}));
    	itemMap.put(StringForItems.LADDER, new ItemLadder(StringForItems.LADDER, StringForItems.WOODEN_LADDER, new String[]{StringForItems.LADDER}));
    	itemMap.put(StringForItems.KEY, new ItemKey(StringForItems.KEY, StringForItems.GOLD_KEY, new String[]{StringForItems.KEY}));
    	itemMap.put(StringForItems.LOCK, new ItemLock(StringForItems.LOCK, StringForItems.GOLD_LOCK, new String[]{StringForItems.LOCK}));
    	itemMap.put(StringForItems.KEYCARD, new ItemKeycard(StringForItems.KEYCARD, StringForItems.PLASTIC_KEYCARD, new String[]{StringForItems.KEYCARD, StringForItems.CARD}));
    	itemMap.put(StringForItems.KEYCARD_READER, new ItemKeycardReader(StringForItems.KEYCARD_READER, StringForItems.METAL_KEYCARD_READER, new String[]{StringForItems.READER, StringForItems.SLOT}));
    	itemMap.put(StringForItems.POT, new ItemClayPot(StringForItems.POT, StringForItems.CLAY_POT, new String[]{StringForItems.POT, StringForItems.POTTERY}));
    	itemMap.put(StringForItems.DIAMOND, new ItemDiamond(StringForItems.DIAMOND, StringForItems.WHITE_DIAMOND, new String[]{StringForItems.DIAMOND, StringForItems.JEWEL}));
        itemMap.put(StringForItems.GOLD, new ItemGold(StringForItems.GOLD, StringForItems.SHINY_GOLD_BAR, new String[]{StringForItems.GOLD, StringForItems.BAR}));
        itemMap.put(StringForItems.MICROWAVE, new ItemMicrowave(StringForItems.MICROWAVE, StringForItems.MICROWAVE_DESC, new String[]{StringForItems.MICROWAVE, StringForItems.APPLIANCE}));
        itemMap.put(StringForItems.FRIDGE, new ItemFridge(StringForItems.FRIDGE, StringForItems.WHITE_REFRIGERATOR, new String[]{StringForItems.FRIDGE, StringForItems.REFRIGERATOR}));
        itemMap.put(StringForItems.FLASHLIGTH, new ItemFlashlight(StringForItems.FLASHLIGTH, StringForItems.FLASHLIGTH_MESSAGE, new String[]{StringForItems.FLASHLIGTH}));
        itemMap.put(StringForItems.TORCH, new ItemTorch(StringForItems.TORCH, StringForItems.METAL_TORCH, new String[]{StringForItems.TORCH, StringForItems.CANDLE}));
        itemMap.put(StringForItems.PIT, new ItemMagicBox(StringForItems.PIT, StringForItems.BOTTOMLESS_PIT, new String[]{StringForItems.PIT, StringForItems.HOLE}));
        itemMap.put(StringForItems.MACHINE, new ItemVendingMachine(StringForItems.MACHINE, StringForItems.VENDING_MACHINE_DESC, new String[]{StringForItems.MACHINE, StringForItems.VENDOR}));
        itemMap.put(StringForItems.SAFE, new ItemSafe(StringForItems.SAFE, StringForItems.SAFE_DESC, new String[]{StringForItems.SAFE}));
        itemMap.put(StringForItems.FOLDER, new ItemFolder(StringForItems.FOLDER, StringForItems.MANILLA_FOLDER, new String[]{StringForItems.FOLDER}));
        itemMap.put(StringForItems.DOCUMENT, new ItemDocument(StringForItems.DOCUMENT, StringForItems.SECRET_DOCUMENT, new String[]{StringForItems.DOCUMENT}));
        itemMap.put(StringForItems.FAN, new ItemLock(StringForItems.FAN, StringForItems.VENTILATION_FAN, new String[]{StringForItems.FAN}));
        itemMap.put(StringForItems.COMPUTER, new ItemComputer(StringForItems.COMPUTER, StringForItems.APPLE_COMPUTER, new String[]{StringForItems.APPLE, StringForItems.COMPUTER, StringForItems.KEYBOARD, StringForItems.IMAC}));
        itemMap.put(StringForItems.COFFEE, new ItemCoffee(StringForItems.COFFEE, StringForItems.COFFEE_STAMING, new String[]{StringForItems.COFFEE, StringForItems.BEVERAGE, StringForItems.MUG}));
        itemMap.put(StringForItems.LIGHT, new ItemDeskLight(StringForItems.LIGHT, StringForItems.DESK_LIGHT, new String[]{StringForItems.LIGHT}));
        itemMap.put(StringForItems.DYNAMITE, new ItemDynamite(StringForItems.DYNAMITE, StringForItems.BUNDLE_OF_DYNAMITE, new String[]{StringForItems.DYNAMITE, StringForItems.EXPLOSIVE, StringForItems.EXPLOSIVES}));
        itemMap.put(StringForItems.BUTTON, new ItemButton(StringForItems.BUTTON, StringForItems.ELEVATOR_BUTTON, new String[]{StringForItems.BUTTON_SMALL}));
        for(int i=1; i<5; i++) {
        	StringBuilder s  = new StringBuilder("Floor ").append(i).append(" Button"); 
        	StringBuilder sd =  new StringBuilder("Elevator Floor ").append(i).append(" Button");
        	itemMap.put(s.toString(), new ItemButton(s.toString(), sd.toString(), new String[]{Integer.toString(i)}));
        }
    	itemMapbyUser.put(userId, itemMap);
    	checkUniqueAliases(itemMap.values());
    }

    /**
     * Factory to create a designed item. All items must be instantiated using this method. Items are created by name
     * @param s the name of the item (or perhaps it's alias)
     * @return the newly instantiated item
     */
    public static Item getInstance(String itemName, String userId) {
    	if (itemMapbyUser == null) {
    		itemMapbyUser = new HashMap<>();
        	itemMap = new HashMap<>();
        }
        if(itemMapbyUser.get(userId)==null) {
        	makeItems(userId);
        }
        
        for (Item i : itemMapbyUser.get(userId).values()) {
            for (String a : i.getAliases()) {
                if (itemName.equals(a)) {
                    return i;
                }
            }
        }
        return null;
    }


    private static void isUniquePrintItem(Item item, Item i) {
        for (String string : item.getAliases()) {
            for (String s : i.getAliases()) {
                if (string == s) {
                	gameInterface.println("Warning: alias conflict between " + item + " and " + i);
                }
            }
        }
    }
    
    /**
     * Ensure that aliases are unique
     */
    private static void checkUniqueAliases(Collection<Item> itemList) {
    	for (Item item : itemList) {
            for (Item i : itemList) {
                if (item == i) {
                    continue;
                }
                isUniquePrintItem(item, i);
            }
        }
    }

    // Getter & setters
    public Item relatedItem() {
        return this.relatedItem;
    }
    public void setRelatedItem(Item i) {
        this.relatedItem = i;
    }

    public Room relatedRoom() {
        return this.relatedRoom;
    }
    public void setRelatedRoom(Room r) {
        this.relatedRoom = r;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String toString() {
        return this.description;
    }

    public String detailDescription() {
        return this.detailDescription;
    }
    public String description() {
        return this.description;
    }

    public void setDescription(String s) {
        this.description = s;
    }
    public void setDetailDescription(String s) {
        this.detailDescription = s;
    }

    /**
     * Control visibility
     */
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }

    // Inspectable
    public Boolean inspect() {
        if (this.inspectMessage != null) {
        	gameInterface.println(this.inspectMessage);
        } else {
        	gameInterface.println("It appears to be a " + this + ".");
        }
        return true;
    }

    public void setInspectMessage(String message) {
        this.inspectMessage = message;
    }

    @Override
    public int value() {
        return this.value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
    
    /**
     * The comparison is based on description
     * @param i
     * @return
     */
    @Override
    public int compareTo(Object i) {
        if (((Item) i).detailDescription.equals(this.detailDescription())) {
            return 0;
        } else {
            return 1;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof Item) {
    		Item item = (Item) obj;
    		if(item.description.equals(description) && item.detailDescription.equals(detailDescription)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return description.hashCode() + detailDescription.hashCode();
    }
}