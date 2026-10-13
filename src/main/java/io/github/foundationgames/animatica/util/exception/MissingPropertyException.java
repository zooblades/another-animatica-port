package io.github.foundationgames.animatica.util.exception;

import net.minecraft.resources.ResourceLocation;

public class MissingPropertyException extends PropertyParseException {
    public MissingPropertyException(ResourceLocation file, String key) {
        super(String.format("Expected property '%s' in file '%s'", key, file));
    }
}
