#! /bin/bash

INFILE=$1
SUM=0
TOTALSUM=0

while read -r LINE
do
    RESULT=$(echo $LINE | gawk '/passed/ {print $2}')
    PASSED=${RESULT%/*}
    TOTAL=${RESULT#*/}
    #printf '%s\n' $PASSED
    SUM=$((SUM+PASSED))
    TOTALSUM=$((TOTALSUM+TOTAL))
done < $INFILE

printf 'Number passed: %d out of %d\n' $SUM $TOTALSUM