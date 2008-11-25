#!/bin/bash          

#------------------------------------------------------------------------------
# Simple script for creating a backup directory and copying the current set of
# installers into it.  This should be done periodically so that we can be sure
# that we have copies of the installers that work in case something breaks
# with the automated installer-builder process.
#------------------------------------------------------------------------------

# Initialize the constants that will be needed later.
BACKUP_DIR_STEM_NAME=backup
BACKUP_DIR=$BACKUP_DIR_STEM_NAME-$(date +%Y-%m-%d)
let NUM_BACKUPS_TO_KEEP=2

echo ""
echo "Creating backup of installers..."

# See if the backup directory already exists and delete it if so.

if [ -d $BACKUP_DIR ]; then
    echo ""
    echo "Warning: A backup directory already exists for today, indicating that a"
    echo "backup has already been performed.  This directory will be deleted."
    echo ""
    rm -rf $BACKUP_DIR
fi

# Create the backup directory.

mkdir $BACKUP_DIR
if [ "$?" -ne "0" ]; then
  echo "Error creating backup directory."
  exit 1
fi

# Copy the files into the backup directory.

echo ""
cp -v *installer* ./$BACKUP_DIR
cp -v *CD* ./$BACKUP_DIR
echo ""

if [ "$?" -ne "0" ]; then
  echo "Error copying files into backup directory."
  exit 1
fi

# Check if we need to delete any old backups.  The number of backups that
# are kept is defined by the number in the awk portion of the command
# that creates the list of excess backup directories.  Adjust this number
# if you need to increase or reduce the number of backups maintained.

EXCESS_BACKUP_DIRS=`ls -C1 -t -d backup* | awk 'NR>4'`
for DIR in $EXCESS_BACKUP_DIRS
do
   echo Removing old backup directory $DIR
   rm -rf $DIR
   echo ""
done

