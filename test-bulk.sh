#!/bin/sh 
HOST="casper-dev.elasticbeanstalk.com"
#HOST="localhost:50000"

request (){
	  # Good create no TTL (use default)
	  URL=`curl --silent --data-urlencode "json={\"secret\": \"$1\",\"ttl\":100}"  http://${HOST}/create`
	  SECRET=`curl --silent -w "%{http_code}" $URL`
	  if [ "$SECRET" == "${1}200" ];then
	          echo "Test - OK"
	  else
	          echo "Test - Failed"
	  fi
}	  

for VARIABLE1 in {1..300}
do
  for VARIABLE2 in {1..5}
  do
	  request  $VARIABLE2 &
  done
  sleep 1  
done
