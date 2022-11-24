#!/bin/zsh
git clone https://github.com/Ajou-Play/oci-secrets
mkdir -p ~/ocikey
mv oci-secrets/* ~/ocikey/
cat ~/ocikey/config
rm -rf oci-secrets