#!/bin/sh
openssl crl -inform DER -in ACI-EL-ORG-TEST.crl -out ACI-EL-ORG-TEST.pem
openssl crl -inform DER -in ACR-EL-TEST.crl -out ACR-EL-TEST.pem
cat ACI-EL-ORG-TEST.pem ACR-EL-TEST.pem > ca-bundle-tomws.crl