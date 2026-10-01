#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac -encoding UTF-8 -Xlint:all -d build assignment2/*.java
java -cp build assignment2.MasterMindRulesTest
java -cp build assignment2.TestHarness
java -cp build assignment2.WordleRulesTest
