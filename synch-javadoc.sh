#!/bin/sh

CUR=$(dirname $0)

DEPLOY_SERVER=newweb
DEPLOY_PATH=/var/www/static/svenson-javadoc

rsync -rvIz --rsh=ssh $CUR/target/apidocs/ $DEPLOY_SERVER:$DEPLOY_PATH

