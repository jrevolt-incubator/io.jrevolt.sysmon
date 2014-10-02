#!/bin/bash

set -eu

server=$1
name="sysmon-server"

# cleanup
ssh $server sudo service $name stop\; sudo chown -R sysmon:sysmon /opt/sysmon | true

cd $(dirname $0)/..

tar cz . | ssh $server sudo -u sysmon tar xz -C /opt/sysmon/

ssh $server sudo /opt/sysmon/bin/linux-x86-64/install.sh\; sudo service $name status
