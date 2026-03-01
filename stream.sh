#!/usr/bin/env bash
# pixel 7a - h265 hardware encoding
scrcpy -s 34051JEHN13506 \
 --video-codec=h265 \
 --video-encoder=c2.exynos.hevc.encoder \
 --video-bit-rate=20M \
 --max-fps=60 \
 --no-audio \
 --video-buffer=50