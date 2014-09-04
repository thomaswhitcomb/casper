#!/bin/sh
HOST="localhost:50000"
#HOST="vast-bayou-9238.herokuapp.com"

# Create missing secret form field
URL=`curl --silent --data-urlencode "sxcret=$1" --data-urlencode "ttl=0" -w "%{http_code}"  http://${HOST}/create`
if [ "$URL" == "Missing secret form field400" ];then
	echo "Test 0 - OK"
else
	echo "Test 0 - Failed"
fi

# Create with 0 TTL which causes retrieval timeout
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=0"  http://${HOST}/create`
sleep 1
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "TTL expired410" ];then
	echo "Test 1 - OK"
else
	echo "Test 1 - Failed"
fi

# Create with bad TTL
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=x" -w "%{http_code}"  http://${HOST}/create`
if [ "$URL" == "TTL must be integer400" ];then
	echo "Test 2 - OK"
else
	echo "Test 2 - Failed"
fi

# Create with TTL - happy path
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=5"   http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 3 - OK"
else
	echo "Test 3 - Failed"
fi

# Double retrieval of a good URL
HTTPCODE=`curl --silent -w "%{http_code}" -o /dev/null  $URL`
if [ "$HTTPCODE" == "404" ];then
	echo "Test 4 - OK"
else
	echo "Test 4 - Failed"
fi

# Good create no TTL (use default)
URL=`curl --silent --data-urlencode "secret=$1"  http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 5 - OK"
else
	echo "Test 5 - Failed"
fi

# Double retrieval of a good URL
HTTPCODE=`curl --silent -w "%{http_code}" -o /dev/null  $URL`
if [ "$HTTPCODE" == "404" ];then
	echo "Test 6 - OK"
else
	echo "Test 6 - Failed"
fi

