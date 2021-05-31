package aputils;

import aputils.util.ServerAdvancementChecker;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ServerAPUtilsMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        APUtilsMod.advancementChecker = new ServerAdvancementChecker();
    }
}
