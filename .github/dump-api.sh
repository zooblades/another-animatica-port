#!/bin/bash
# Prints member signatures of classes used by the mod as workflow annotations, so the exact
# API of a Minecraft/Fabric version can be checked without a local game install.
CANDS=$(find / -name '*.jar' 2>/dev/null | grep -viE 'sources|/jdk|/java-|gradle-[0-9]|/wrapper/dists|/hostedtoolcache/|/usr/lib/|/usr/share/' )
jar_for() { for j in $CANDS; do javap -cp "$j" "$1" >/dev/null 2>&1 && { echo "$j"; return; }; done; }
emit() { python3 - "$1" <<'PY'
import sys
t=open('/tmp/dump.txt',errors='replace').read()[:15000].replace('%','%25').replace('\r','%0D').replace('\n','%0A')
print('::notice title=%s::%s' % (sys.argv[1], t))
PY
}
for cls in ${DUMP_CLASSES:-net.fabricmc.fabric.api.resource.v1.ResourceLoader net.minecraft.server.packs.resources.ResourceManagerReloadListener net.minecraft.server.packs.resources.PreparableReloadListener net.minecraft.server.packs.PackType net.minecraft.client.renderer.texture.TextureManager net.minecraft.client.renderer.texture.DynamicTexture com.mojang.blaze3d.platform.NativeImage net.minecraft.client.gui.screens.options.VideoSettingsScreen net.minecraft.resources.Identifier}; do
  J=$(jar_for "$cls")
  if [ -z "$J" ]; then echo "class not found anywhere: $cls" > /tmp/dump.txt; else javap -p -cp "$J" "$cls" 2>&1 | grep -vE '^Compiled from' > /tmp/dump.txt; fi
  emit "$cls"
done
