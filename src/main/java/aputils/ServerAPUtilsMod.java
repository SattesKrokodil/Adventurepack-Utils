package aputils;

import aputils.util.ServerAdvancementChecker;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.text.LiteralText;

public class ServerAPUtilsMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        APUtilsMod.advancementChecker = new ServerAdvancementChecker();
        
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            sender.sendPacket(APUtilsMod.versionCheckPacketID, PacketByteBufs.empty());
        });
        
        ServerLoginNetworking.registerGlobalReceiver(APUtilsMod.versionCheckPacketID, (server, handler, understood, buf, synchronizer, responseSender) -> {
            String serverVersion = APUtilsConfig.getVersion();
            if (!understood) {
                handler.disconnect(new LiteralText("Your client didn't understand the version check packet\nPlease install a newer version of Adventure Pack to join\nExpected version: " + serverVersion));
                return;
            }
            
            String version = buf.readString(128);
            if (!version.equals(serverVersion)) {
                handler.disconnect(new LiteralText("Incorrect version of Adventure Pack!\nExpected: " + serverVersion +"\nGot: " + version));
            }
        });
    }
}
