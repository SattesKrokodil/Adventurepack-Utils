package aputils.cell;

import aputils.APUtilsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CellLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {


    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public CellLoader() {
        super(GSON, "cells");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        Cell.REGISTRY.clear();
        loader.forEach((id, je) -> {
            try {
                Cell cell = Cell.fromJson(je.getAsJsonObject());
                Cell.REGISTRY.register(id, cell);
            } catch(Exception e) {
                APUtilsMod.LOGGER.error("Couldn't read cell \"" + id.toString() + "\" (skipping): " + e.getMessage());
            }
        });
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(APUtilsMod.MODID, "cells");
    }
}
