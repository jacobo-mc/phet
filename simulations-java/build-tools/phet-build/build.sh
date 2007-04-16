#!/bin/bash

OLD_DIR=`pwd`

ROOT_DIR=../..

ANT_HOME=$ROOT_DIR/build-tools/apache-ant-1.7.0
ANT_OPTS=-Xmx640m

PATH=$ANT_HOME/bin:$PATH

export ANT_HOME PATH ANT_OPTS

cd $ROOT_DIR

if [ `uname` = "Darwin" ]; then
	JAVA_HOME=/Library/Java/Home
	export JAVA_HOME
	
	echo "Mac detected; assuming Java home is $JAVA_HOME"
fi

cd $ROOTDIR/build-tools/phet-build
if [ "$JAVA_HOME" = "" ]; then
    echo "The environment variable JAVA_HOME must be set to the location of a valid JDK."
else
    if [ $# = 0 ]; then
        ant
    elif [ $# = 1 ]; then
        ant $1
    elif [ $# = 2 ]; then
        ant $1 -Dname.sim=$2
    else
        echo "Unknown command line arguments."
    fi
fi

cd $OLD_DIR
