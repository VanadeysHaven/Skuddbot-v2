echo "Setting up..."
mkdir database/import_data -p
mkdir backups -p

echo "Pulling latest version from Git..."
git fetch
git pull

commit=$(git log --pretty=format:'%h' -n 1)
branch=$(git branch --show-current)
time_file=$(date +%Y%m%d%H%M%S)
time_bot=$(date "+%d-%m-%Y %H:%M:%S \(UTC\)")

echo "Deploying $branch > $commit"
echo "Updating configs"
rm configs/git.env -f
touch configs/git.env
{
  echo "COMMIT=$commit
  BRANCH=$branch
  DEPLOY_TIME=$time_bot"
} >> configs/git.env

database_state=$(sudo docker inspect --format="{{.State.Running}}" skuddbot_v2_database)
if [[ $*  == "--skip-db" ]];
then
  echo "Skipping backup database"
else
  if [ "$database_state" == "true" ];
  then
    echo "Backing up database..."
    sudo docker exec -it skuddbot_v2_database sh -c 'exec mysqldump ${MYSQL_DATABASE} -uroot -p"${MYSQL_ROOT_PASSWORD}"' | sudo tee backups/backup-"$time_file".sql > /dev/null
  fi
fi

echo "Shutting down bot..."
sudo docker compose down

echo "Rebuilding images and launching..."
sudo docker compose up -d --remove-orphans --build

echo
