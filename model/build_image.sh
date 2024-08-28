#!/usr/bin/env bash

#
# COPYRIGHT Ericsson 2023
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

VERSION=0.0.6
docker build -t armdocker.rnd.ericsson.se/proj-smoke-dev/eric-oss-get-started-ml-model:${VERSION} .
docker push armdocker.rnd.ericsson.se/proj-smoke-dev/eric-oss-get-started-ml-model:${VERSION}
