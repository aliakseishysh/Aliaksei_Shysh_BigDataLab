#!/bin/bash
#
# Performs packages uninstalling

sudo yum -y erase git
rm -rf /usr/bin/git
sudo yum -y erase java
sudo yum -y erase maven
rm -rf /usr/bin/mvn
sudo yum -y erase postgresql-server
rm -rf /var/lib/psql
rm -rf /var/lib/pgsql/data
