About
-----
The source code for running the experiments for the paper:

>> Wojciech Jaśkowski "Mastering 2048 with Delayed Temporal Coherence Learning, Multi-State Weight Promotion, Redundant Encoding and Carousel Shaping", IEEE Transactions on Computational Intelligence and AI in Games (accepted) [[arXiv](https://arxiv.org/pdf/1604.05085.pdf)]

The code in this repo has been used to train the best 2048 controller, which (along with an efficient C++ implementation) is available in a [separate repo](https://github.com/aszczepanski/2048).

Authors
-------
Wojciech Jaśkowski

(Some code in this repository is due to Marcin Szubert and Paweł Liskowski)

Prerequisites
-------------
Java 8, Maven

Building
--------
```bash
> mvn install:install-file -Dfile=lib/stilts.jar -DgroupId=uk.ac.starlink -DartifactId=stilts -Dversion=2.4 -Dpackaging=jar
> mvn package -Dmaven.test.skip=true
```

Running
-------
Example:
```bash
> java -Xmx50g -Dlog4j.configuration=file:configs/tciaig-2048/log4j.properties -Dframework.properties=configs/tciaig-2048/42-33_tcl-0.5-0.5.properties -Dseed=123 -Dresults_dir=results/tcl/123 -jar cevo.jar
```

>> Note: some of the experiments require a lot of memory (32GB might not be enough).

[Available config files](https://github.com/wjaskowski/mastering-2048/tree/master/configs/tciaig-2048)

Disclaimer
---------
This repo contains a lot of code irrelevant to 2048
