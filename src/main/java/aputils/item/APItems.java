package aputils.item;

import aputils.APUtilsMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class APItems {

    public static CellItem CELL = new CellItem(new Item.Settings().maxCount(1));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(APUtilsMod.MODID, "cell"), CELL);
    }
}
