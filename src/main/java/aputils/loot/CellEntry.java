package aputils.loot;

import aputils.cell.Cell;
import aputils.cell.CellType;
import aputils.item.CellItem;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CellEntry extends LeafEntry {

    private final Optional<Rarity> rarity;
    private final Optional<CellType> cellType;

    private CellEntry(Optional<Rarity> rarity, Optional<CellType> cellType, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.rarity = rarity;
        this.cellType = cellType;
    }

    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.ITEM;
    }

    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        Stream<Cell> registeredCells = Cell.REGISTRY.stream();
        if(rarity.isPresent()) {
            registeredCells = registeredCells.filter(cell -> cell.getRarity() == rarity.get());
        }
        if(cellType.isPresent()) {
            registeredCells = registeredCells.filter(cell -> cell.getCellType() == cellType.get());
        }
        Object[] cells = registeredCells.toArray();
        Cell cell = (Cell)cells[context.getRandom().nextInt(cells.length)];
        lootConsumer.accept(CellItem.create(cell));
    }

    public static LeafEntry.Builder<?> builder() {
        return builder((weight, quality, conditions, functions) -> {
            return new CellEntry(Optional.empty(), Optional.empty(), weight, quality, conditions, functions);
        });
    }

    public static LeafEntry.Builder<?> builder(CellType cellType) {
        return builder((weight, quality, conditions, functions) -> {
            return new CellEntry(Optional.empty(), Optional.of(cellType), weight, quality, conditions, functions);
        });
    }

    public static LeafEntry.Builder<?> builder(Rarity rarity) {
        return builder((weight, quality, conditions, functions) -> {
            return new CellEntry(Optional.of(rarity), Optional.empty(), weight, quality, conditions, functions);
        });
    }

    public static LeafEntry.Builder<?> builder(Rarity rarity, CellType cellType) {
        return builder((weight, quality, conditions, functions) -> {
            return new CellEntry(Optional.of(rarity), Optional.of(cellType), weight, quality, conditions, functions);
        });
    }

    public static class Serializer extends LeafEntry.Serializer<CellEntry> {
        public void addEntryFields(JsonObject jsonObject, CellEntry cellEntry, JsonSerializationContext jsonSerializationContext) {
            super.addEntryFields(jsonObject, cellEntry, jsonSerializationContext);
            cellEntry.rarity.ifPresent(value -> jsonObject.addProperty("rarity", value.name().toLowerCase(Locale.ROOT)));
            cellEntry.cellType.ifPresent(type -> jsonObject.addProperty("cell_type", type.name().toLowerCase(Locale.ROOT)));
        }

        protected CellEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            Optional<Rarity> rarity;
            if(jsonObject.has("rarity")) {
                rarity = Optional.of(Rarity.valueOf(JsonHelper.getString(jsonObject, "rarity").toUpperCase(Locale.ROOT)));
            } else {
                rarity = Optional.empty();
            }
            Optional<CellType> cellType;
            if(jsonObject.has("cell_type")) {
                cellType = Optional.of(CellType.valueOf(JsonHelper.getString(jsonObject, "cell_type").toUpperCase(Locale.ROOT)));
            } else {
                cellType = Optional.empty();
            }
            return new CellEntry(rarity, cellType, i, j, lootConditions, lootFunctions);
        }
    }
}
