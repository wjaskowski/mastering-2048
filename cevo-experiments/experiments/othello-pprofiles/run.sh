#!/bin/bash
nice -10 java -Dhazelcast.config=hazelcast.xml -Dhazelcast.logging.type=log4j -Dlog4j.configuration=file:log4j.properties -Xmx1g -XX:PermSize=192M -XX:MaxPermSize=192M -jar random.jar put.ci.cevo.newexperiments.profiles.GeneratePerfProfilesDBRandom

