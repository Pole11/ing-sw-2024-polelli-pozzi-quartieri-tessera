#!/bin/bash

for file in *_*.jpg; do
    new_name="${file#*_}"
    mv "$file" "$new_name"
done

