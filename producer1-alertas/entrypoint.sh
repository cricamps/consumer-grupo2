#!/bin/sh
set -e

# El wallet original (con sqlnet.ora apuntando a una ruta de Windows)
# se monta de solo lectura en /wallet-source. Lo copiamos a una ruta
# interna escribible y corregimos la DIRECTORY del wallet para que
# apunte a esa ruta dentro del contenedor.
mkdir -p /app/wallet
cp -r /wallet-source/* /app/wallet/
sed -i 's#DIRECTORY="[^"]*"#DIRECTORY="/app/wallet"#' /app/wallet/sqlnet.ora

export TNS_ADMIN=/app/wallet
exec java -jar app.jar
