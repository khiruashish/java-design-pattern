#!/usr/bin/env bash
# Compiles every module, runs each *Demo class and saves its console output to
# outputs/<module>/<package>/<Demo>.txt. The docs embed these files, so every
# "Output" block on the site is the real output of the code shown.
#
#   ./run-demos.sh           regenerate outputs
#   ./run-demos.sh --check   fail if any output differs from the committed files
set -euo pipefail
cd "$(dirname "$0")"

mvn -q -B -DskipTests compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -Dmdep.includeScope=runtime \
  2>&1 | { grep -v '^Picked up JAVA_TOOL_OPTIONS' || true; }

out_dir=outputs
[[ "${1:-}" == "--check" ]] && out_dir=$(mktemp -d)

count=0
for classes in */target/classes; do
  module=${classes%%/*}
  cp="$classes"
  [[ -s "$module/target/classpath.txt" ]] && cp="$classes:$(cat "$module/target/classpath.txt")"
  while IFS= read -r file; do
    rel=${file#"$classes"/}
    fqcn=$(echo "${rel%.class}" | tr '/' '.')
    pkg_path=$(dirname "${rel#com/handbook/$module/}")
    target="$out_dir/$module/$pkg_path/$(basename "${rel%.class}").txt"
    mkdir -p "$(dirname "$target")"
    java -Dstdout.encoding=UTF-8 -Duser.language=en -Duser.country=US -cp "$cp" "$fqcn" > "$target" 2> >(grep -v '^Picked up JAVA_TOOL_OPTIONS' >&2)
    count=$((count + 1))
  done < <(find "$classes" -name '*Demo.class' ! -name '*$*' | sort)
done

if [[ "${1:-}" == "--check" ]]; then
  diff -ru outputs "$out_dir" && echo "All $count demo outputs match." 
else
  echo "Wrote $count demo outputs to $out_dir/"
fi
