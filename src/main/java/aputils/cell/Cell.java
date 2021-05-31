package aputils.cell;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class Cell {

    public static Registry<Cell> REGISTRY = FabricRegistryBuilder.createSimple(Cell.class, new Identifier("aputils:cell")).buildAndRegister();

    private CellType cellType;
    private Rarity rarity;
    private Identifier advancement;
    private int minLevel;
    private String translationKey;

    public Cell(CellType cellType, Rarity rarity, Identifier advancement, int minLevel) {
        this.cellType = cellType;
        this.rarity = rarity;
        this.advancement = advancement;
        this.minLevel = minLevel;
    }

    public CellType getCellType() {
        return cellType;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Identifier getAdvancement() {
        return advancement;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public String getTranslationKey() {
        if(translationKey == null) {
            translationKey = Util.createTranslationKey("cell", REGISTRY.getId(this));
        }
        return translationKey;
    }

    public static Cell fromJson(JsonObject jsonObject) {
        String cellTypeString = JsonHelper.getString(jsonObject, "type");
        String advancement = JsonHelper.getString(jsonObject, "advancement");
        String rarity = JsonHelper.getString(jsonObject, "rarity");
        int minLevel = JsonHelper.getInt(jsonObject, "min_level");
        return new Cell(CellType.valueOf(cellTypeString.toUpperCase(Locale.ROOT)), Rarity.valueOf(rarity.toUpperCase(Locale.ROOT)), new Identifier(advancement), minLevel);
    }
}
