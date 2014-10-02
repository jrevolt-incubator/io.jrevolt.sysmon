#!/bin/bash

set -eu
fail() { echo "$1"; exit 1; }
[ $(whoami) = root ] || fail "Must be root, not $(whoami)";

dir="$(realpath $(dirname $0))"

cp -v $dir/sysmon-agent.conf /etc/sysconfig/sysmon-agent && chown sysmon:sysmon /etc/sysconfig/sysmon-agent
rm -fv /etc/init.d/sysmon-agent || true
ln -s $dir/sysmon-agent /etc/init.d/sysmon-agent && (
	chown root:root $dir/sysmon-agent;
	chmod 755 $dir/sysmon-agent;
)
chkconfig sysmon-agent on

shortcut="$(echo ~sysmon)/bin/sysmon-agent"
[ -L $shortcut ] && rm -f $shortcut
sudo -u sysmon ln -sv $dir/sysmon-agent $shortcut

service sysmon-agent start
