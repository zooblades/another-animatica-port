#!/bin/bash
# Prints member signatures of Minecraft classes used by the mod as workflow annotations,
# so the exact 1.21.11 API (Mojang names) can be checked without a local game install.
JAR=""
CANDS=$(find / -name '*.jar' -size +8M 2>/dev/null | grep -viE 'sources|/jdk|/java-|gradle-[0-9]|/wrapper/dists|/hostedtoolcache/' )
for j in $CANDS; do
  if javap -cp "$j" net.minecraft.client.renderer.texture.TextureManager >/dev/null 2>&1; then JAR="$j"; break; fi
done
echo "candidates: $(echo "$CANDS" | head -20 | tr '\n' ' ')"
echo "jar: $JAR"
[ -z "$JAR" ] && { echo "::warning title=api dump::no minecraft jar found among: $(echo $CANDS | head -c 1500)"; exit 0; }
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
