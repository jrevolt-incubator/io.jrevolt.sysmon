#!/bin/bash

cd $(dirname $0)

test="
$(echo st1{soa,ints,bpms,crms}{01,02})
$(echo st1prox{01,02,11,12})
$(echo st1port{01,02,11,12})
$(echo st1epod{01,02,11,12})
$(echo st1bils{01,02})
$(echo st1lear{01,02})
$(echo st1ccas{01,02,03,04})
$(echo st1vu{as,ss}{01,02})
$(echo st1egov{01,02,11,12,21,22,31,32,41,42,51,52})
$(echo st1sts{01,02})
"

for i in $test; do
echo "## $i ##"
./deploy.sh $i
done