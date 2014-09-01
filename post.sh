echo $1
curl -v --data-urlencode "secret=$1" http://localhost:8080/create
