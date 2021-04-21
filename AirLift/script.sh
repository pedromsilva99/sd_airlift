
for i in $(seq 1 $1)
 do
 java -cp .:../../../genclass.jar main.AirLift > log.txt
 echo "Cicle $i"
 done

