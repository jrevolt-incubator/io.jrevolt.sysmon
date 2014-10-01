#!/bin/bash

set -eu
fail() { echo "$1"; exit 1; }
[ $(whoami) = root ] || fail "Must be root, not $(whoami)";

# cleanup/reset
service sysmon-agent stop || true
chown -R sysmon:sysmon /opt/sysmon

dir="$(realpath $(dirname $0))"

cp -v $dir/sysmon-server.conf /etc/sysconfig/sysmon-server
rm -fv /etc/init.d/sysmon-server || true
ln -s $dir/sysmon-server /etc/init.d/sysmon-server && (
	chown root:root $dir/sysmon-server;
	chmod 755 $dir/sysmon-server;
)
chkconfig sysmon-server on

sudo -u sysmon ln -s $dir/sysmon-server ~/bin/sysmon-server

service sysmon-server start
