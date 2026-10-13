#!/bin/bash
# Prints member signatures of Minecraft classes used by the mod as workflow annotations,
# so the exact 1.21.11 API (Mojang names) can be checked without a local game install.
JAR=$(find / -name '*.jar' \( -path '*loom-cache*' -o -path '*fabric-loom*' \) 2>/dev/null | grep -iE 'minecraft' | grep -viE 'sources|loader|fabric-api' | xargs -r ls -S 2>/dev/null | head -1)
echo "jar: $JAR"
[ -z "$JAR" ] && { echo "::warning title=api dump::no minecraft jar found"; exit 0; }
emit() { # title, text
  python3 - "$1" <<'PY'
import sys
t=sys.stdin.read() if False else open('/tmp/dump.txt',errors='replace').read()
t=t[:15000].replace('%','%25').replace('\r','%0D').replace('\n','%0A')
print('::notice title=%s::%s' % (sys.argv[1], t))
PY
}
for cls in net.minecraft.client.renderer.texture.TextureManager \
           net.minecraft.client.renderer.texture.DynamicTexture \
           net.minecraft.client.renderer.texture.AbstractTexture \
           com.mojang.blaze3d.platform.NativeImage \
           net.minecraft.resources.Identifier \
           net.minecraft.client.gui.screens.options.VideoSettingsScreen \
           net.minecraft.client.gui.components.OptionsList \
           net.minecraft.client.OptionInstance \
           com.mojang.blaze3d.systems.RenderSystem; do
  javap -p -cp "$JAR" "$cls" 2>&1 | grep -vE '^Compiled from' > /tmp/dump.txt
  emit "$cls" 
done
javap -p -c -cp "$JAR" net.minecraft.client.gui.screens.options.VideoSettingsScreen 2>&1 | grep -E 'invoke|^  [a-z].*\(' > /tmp/dump.txt
emit "VideoSettingsScreen bytecode calls"
javap -p -c -cp "$JAR" net.minecraft.resources.Identifier 2>&1 | grep -E 'validPathChar|isValidPath|assert|^  [a-z].*\(' | head -60 > /tmp/dump.txt
emit "Identifier validation"
