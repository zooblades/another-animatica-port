#!/bin/bash
# Prints member signatures of classes used by the mod as workflow annotations, so the exact
# API of a Minecraft/Fabric version can be checked without a local game install.
CLASSES="${DUMP_CLASSES:-net.fabricmc.fabric.api.resource.v1.ResourceLoader net.minecraft.server.packs.resources.ResourceManagerReloadListener net.minecraft.server.packs.resources.PreparableReloadListener net.minecraft.server.packs.PackType net.minecraft.client.renderer.texture.TextureManager net.minecraft.client.renderer.texture.DynamicTexture com.mojang.blaze3d.platform.NativeImage net.minecraft.client.gui.screens.options.VideoSettingsScreen net.minecraft.resources.Identifier}"
python3 - $CLASSES <<'PY'
import sys, os, zipfile, subprocess, glob
want = {c: c.replace('.', '/') + '.class' for c in sys.argv[1:]}
roots = [os.path.expanduser('~/.gradle'), os.getcwd() + '/.gradle', '/home/runner/.gradle']
jars = set()
for r in roots:
    for p in glob.glob(r + '/**/*.jar', recursive=True):
        if 'sources' not in p: jars.add(p)
found = {}
for j in jars:
    try:
        names = set(zipfile.ZipFile(j).namelist())
    except Exception:
        continue
    for c, path in want.items():
        if c not in found and path in names:
            found[c] = j
def emit(title, text):
    text = text[:15000].replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
    print('::notice title=%s::%s' % (title, text))
for c in want:
    if c not in found:
        emit(c, 'class not found in %d jars' % len(jars)); continue
    out = subprocess.run(['javap', '-p', '-cp', found[c], c], capture_output=True, text=True).stdout
    emit(c, out)
PY
