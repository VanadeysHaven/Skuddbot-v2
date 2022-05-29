echo "Pulling latest version from Git..."
sudo git pull

commit=$(sudo git log --pretty=format:'%h' -n 1)
branch=$(sudo git branch --show-current)
time="Temp"

echo "Deploying $branch > $commit"

echo "Backing up database..."
sudo docker exec -it skuddbot_v2_database sh -c 'exec mysqldump ${MYSQL_DATABASE} -uroot -p"${MYSQL_ROOT_PASSWORD}"' > backup.sql

echo "Updating database..."
sudo docker exec -it skuddbot_v2_database sh -c 'exec mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" ${MYSQL_DATABASE} -e "INSERT INTO global_settings(setting, value) VALUE (\"commit\",\"$commit\") ON DUPLICATE KEY UPDATE value=\"$commit\";"'
sudo docker exec -it skuddbot_v2_database sh -c 'exec mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" ${MYSQL_DATABASE} -e "INSERT INTO global_settings(setting, value) VALUE (?,?) ON DUPLICATE KEY UPDATE value=?;"'
sudo docker exec -it skuddbot_v2_database sh -c 'exec mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" ${MYSQL_DATABASE} -e "INSERT INTO global_settings(setting, value) VALUE (?,?) ON DUPLICATE KEY UPDATE value=?;"'

echo "Shutting down bot..."
sudo docker compose down

echo "Rebuilding images and launching..."
sudo docker compose up -d
