#!/bin/sh
HOST="localhost:50000"
#HOST="vast-bayou-9238.herokuapp.com"

# Create missing secret form field from FORM
URL=`curl --silent --data-urlencode "sxcret=$1" --data-urlencode "ttl=10" -w "%{http_code}" http://${HOST}/`
if [ "$URL" == "Missing Secret400" ];then
	echo "Test 0 - OK"
else
	echo "Test 0 - Failed"
fi

# Create with 0 TTL which causes retrieval timeout from FORM
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=0"  http://${HOST}/`
sleep 1
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "TTL expired410" ];then
	echo "Test 1 - OK"
else
	echo "Test 1 - Failed"
fi

# Create with bad TTL from FORM
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=x" -w "%{http_code}"  http://${HOST}/`
if [ "$URL" == "TTL must be integer400" ];then
	echo "Test 2 - OK"
else
	echo "Test 2 - Failed"
fi

# Create with TTL - happy path from FORM
URL=`curl --silent --data-urlencode "secret=$1" --data-urlencode "ttl=5"   http://${HOST}/`
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

# Good create no TTL (use default) from FORM
URL=`curl --silent --data-urlencode "secret=$1"  http://${HOST}/`
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

# Create secret with ttl from API
URL=`curl --silent --data-urlencode "json={\"secret\": \"$1\",\"ttl\":100}"  http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 7 - OK"
else
	echo "Test 7 - Failed"
fi
# Create secret with no ttl from API
URL=`curl --silent --data-urlencode "json={\"secret\": \"$1\"}"  http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 8 - OK"
else
	echo "Test 8 - Failed"
fi
# Create secret with ttl and no secret from API
URL=`curl --silent --data-urlencode "json={\"ttl\": 10}"  http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 9 - OK"
else
	echo "Test 9 - Failed"
fi
# Create secret with no ttl and no secret from API
URL=`curl --silent --data-urlencode "json={}"  http://${HOST}/create`
SECRET=`curl --silent -w "%{http_code}" $URL`
if [ "$SECRET" == "${1}200" ];then
	echo "Test 10 - OK"
else
	echo "Test 10 - Failed"
fi
# Create secret with no JSON
URL=`curl --silent --data-urlencode "n=5" -w "%{http_code}"  http://${HOST}/create`
if [ "$URL" == "500" ];then
	echo "Test 11 - OK"
else
	echo "Test 11 - Failed"
fi
