#! /bin/bash

cd /orientdb/bin
oetl.sh /orientdb/data/openbeerdb/import/categories.json
oetl.sh /orientdb/data/openbeerdb/import/styles.json
oetl.sh /orientdb/data/openbeerdb/import/breweries.json
oetl.sh /orientdb/data/openbeerdb/import/beers.json
