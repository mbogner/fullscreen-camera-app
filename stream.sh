#!/usr/bin/env bash
scrcpy -s fa4ccbd2 \
  --video-bit-rate=40M --max-fps=60 --video-codec=h264 \
  --video-encoder='OMX.qcom.video.encoder.avc'