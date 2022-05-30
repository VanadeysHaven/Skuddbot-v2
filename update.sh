echo "Pulling latest version from Git..."
git pull

commit=$(git log --pretty=format:'%h' -n 1)
branch=$(git branch --show-current)
time_file=$(date +%Y%m%d%H%M%S)
time_bot=$(date "+%Y-%m-%d %H:%M:%S \(UTC\)")

echo "Deploying $branch > $commit"
echo "Updating configs"
rm configs/git.env -f
touch configs/git.env
echo "COMMIT=$commit" >> configs/git.env
echo "BRANCH=$branch" >> configs/git.env
echo "DEPLOY_TIME=$time_bot" >> configs/git.env

if [ ! "$(docker ps -a | grep skuddbot_v2_database)" ];
then
  echo "Backing up database..."
  sudo docker exec -it skuddbot_v2_database sh -c 'exec mysqldump ${MYSQL_DATABASE} -uroot -p"${MYSQL_ROOT_PASSWORD}"' > backups/backup-$time_file.sql
fi

echo "Shutting down bot..."
sudo docker compose down

echo "Rebuilding images and launching..."
sudo docker compose up -d --remove-orphans --build

echo
