#!/bin/sh
set -e

mkdir -p /app/wallet
cp -r /wallet-source/* /app/wallet/
sed -i 's#DIRECTORY="[^"]*"#DIRECTORY="/app/wallet"#' /app/wallet/sqlnet.ora

export TNS_ADMIN=/app/wallet
exec java -jar app.jar
