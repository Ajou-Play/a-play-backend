#!/bin/zsh
git clone https://github.com/Ajou-Play/oci-secrets
mkdir -p ~/ocikey
mv oci-secrets/* src/main/resources
cat src/main/resources/oci_config
rm -rf oci-secrets
