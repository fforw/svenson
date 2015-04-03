#!/bin/sh

DEPLOY_SERVER=newweb
DEPLOY_PATH=/var/www/static/svenson-javadoc

rsync -rvIz --rsh=ssh /home/sven/workspace/svenson/target/apidocs/ $DEPLOY_SERVER:$DEPLOY_PATH

