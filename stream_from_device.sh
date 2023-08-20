#!/usr/bin/env bash
ffmpeg -i tcp://192.168.0.248:9000 -c:v copy -f h264 -bufsize 128k - | ffplay -
