#! /bin/bash

dir=$1
#Iterate the loop until a less than 10
a=0
while [ $a -lt 10 ]
do
    filepath_in=$dir/$a-in.json
    filepath_out=$dir/$a-out.json
	if test -f $filepath_in; then
        ./xbaddies < $filepath_in | python helperScripts/jsonEquals - $filepath_out
    fi
	# increment the value
	a=`expr $a + 1`
done

#Ex usage:
# ./tester2.sh 9/Tests/
# SAME JSON
# SAME JSON
# ...