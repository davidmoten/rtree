#!/bin/bash
set -e
mvn site
cd ../davidmoten.github.io
git pull
mkdir -p rtree
cp -r ../rtree/target/site/* rtree/
git add .
git commit -am "update site reports"
git push
