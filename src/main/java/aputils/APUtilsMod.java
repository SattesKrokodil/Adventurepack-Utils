package aputils;

import aputils.command.CellCommand;
import aputils.cell.CellLoader;
import aputils.item.APItems;
import aputils.loot.CellEntry;
import aputils.util.AdvancementChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.loot.table.LootEntryTypeRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APUtilsMod implements ModInitializer {

	public static final String MODID = "aputils";
	public static final Logger LOGGER = LogManager.getLogger(APUtilsMod.class);
	public static boolean isExileLoaded = false;

	public static AdvancementChecker advancementChecker;

	@Override
	public void onInitialize() {
		APItems.register();
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			CellCommand.register(dispatcher);
		}));
		if(FabricLoader.getInstance().isModLoaded("mmorpg")) {
			isExileLoaded = true;
		}

		LootEntryTypeRegistryImpl.INSTANCE.register(new Identifier(MODID, "cell"), new CellEntry.Serializer());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CellLoader());
	}
}