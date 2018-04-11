docker save -o ubuntu:latest.tar ubuntu:latest
docker save ubuntu:latest > ubuntu:latest.tar

docker load -i ubuntu:latest.tar
docker load < ubuntu:latest.tar

docker import ubuntu:latest_new ubuntu:latest.tar

docker export -o ubuntu:new.tar ubuntu_container
docker export ubuntu_container > ubuntu:latest.tar

docker exec -it mariadb /bin/bash

docker inspect mariadb

docker attach --sig-proxy=false mariadb

docker logs -f -t --since="2017-05-31" --tail=10 mariadb

