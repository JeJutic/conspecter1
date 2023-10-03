#!/bin/bash -e

cd "repos/$1/$2" && git pull
find "repos/$1/$2" -type f -name "*.tex"
