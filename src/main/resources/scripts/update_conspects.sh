#!/bin/bash -e

cd "repos/$1/$2"
#git pull
cd "../../../"

find "repos/$1/$2" -type f -name "*.tex"
