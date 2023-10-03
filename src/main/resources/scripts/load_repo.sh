#!/bin/bash -e

mkdir -p "repos/$2"
mkdir "repos/$2/$3" # to check for failing
rm -r "repos/$2/$3" # only will be executed if it was just created
git clone "$1" "repos/$2/$3"
