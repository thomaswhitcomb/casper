#!/bin/sh

URL=`curl --silent --data-urlencode "secret=$1" http://localhost:8080/create`
SECRET=`curl $URL`


if [ "$SECRET" == "$1" ];then
	echo "Test 1 - OK"
else
	echo "Test 1 - Failed"
fi

HTTPCODE=`curl --silent -w "%{http_code}" -o /dev/null  $URL`
if [ "$HTTPCODE" == "404" ];then
	echo "Test 2 - OK"
else
	echo "Test 2 - Failed"
fi
