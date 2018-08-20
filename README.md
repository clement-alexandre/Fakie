# Fakie

[![Build Status](https://travis-ci.org/alexandre-clement/fakie.svg?branch=master)](https://travis-ci.org/alexandre-clement/fakie)
[![codecov](https://codecov.io/gh/alexandre-clement/fakie/branch/master/graph/badge.svg)](https://codecov.io/gh/alexandre-clement/fakie)

Mining Mobile Apps to Learn Design Patterns and Code Smells.  
Fakie is a tool to automatically generate the Antipattern Queries from the Graph DB for [Paprika](https://github.com/alexandre-clement/paprika).  

# Table of contents
*   [Getting Started](#getting-started)
*   [Output](#output)
*   [Usage](#usage)
*   [Code Smells](#code-smells-file)
*   [Overview](#overview)
*   [Development](#development)
*   [Troubleshooting](#troubleshooting)

## Getting Started

### Build with [Maven](https://maven.apache.org/) - Dependency Management

* Compile `mvn clean compile`
* Running the test `mvn clean install`
* Create jar `mvn clean package`
* Execute `mvn exec:java -Dexec.args="here goes your arguments separated by space"`

# Results

* BLOB Class
```cypher
// Confidence : 1.0, Support : 1.0
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_async_task = false AND n.is_broadcast_receiver = false AND n.number_of_methods >= 14.5 AND n.number_of_attributes >= 7.5 AND n.npath_complexity >= 2557.0 AND n.number_of_children >= 0.0)
RETURN
     n.name
```

* Complex Class (CC)
```cypher 
// Confidence : 0.9975247524752475, Support : 0.9975247524752475
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_application = false AND n.npath_complexity >= 2557.0 AND n.number_of_children >= 0.0)
RETURN
     n.name
```
```cypher 
// Confidence : 1.0, Support : 0.9801980198019802
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_async_task = false AND n.is_application = false AND n. --CLASS_OWNS_METHOD--> onTrimMemory = false AND n.is_interface = false AND n.name <> 'com.addi.core.tokens.numbertokens.Int16NumberToken' AND n.parent_name <> 'com.addi.core.tokens.NumberToken' AND n.npath_complexity >= 2557.0 AND n.number_of_children >= 0.0 AND n.class_complexity >= 23.5)
RETURN
     n.name
```
```cypher 
// Confidence : 1.0, Support : 1.0
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_async_task = false AND n.is_application = false AND n. --CLASS_OWNS_METHOD--> onTrimMemory = false AND n.is_interface = false AND n.npath_complexity >= 2557.0 AND n.number_of_children >= 0.0 AND n.class_complexity >= 23.5)
RETURN
     n.name
```
```cypher 
// Confidence : 1.0, Support : 0.9975247524752475
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_async_task = false AND n.is_application = false AND n. --CLASS_OWNS_METHOD--> onTrimMemory = false AND n.is_interface = false AND n.name <> 'com.addi.core.tokens.numbertokens.Int16NumberToken' AND n.npath_complexity >= 2557.0 AND n.number_of_children >= 0.0 AND n.class_complexity >= 23.5)
RETURN
     n.name
```
* Hashmap Usage (HMU)
```cypher
// Confidence : 1.0, Support : 1.0
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n. --CALLS--> <init>#java.util.HashMap = true AND n.is_synchronized = false AND n.is_abstract = false AND NOT n.number_of_parameters >= 5.0)
RETURN
     n.name
```
```cypher
// Confidence : 1.0, Support : 0.8823529411764706
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n. --CALLS--> <init>#java.util.HashMap = true AND n.is_init = false AND n.is_synchronized = false AND n.is_abstract = false AND n.full_name <> '<clinit>#com.addi.core.interpreter.GlobalValues' AND n.name <> '<clinit>' AND NOT n.number_of_parameters >= 5.0)
RETURN
     n.name
```

* No Low Memory Resolver (NLMR)

```cypher
// Confidence : 1.0, Support : 1.0
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_application = false AND n. --CLASS_OWNS_METHOD--> onTrimMemory = false AND n. --CLASS_OWNS_METHOD--> onLowMemory = false AND n.number_of_children >= 0.0)
RETURN
     n.name
```

```cypher
// Confidence : 1.0, Support : 0.8783783783783784
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_application = false AND n. --CLASS_OWNS_METHOD--> onTrimMemory = false AND n. --CLASS_OWNS_METHOD--> onLowMemory = false AND n.number_of_children >= 0.0 AND NOT n.class_complexity >= 23.5)
RETURN
     n.name
```

* Long Method (LM)

```cypher
// Confidence : 1.0, Support : 1.0
CYPHER planner=rule
START
     n = node(*)
WHERE
     (n.is_synchronized = false AND n.is_abstract = false)
RETURN
     n.name
```


# Usage

## Running Fakie Phases

* [**Analyse**](#analyse) : Use Paprika to analyse android apk and generate a database.
* [**Query**](#query) : Use the queries available in Paprika to detect the code smells in the database.
* [**Generate**](#generate) : Generate a new set of queries for Paprika from the database and the code smells detected by Paprika
    * [**Load Graph**](#graph-loader) : Load the Paprika database as a graph
    * [**Apply Learning Algorithm**](#learning-algorithm) : Apply a learning algorithm on the graph to generate rules
    * [**Export Rules as Queries**](#query-exporter) : Export the generated rules as queries for Paprika

An interesting thing to note is that phases may be executed in sequence.
```
fakie analyse query generate
```
Moreover, phases which needs an input will take by default the output of the previous phases.  
For instances, the `query` in our example will take the database generated by the `analyse` phase in input.  
This behaviour can be overridden with the options of the command.

## Help and Version

```
 ███████╗ █████╗ ██╗  ██╗██╗███████╗
 ██╔════╝██╔══██╗██║ ██╔╝██║██╔════╝
 █████╗  ███████║█████╔╝ ██║█████╗
 ██╔══╝  ██╔══██║██╔═██╗ ██║██╔══╝
 ██║     ██║  ██║██║  ██╗██║███████╗
 ╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚══════╝

Usage: fakie [-hV] [COMMAND]

Description:
  Mining Mobile Apps to Learn Design Patterns and Code Smells.

Options:
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

Commands:
  analyse     Run Paprika analyse on given folder
  query       Run Paprika query on the given db
  load-neo4j  Import Android applications from a Neo4j database.
  generate    Macro command to load a Neo4j database and then use the FPGrowth
                algorithm to generate Cypher queries
 ```
 
## Analyse

Use Paprika to analyse android apk and generate a database.

```
Usage: fakie analyse [-hV] [-a=<androidJars>] [-db=<db>] [-f=<apk>] [-i=<info>]
                     [COMMAND]

Description:
  Run Paprika analyse on given folder

Options:
  -a, --android=<androidJars>
                             Path to the android platform jars
                               Default: android_platform
  -db, --database=<db>       Path to the info Paprika db
  -f, --apk-folder=<apk>     Path to the apk folder
                               Default: apk
  -h, --help                 Show this help message and exit.
  -i, --info-apk=<info>      Path to the info apk file
                               Default: info.csv
  -V, --version              Print version information and exit.

Commands:
  query       Run Paprika query on the given db
  load-neo4j  Import Android applications from a Neo4j database.
  generate    Macro command to load a Neo4j database and then use the FPGrowth
                algorithm to generate Cypher queries
```

### File Structure

By default, your file structure should look like this
```
root
│   info.csv   
│
└───android_platform
│   │   android-3
│   │   │   android.jar
│   │   android-4
│   │   │   android.jar
│   │   ...
│   
└───apk
    │   org.torproject.android.apk
    │   org.wikipedia.apk
    │   ...
```
But you can specify each of this path by using the options of the `analyse` command.  
You can find many Android platforms in [this Github repository](https://github.com/Sable/android-platforms). 


### Info.csv

The file `info.csv` contains the name, the main package and the apk name of the android application you want to analyse.  
Here is an example with 4 applications :

```csv
"Google IO Sched","com.google.samples.apps.iosched","iosched.apk"
"AdAway","org.adaway","org.adaway_53.apk"
"ADW.Launcher","org.adw.launcher","org.adw.launcher_34.apk"
"Wikipedia","org.wikipedia","org.wikipedia_109.apk"
```

Note : the csv must not have a header

### Examples

* `fakie analyse`
* `fakie analyse -a "android_platform_master" -db "output" -f . -i "info/info.csv"`

## Query

Use the queries available in Paprika to detect the code smells in the database.

```
Usage: fakie query [-hV] [-db=<db>] [-s=<suffix>] [COMMAND]

Description:
  Run Paprika query on the given db

Options:
  -db, --database=<db>       Path to the info Paprika db
  -h, --help                 Show this help message and exit.
  -s, --suffix=<suffix>      Suffix for the csv filename
  -V, --version              Print version information and exit.

Commands:
  load-neo4j  Import Android applications from a Neo4j database.
  generate    Macro command to load a Neo4j database and then use the FPGrowth
                algorithm to generate Cypher queries
```

### File Structure

By default, your file structure should look like this
```
root
└───db
│   │   ...
└───codesmell   <-- this folder will be generated if it does not exist
│   │   ...     <-- the detected code smells goes here
```
But you can specify each of this path by using the options of the `query` command.

### Examples

* `fakie query`
* `fakie query -db "output" -s "output-codesmell/"`  
:warning: You have to put a `/` at the end of your suffix if you want it to be a folder. This behavior comes from Paprika.

## Generate

Generate a new set of queries for Paprika from the database and the code smells detected by Paprika

```
Usage: fakie generate [-hV] [-db=<db>] [-f=<codesmell>] [-n=<n>] [-o=<output>]
                      [-s=<support>]

Description:
  Macro command to load a Neo4j database and then use the FPGrowth algorithm to
generate Cypher queries

Options:
  -db, --database=<db>       Path to the Neo4j database
  -f, --file=<codesmell>     Path to the file containing the code smells in the
                               database
  -h, --help                 Show this help message and exit.
  -n, --nb-rules=<n>         Number of rules to find
                               Default: 10000
  -o, --output=<output>      Destination folder for the generated queries
  -s, --min-support=<support>
                             Minimum support bound
                               Default: 0.1
  -V, --version              Print version information and exit.
```


### File Structure

By default, your file structure should look like this
```
root
└───db
│   │   ...
└───codesmell    <--- every csv in this folder will be imported as a code smell file
│   │   BLOB.csv
│   │   LM.csv
│   │   ...
└───queries      <-- this folder will be generated if it does not exist
│   └───cypher   <-- this folder will be generated if it does not exist
│   │   │   ...  <-- the generated queries goes here
```
But you can specify each of this path by using the options of the `query` command.

### Examples

* `fakie generate`
* `fakie generate -db "output" -n 50000 -s 0.5 -f "output-codesmell" -o "output-queries"`


## Graph loader

Load the Paprika database as a graph

* Neo4j `load-neo4j` : Load graph from a Neo4j database

```
Usage: fakie load-neo4j [-hV] [-db=<db>] [COMMAND]

Description:
  Import Android applications from a Neo4j database.

Options:
  -db, --database=<db>       Path to the Neo4j database
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.

Commands:
  fpgrowth  Use the FPGrowth algorithm on the dataset
  apriori   Use the Apriori algorithm on the dataset
```

## Learning Algorithm

Apply a learning algorithm on the graph to generate rules

* FPGrowth `fpgrowth` : Use the FPGrowth algorithm on the dataset
```
Usage: fakie load-neo4j fpgrowth [-hV] [-f=<codesmell>] [-n=<n>] [-s=<support>]
                                 [COMMAND]

Description:
  Use the FPGrowth algorithm on the dataset

Options:
  -f, --file=<codesmell>   Path to the file containing the code smells in the
                             database
  -h, --help               Show this help message and exit.
  -n, --nb-rules=<n>       Number of rules to find
                             Default: 10000
  -s, --min-support=<support>
                           Minimum support bound
                             Default: 0.1
  -V, --version            Print version information and exit.

Commands:
  cypher  Export the generated rules in the Cypher query language
```

* Apriori `apriori` : Use the Apriori algorithm on the dataset :warning: NOT RECOMMENDED : VERY SLOW :snail:
```
Usage: fakie load-neo4j apriori [-hV] [-f=<codesmell>] [-n=<n>] [-s=<support>]
                                [COMMAND]

Description:
  Use the Apriori algorithm on the dataset

Options:
  -f, --file=<codesmell>   Path to the file containing the code smells in the
                             database
  -h, --help               Show this help message and exit.
  -n, --nb-rules=<n>       Number of rules to find
                             Default: 10000
  -s, --min-support=<support>
                           Minimum support bound
                             Default: 0.1
  -V, --version            Print version information and exit.

Commands:
  cypher  Export the generated rules in the Cypher query language
```
        
## Query Exporter

Export the generated rules as queries for Paprika

* Cypher `cypher` : Export the generated rules in the Cypher query language
```
Usage: fakie load-neo4j apriori cypher [-hV] [-o=<output>]

Description:
  Export the generated rules in the Cypher query language

Options:
  -h, --help              Show this help message and exit.
  -o, --output=<output>   Destination folder for the generated queries
  -V, --version           Print version information and exit.
```

## Code Smells File

In order to work properly, the learning algorithm needs an input file containing the code smell present in the targeted project.
This file should look like this :

### CSV (Default Paprika output for code smell detection)

The code smell name should appear in the filename (ex: `BLOB.csv`)

```csv
app_key,lack_of_cohesion_in_methods,number_of_methods,number_of_attributes,full_name,fuzzy_value
Aard,1837,71,29,aarddict.android.ArticleViewActivity,1
Aard,150,20,10,aarddict.android.LookupActivity,0.245
Aard,116,25,17,aarddict.android.DictionaryService,1
```

### Json

```json
{
  "codeSmells": [
    {
      "labels": ["Class"],
      "properties": {"name": "org.torproject.android.service.TorService"},
      "name": "God Class"
    },
    {
      "labels": ["Class"],
      "properties": {"name": "org.torproject.android.OrbotMainActivity"},
      "name": "God Class"
    },
    {
      "labels": ["Class"],
      "properties": {"name": "org.torproject.android.Prefs"},
      "name": "God Class"
    },
    {
      "labels": ["Class"],
      "properties": {"name": "org.torproject.android.settings.TorifiedApp"},
      "name": "God Class"
    }
  ]
}
```

Note : Fakie tries to find the vertex that best fit the given labels and properties. If Fakie finds too many matches for a given instance, it will silently ignore it.

## Overview

![Overview](docs/images/overview.svg)

## Development

### Embedded Paprika
* Run Paprika Analyse :heavy_check_mark:
* Run Paprika Query :x:
    * Run all fuzzy queries :heavy_check_mark:
    * Run all queries :x:
    * Run only a specific query :x:
    * Run a subset of queries :x:
* Parse Paprika Query result :heavy_check_mark:

### Load Graph
* Neo4j :heavy_check_mark:

### Process Fakie Model
* Add code smell to the Fakie model :heavy_check_mark:
* Convert vertices numeric properties to nominal :heavy_check_mark:
* Convert vertices numeric properties to boolean :heavy_check_mark:
* Convert vertices arrays properties to nominal :heavy_check_mark:
* Convert vertices nominal properties to boolean :heavy_check_mark:
* Removing properties with a single value :heavy_check_mark:
* Process only vertices which contains a code smell :heavy_check_mark:
* Convert edges properties values to boolean :x:
* Resolution of collision among properties :x:

Note: Converting numeric to nominal and then nominal to boolean is not equals to converting numeric to boolean.
The conversion from numeric to boolean uses a threshold system in order to accelerate the learning phase.

### Dump Fakie Model
* Dump Fakie Model to an ARFF dataset :heavy_check_mark:

### Read Dataset
* Read an ARFF dataset :heavy_check_mark:

### Data Mining Algorithm
* Implement FPGrowth algorithm to infer associations rules among properties :heavy_check_mark:
* Implement Apriori algorithm to infer associations rules among properties :heavy_check_mark:

### Filter Rules
* Filter the rules to keep only those that identify a smell code :heavy_check_mark:
* Filter the rules to keep only the many to one rules :heavy_check_mark:
* Remove consequences in rules that are not a code smell :heavy_check_mark:
* Filter rules with the same support but a different the amount of premises :heavy_check_mark:

### Export Rules to Queries
* Export the rules to Cypher :heavy_check_mark:
* Convert Fakie rules to allow a reuse by Paprika :heavy_check_mark:

## Troubleshooting

Fakie is still in development.  
Found a bug? We'd love to know about it!  
Please report all issues on the github issue tracker.
