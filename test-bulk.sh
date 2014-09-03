#!/bin/sh 
for VARIABLE in {1..300}
do
  for VARIABLE in {1..10}
  do
	./test.sh $VARIABLE &
  done
  sleep 1  
done
