#!/bin/sh
HOST="localhost:50000"
#HOST="vast-bayou-9238.herokuapp.com"

URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=0"  http://${HOST}/create`
echo $URL
sleep 5
SECRET=`curl --silent $URL`
echo $SECRET
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
