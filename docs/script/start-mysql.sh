#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

docker rm -f -v volunteer-mysql >/dev/null 2>&1 || true

docker run -d \
  --name volunteer-mysql \
  --restart unless-stopped \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=volunteer_management \
  -p 3306:3306 \
  -v "$ROOT_DIR/backend/src/main/resources/sql:/docker-entrypoint-initdb.d:ro" \
  mysql:8.4
