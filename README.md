About
-----
The source code for running the experiments for the paper:

Wojciech Jaśkowski "Mastering 2048 with Delayed Temporal Coherence Learning, Multi-State Weight Promotion, Redundant Encoding and Carousel Shaping" [arXiv](https://arxiv.org/pdf/1604.05085.pdf)

Authors
-------
Wojciech Jaśkowski

Some code in this repository is due to Marcin Szubert and Paweł Liskowski

Prerequisites
-------------
Java 8, Maven

Building
--------
```bash
> mvn package
```

Running
-------
Example:
```bash
> java -Xmx50g -Dlog4j.configuration=file:configs/tciaig-2048/log4j.properties -jar cevo.jar -Dframework.properties=configs/tciaig-2048/42-33_tcl-0.5-0.5.properties -Dseed=123 -Dresults_dir=results/tcl/123
```

Note: some of the experiments require a lot of memory (32GB might not be enough).

Available configs: [configs/tciaig-2048]

Disclaimer
---------
This repo contains a lot of code irrelevant to 2048
