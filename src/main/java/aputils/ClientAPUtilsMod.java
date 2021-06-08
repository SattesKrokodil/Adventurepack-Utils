package aputils;

import aputils.cell.CellType;
import aputils.item.APItems;
import aputils.util.ClientAdvancementChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ClientAPUtilsMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        APUtilsMod.advancementChecker = new ClientAdvancementChecker();
        for(CellType cellType : CellType.values()) {
            String cellTypeName = cellType.name().toLowerCase();
            FabricModelPredicateProviderRegistry.register(APItems.CELL, new Identifier(cellTypeName), (itemStack, clientWorld, livingEntity) ->
                itemStack.hasTag() && itemStack.getOrCreateSubTag("CellData").contains("Type") && itemStack.getOrCreateSubTag("CellData").getString("Type").equals(cellTypeName)
                ? 1.0f : 0.0f);
        }
    
        ClientLoginNetworking.registerGlobalReceiver(APUtilsMod.versionCheckPacketID, (client, handler, buf, listenerAdder) -> {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeString(APUtilsConfig.getVersion());
            return CompletableFuture.completedFuture(buffer);
        });
    }
}
