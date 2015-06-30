#!/bin/bash

set -eu
fail() { echo "$1"; exit 1; }
[ $(whoami) = root ] || fail "Must be root, not $(whoami)";

name="sysmon-agent"

# cleanup/reset
service $name stop || true
chown -R sysmon:sysmon /opt/sysmon

dir="$(realpath $(dirname $0))"

cp -v $dir/$name.conf /etc/sysconfig/$name && chown sysmon:sysmon /etc/sysconfig/$name
rm -fv /etc/init.d/$name || true
ln -s $dir/$name /etc/init.d/$name && (
	chown root:root $dir/$name;
	chmod 755 $dir/$name;
)
chkconfig $name on

shortcut="$(echo ~sysmon)/bin/$name"
[ -L $shortcut ] && rm -f $shortcut
sudo -u sysmon ln -sv $dir/$name $shortcut

[ -d ~sysmon/log ] || sudo -u sysmon mkdir ~sysmon/log
[ -f ~sysmon/log/wrapper.log ] && sudo rm ~sysmon/log/wrapper.log

service $name start
