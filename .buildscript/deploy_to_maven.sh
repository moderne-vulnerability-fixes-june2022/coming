#!/bin/bash
#
# Deploy a jar, source jar, and javadoc jar to Sonatype Maven Central repo.
#
# Inspired from https://github.com/square/retrofit/blob/fccedbeb4d5181c926fff450cdf5c5116ef0eeaa/.buildscript/deploy_snapshot.sh

SLUG="SpoonLabs/coming"
JDK="oraclejdk8"
BRANCH="master"

echo "Run deploy_to_maven"

set -e

echo "Deploying ..."
# made with "travis encrypt-file codesigning.asc -r SpoonLabs/coming --add"
openssl aes-256-cbc -K $encrypted_a263e63e6aa6_key -iv $encrypted_a263e63e6aa6_iv -in  ./.buildscript/codesigning.asc.enc -out codesigning.asc -d
echo "Before gpg"
gpg2 --fast-import codesigning.asc
echo "After gpg"

# getting the previous version on Maven Central
#  PREVIOUS_MAVEN_CENTRAL_VERSION=`curl "http://search.maven.org/solrsearch/select?q=a:gumtree-spoon-ast-diff+g:fr.inria.gforge.spoon.labs&rows=20&wt=json" | jq -r .response.docs[0].latestVersion | egrep -o "[0-9]+$"`
PREVIOUS_MAVEN_CENTRAL_VERSION=0

# and incrementing it
mvn versions:set -DnewVersion=1.$((PREVIOUS_MAVEN_CENTRAL_VERSION+1))

echo "do we still have more than one secret key?"
# gpg2 --batch --yes --delete-secret-and-public-key mpapis@gmail.com
gpg2 --list-keys

echo "Starting deployment using maven deploy ..."
mvn -Prelease deploy --settings .buildscript/settings.xml -Dmaven.test.skip=true -Dpgp.skip-true -Dgpg.passphrase=$PASSPHRASE #$GPG_PASSPHRASE
echo "Well deployed!"
