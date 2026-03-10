#!/bin/bash

echo "Waiting for MySQL slave to start..."

until mysql -h 127.0.0.1 --protocol=TCP -uroot -proot_password -e "SELECT 1" > /dev/null 2>&1; do
  sleep 2
done

echo "Slave MySQL started"

echo "Waiting for master..."

until mysql -h db-master -uroot -proot_password -e "SELECT 1" > /dev/null 2>&1; do
  sleep 2
done

echo "Master ready"

MASTER_STATUS=$(mysql -h db-master -uroot -proot_password -e "SHOW MASTER STATUS\G")

LOG_FILE=$(echo "$MASTER_STATUS" | grep -m1 " File:" | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep -m1 " Position:" | awk '{print $2}')

echo "Configuring replication from $LOG_FILE @ $LOG_POS ..."

mysql -h 127.0.0.1 --protocol=TCP -uroot -proot_password <<EOF
STOP REPLICA;
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='db-master',
  SOURCE_USER='replica',
  SOURCE_PASSWORD='replica_pass',
  SOURCE_LOG_FILE='${LOG_FILE}',
  SOURCE_LOG_POS=${LOG_POS};
START REPLICA;
EOF

echo "Replication configured"
