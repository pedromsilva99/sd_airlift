
for i in $(seq 1 $1)
 do
 java -cp .:../../../genclass.jar main.AirLift $2 > log$2.txt
 echo "Cicle $i"
 done
rm log$2.txt
 $2
