#!/bin/bash

origen="/home/joel/Escritorio/Proyectos/Pyronix/Nature's Betrayal/neoforge/build/libs"
destino="/mnt/PyronixStudio/docker/stacks/minecraft/hazardcraft-7/data/mods"

# Borra los .jar que comiencen con ProtectionStone- en el destino
rm -f "$destino/natures_betrayal-neoforge-"*.jar

# Copia los .jar desde origen al destino
cp "$origen/natures_betrayal-neoforge-"*.jar "$destino/"

echo "Copia completada."
