#!/bin/bash

echo "Waiting for MySQL slave to start..."

until mysql -uroot -proot_password -e "SELECT 1" > /dev/null 2>&1; do
  sleep 2
done

echo "Slave MySQL started"

echo "Waiting for master..."

until mysql -h db-master -uroot -proot_password -e "SELECT 1" > /dev/null 2>&1; do
  sleep 2
done

echo "Master ready"

MASTER_STATUS=$(mysql -h db-master -uroot -proot_password -e "SHOW MASTER STATUS\G")

LOG_FILE=$(echo "$MASTER_STATUS" | grep File | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep Position | awk '{print $2}')

echo "Configuring replication..."

mysql -uroot -proot_password <<EOF
STOP SLAVE;
CHANGE MASTER TO
MASTER_HOST='db-master',
MASTER_USER='replica',
MASTER_PASSWORD='replica_pass',
MASTER_LOG_FILE='$LOG_FILE',
MASTER_LOG_POS=$LOG_POS;
START SLAVE;
EOF

echo "Replication configured"
