#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p target/verificacao-dominio
java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -d target/verificacao-dominio \
  src/main/java/br/com/educacao/gamificada/domain/*.java scripts/VerificacaoDominio.java
java -cp target/verificacao-dominio VerificacaoDominio
