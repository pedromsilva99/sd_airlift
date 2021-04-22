
for i in $(seq 1 $1)
 do
 gnome-terminal --window-with-profile=Unnamed -e "/home/filipe/Desktop/4_ano/SD/git/sd_airlift/AirLift/script.sh $2 $i "> /dev/null 2>&1
 done

