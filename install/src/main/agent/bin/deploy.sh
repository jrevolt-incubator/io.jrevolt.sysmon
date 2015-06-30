#!/bin/bash

set -eu

name="sysmon-agent"

cd $(dirname $0)/..

for server in $*; do

	echo "### Deploying agent on $server..."

	# cleanup
	ssh $server sudo service $name stop\; sudo chown -R sysmon:sysmon /opt/sysmon | true

	tar cz . | ssh $server sudo -u sysmon tar xz -C /opt/sysmon/

	ssh $server sudo /opt/sysmon/bin/linux-x86-64/install.sh\; sudo service $name status

done

