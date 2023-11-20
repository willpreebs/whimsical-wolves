#! /bin/bash
./test < $1/$2-in.json | python helperScripts/jsonEquals - $1/$2-out.json

# Ex: ./tester.sh 6/grade/4 0
# SAME JSON 