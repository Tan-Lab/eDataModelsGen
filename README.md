# eDataModelsGen

## About The Tool

This tool aims to generate various data models for the ECHONET Lite protocol from the <a href="https://echonet.jp/spec_object_rm_en/">APPENDIX Detailed Requirements for ECHONET Device Objects</a>. 

* Input: A JSON version of the Appendix (Machine Readable Appendix) provided by the Web API Working Group of the ECHONET Consortium.
* Output: Target data models (Device Description, Fiware Smart Data Model, s4echonet ontology, etc..) and target API documents that are compatible with Swagger.


##Built With

* Java SE-1.8
* Java Swing (for GUI),
* simple-json (to parse the MRA)
* jackson (to export json-data-models)
* owlapi (to export the s4echonet ontology)



## Getting Started
Clone the project and follow the instructions.

### Prerequisites
* Java > 1.8
* Maven

### Installation

* mvn install

### Usage
* java -jar **combined-jar-file** [-i] *required* **input files** [-o] *required* **output directory** [-g] *optional* **to enable GUI app**
* **input files**: 
* ***Full MRA file*** [xxxmra.json]
* ***A device object file*** [0xxxx.json]
* ***MC rule file*** [xxxMCRule.json]

#### Example
* ***No GUI***
* java -jar  **combined-jar-file** -i 0x0000.json 0x0001.json 0x0003.json 0x0000_MCRules.json -o /home
* *** With GUI***
* java -jar  **combined-jar-file** -i 0x0000.json 0x0001.json 0x0003.json 0x0000_MCRules.json -o /home -g
## Roadmap

The current version only supports the Device Description. Following features are coming next.
* s4echonet Ontology 
* swagger.yaml template for the ECHONET Lite WebAPI
* Fiware Smart Device Template


## Contributing

Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/aFeature`)
3. Commit your Changes (`git commit -m 'commit string'`)
4. Push to the Branch (`git push origin feature/aFeature`)
5. Open a Pull Request


## License

Distributed under the MIT License. See `LICENSE` for more information.


## Contact

PHAM, Van Cu - cupham@jaist.ac.jp

## Acknowledgements
This work is supported by the ECHONET Consortium
* Special thanks to ECHONET Web API working group members for their great efforts to create the machine readable appendix
