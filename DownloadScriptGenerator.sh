#!/bin/bash

java \
  -ea \
  -cp bin/main:$(xmlstarlet sel -t -v '/classpath/classpathentry/@path' .classpath | sed -e ':a' -e 'N' -e '$!ba' -e 's/\n/:/g') \
  -Durl=$1 \
  eu.fays.rockbox.dl.DownloadScriptGenerator
