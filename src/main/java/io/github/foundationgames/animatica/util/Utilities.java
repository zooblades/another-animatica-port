package io.github.foundationgames.animatica.util;

import net.minecraft.resources.ResourceLocation;

public enum Utilities {;
    public static ResourceLocation processPath(ResourceLocation fileRelativeTo, ResourceLocation path) {
        if (path.getPath().startsWith("./")) {
            int lInd = fileRelativeTo.getPath().lastIndexOf("/");
            if (lInd > 0) {
                var builder = new StringBuilder(fileRelativeTo.getPath());
                builder.replace(lInd, builder.length(), path.getPath().replaceFirst("\\./", "/"));
                return ResourceLocation.fromNamespaceAndPath(fileRelativeTo.getNamespace(), builder.toString());
            }
        } else if (path.getPath().startsWith("~/")) {
            return ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath().replaceFirst("~/", "optifine/"));
        }
        return path;
    }
}
