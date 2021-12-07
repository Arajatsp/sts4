#!/bin/bash
set -e
./scripts/preinstall.sh
npm install
npm run vsce-package
rm -fr ~/.vscode/extensions/pivotal.vscode-concourse*
rm -fr ~/.vscode/extensions/.obsolete
code --install-extension vscode-concourse-*.vsix
