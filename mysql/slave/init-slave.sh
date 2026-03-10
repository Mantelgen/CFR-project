#!/bin/bash
set -e

echo "Getting master binlog position..."
MASTER_STATUS=$(mysql -h db-master -uroot -proot_password -e "SHOW MASTER STATUS\G")

LOG_FILE=$(echo "$MASTER_STATUS" | grep -m1 " File:" | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep -m1 " Position:" | awk '{print $2}')

if [ -z "$LOG_FILE" ] || [ -z "$LOG_POS" ]; then
  echo "ERROR: Could not retrieve master binlog position" >&2
  exit 1
fi

echo "Configuring replication from $LOG_FILE @ $LOG_POS ..."

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
