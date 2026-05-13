#!/bin/sh
cd "$(dirname "$0")" || exit 1
if command -v mvn >/dev/null 2>&1; then
  exec mvn spring-boot:run
fi
echo "Maven is not installed. On Kali/Debian run:"
echo "  sudo apt update && sudo apt install -y maven"
echo "Then from this folder:"
echo "  mvn spring-boot:run"
echo "Open http://localhost:8080/"
exit 1
