#!/bin/bash
set -e
workdir=`pwd`

#Uncomments the below to publish all vsix files in the task inputs:
#vsix_files=`ls ${workdir}/s3-*/*.vsix`

#Uncomment the below to publish only concourse vxix
#vsix_files=`ls ${workdir}/s3-*/vscode-concourse-*.vsix`

#Uncomment the below to publish all vsix files
vsix_files=`ls ${workdir}/s3-*/vscode-*.vsix`

for vsix_file in $vsix_files
do
    echo "****************************************************************"
    echo "*** Publishing : ${vsix_file}"
    echo "****************************************************************"
    echo ""
    echo "We are runing the following command:"
    echo ""
    echo "     vsce publish -p vsce_token --packagePath $vsix_file"
    echo ""
    vsce publish -p $vsce_token --packagePath $vsix_file
done
