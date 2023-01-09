#!/usr/bin/env bash


echo 'Build...'

gradle clean build

echo 'Copy files to server...'

scp -i ~/.ssh/id_rsa \
    build/libs/psychology-0.0.1-snapshot.jar \
    mmm@82.146.32.182:/home/mmm/code/

echo 'Restart application'

ssh -i ~/.ssh/id_rsa mmm@82.146.32.182 << EOF
    pgrep java | xargs kill -9
    rm log.txt
    java -jar ./code/psychology-0.0.1-snapshot.jar > log.txt &
EOF

echo 'Bye'