#!/bin/bash

# the two included paths should be placed last (see: https://stackoverflow.com/a/20167038)
# qpm test.run -l $TRAVIS_BUILD_DIR/BUILD/travis_test_run.json --path $SCLANG --include $HOME/Quarks $TRAVIS_BUILD_DIR/testsuite/classlibrary
QPM_DEBUG=1 qpm test.run -l '/home/travis/build/supercollider/supercollider/BUILD/travis_test_run.json' --path $SCLANG --include $HOME/Quarks $TRAVIS_BUILD_DIR/testsuite/classlibrary
