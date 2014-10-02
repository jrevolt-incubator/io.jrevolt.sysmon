#!/bin/bash

set -eu

cd $(dirname $0)

server_url="$1"

curl -s "$server_url/rest/servers"

echo TODO