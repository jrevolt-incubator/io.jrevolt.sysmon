#!/bin/bash

set -eu

server=$1
name="sysmon-server"

# cleanup
ssh $server sudo service $name stop\; sudo chown -R sysmon:sysmon /opt/sysmon | true

cd $(dirname $0)

scp -r . sysmon@$server:

ssh $server sudo /opt/sysmon/bin/linux-x86-64/install.sh\; sleep 5\; sudo service $name status
