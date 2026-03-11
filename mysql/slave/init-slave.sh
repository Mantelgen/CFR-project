#!/bin/bash
set -e

echo "Waiting for master..."
until mysql -h db-master -uroot -proot_password -e "SELECT 1" >/dev/null 2>&1; do
  sleep 3
done

echo "Waiting for slave..."
until mysql -h db-slave -uroot -proot_password -e "SELECT 1" >/dev/null 2>&1; do
  sleep 3
done

echo "Getting master binlog position..."
MASTER_STATUS=$(mysql -h db-master -uroot -proot_password -e "SHOW MASTER STATUS\G")

LOG_FILE=$(echo "$MASTER_STATUS" | grep -m1 " File:" | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep -m1 " Position:" | awk '{print $2}')

echo "Configuring replication..."

mysql -h db-slave -uroot -proot_password <<EOF
STOP REPLICA;
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='db-master',
  SOURCE_USER='replica',
  SOURCE_PASSWORD='replica_pass',
  SOURCE_LOG_FILE='${LOG_FILE}',
  SOURCE_LOG_POS=${LOG_POS};
START REPLICA;
EOF

echo "Replication configured successfully"
