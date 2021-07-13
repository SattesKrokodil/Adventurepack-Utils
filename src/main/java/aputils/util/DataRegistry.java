package aputils.util;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.stream.Stream;

public class DataRegistry<T> {

    private final HashMap<Identifier, T> idToElem = new HashMap<>();
    private final HashMap<T, Identifier> elemToId = new HashMap<>();

    public void register(Identifier id, T elem) {
        if(idToElem.containsKey(id)) {
            throw new IllegalArgumentException("Attempted to register ID " + id + ", which was already contained!");
        }
        idToElem.put(id, elem);
        elemToId.put(elem, id);
    }

    public Identifier getId(T elem) {
        if(!elemToId.containsKey(elem)) {
            throw new IllegalArgumentException("Element is not contained in registry!");
        }
        return elemToId.get(elem);
    }

    public T get(Identifier id) {
        if(!idToElem.containsKey(id)) {
            throw new IllegalArgumentException("Identifier is not contained in registry: " + id);
        }
        return idToElem.get(id);
    }

    public void clear() {
        idToElem.clear();
        elemToId.clear();
    }

    public Stream<Identifier> getIds() {
        return elemToId.values().stream();
    }

    public Stream<T> stream() {
        return idToElem.values().stream();
    }
}
