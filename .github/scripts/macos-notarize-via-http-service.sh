#!/usr/bin/env bash

DMG=$1
NOTARIZE_SERVICE_URL=$2

echo "Notarize DMG"
echo ${DMG}

RESPONSE=\
$(curl -s -X POST \
  -F file=@${DMG} \
  -F 'options={"primaryBundleId": "SpringTools4", "staple": true};type=application/json' \
  ${NOTARIZE_SERVICE_URL}/macos-notarization-service/notarize)
	  
echo "Notarization request submitted"
echo ${RESPONSE}
  
UUID=$(echo ${RESPONSE} | jq -r '.uuid')

STATUS=$(echo ${RESPONSE} | jq -r '.notarizationStatus.status')

echo "Status after submitting"
echo ${STATUS}

while [[ ${STATUS} == 'IN_PROGRESS' ]]; do
  sleep 60
  RESPONSE=$(curl -s ${NOTARIZE_SERVICE_URL}/macos-notarization-service/${UUID}/status)
  STATUS=$(echo ${RESPONSE} | jq -r '.notarizationStatus.status')

  echo "Waiting for status to change"
  echo ${RESPONSE}
  echo ${STATUS}
done

echo "Status is not in progress anymore"
echo ${RESPONSE}
echo ${STATUS}

if [[ ${STATUS} != 'COMPLETE' ]]; then
  echo "Notarization failed: ${RESPONSE}"
  exit 1
fi

mv "${DMG}" "${DMG}-unnotarized"

curl -o ${DMG} -J ${NOTARIZE_SERVICE_URL}/macos-notarization-service/${UUID}/download
