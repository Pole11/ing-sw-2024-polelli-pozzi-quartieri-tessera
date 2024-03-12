#!/bin/bash

pandoc ./$1.md -o ./$1.pdf -f markdown-implicit_figures
