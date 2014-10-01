#!/bin/sh 
HOST="casper-dev.elasticbeanstalk.com"
#HOST="localhost:50000"
for VARIABLE1 in {1..300}
do
  for VARIABLE2 in {1..10}
  do
	  # Good create no TTL (use default)
	  URL=`curl --silent --data-urlencode "json={\"secret\": \"$VARIABLE2\",\"ttl\":100}"  http://${HOST}/create`
	  SECRET=`curl --silent -w "%{http_code}" $URL`
	  if [ "$SECRET" == "${VARIABLE2}200" ];then
	          echo "Test - OK"
	  else
	          echo "Test - Failed"
	  fi
  done
  sleep 1  
done
