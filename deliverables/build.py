#!/bin/python

import subprocess
import argparse
import re

def translate():
    # Get the content from in_file_path
    with open(in_file_path, 'r') as file:
        content = file.read()

    # Set the default size for every image to 400px
    pattern_immagine = r"!\[(.*?)\]\((.*?.png)\)"
    modified_content = re.sub(pattern_immagine, r"![\1](\2){ width=400px }", content)

    # Put a new chapter on a new page
    modified_content = modified_content.replace('\n# ', '\n\\newpage\n# ')

    with open(out_file_path, 'w') as file:
        # Write the frontmatter
        file.write(frontmatter_content)
        # Write the file contents
        file.write(modified_content)

    print("Translation completed")

def compile():
    # Compile to pdf using pandoc
    subprocess.run(pandoc_comand, shell=True, capture_output=True, text=True)

    print("PDF generated " + out_pdf_file_path)

# Create a parser to define flags
parser = argparse.ArgumentParser(description='Translate and/or compile the source file')

# Define flags
parser.add_argument('-t', action='store_true', help='Uglify the source file to prepare the file for pandoc')
parser.add_argument('-c', action='store_true', help='Compile with pandoc')
parser.add_argument('-i', help='Input plain markdown file')
parser.add_argument('-o', help='Output pdf file')
parser.add_argument('-d', help='Title of the document')
parser.add_argument('-s', help='Subtitle of the document')

args = parser.parse_args()

in_file_path = args.i
out_file_path = args.i.replace('.md', '.tmp.md')
out_pdf_file_path = args.o
pandoc_comand = "pandoc -f markdown-implicit_figures -i " + out_file_path + " -o " + out_pdf_file_path

# Define global variables
frontmatter_content = '''---
title: {title}
sub-title: {sub_title}
toc: true
toc-title: Indice
author:
- Riccardo Polelli
- Filippo Pozzi
- Federico Quartieri
- Giacomo Tessera
number-sections: true
geometry:
- top=20mm
- left=20mm
---\n\n\\newpage\n'''.format(title=args.d, sub_title=args.s)

if (args.t):
    translate()
elif (args.c):
    compile()
else:
    print("Usage:\n\t-t translate\n\t-c compile")