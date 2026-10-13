package io.github.foundationgames.animatica.util.exception;

import net.minecraft.resources.ResourceLocation;

public class InvalidPropertyException extends PropertyParseException {
    public InvalidPropertyException(ResourceLocation file, String key, String expectedType) {
        super(String.format("Property '%s' in file '%s' expected to be of type: %s", key, file, expectedType));
    }
}
