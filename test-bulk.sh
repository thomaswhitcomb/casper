#!/bin/sh 
HOST="localhost:50000"
for VARIABLE in {1..300}
do
  for VARIABLE in {1..10}
  do
	  # Good create no TTL (use default)
	  URL=`curl --silent --data-urlencode "secret=$VARIABLE"  http://${HOST}/create`
	  SECRET=`curl --silent -w "%{http_code}" $URL`
	  if [ "$SECRET" == "${VARIABLE}200" ];then
	          echo "Test - OK"
	  else
	          echo "Test - Failed"
	  fi
  done
  sleep 1  
done
