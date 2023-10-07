#!/bin/bash -e

pdflatex -interaction=nonstopmode -output-directory tasks "$1.tex"
