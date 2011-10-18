#! /bin/sh
git push origin master
git fetch bplug
git subtree merge --squash -P base bplug/master
