# Svenson

svenson is a Java5 JSON generator/parser. It lets you convert Java object graphs into JSON and vice versa. svenson offers an API and annotations to aid you in this.

## Features

* Free choice of java side objects: From total generic list/map scenarios to mapping POJOs to/from JSON or a mix between those two, from JSON generation being something your classes handle themselves to something JSONifying them.

* Annotations and interfaces to help you to create your own applications that work with JSON.

* Quality Assurance with unit tests ensures svenson keeps working after changes.

![Svenson cycle](http://fforw.de/static/image/svenson-cycle.png)

## Javadoc
[Current release javadoc](http://fforw.de/static/svenson-javadoc/)


## Genesis

The initial code for svenson grew out of a simple generator tool class I once wrote for my projects. I wrote JSON parsing code while working on my own couchdb driver called [jcouchdb](http://code.google.com/p/jcouchdb/). Later I decided to create a standalone svenson project because the outcome seemed to be sufficient for all kinds of scenarios not involving jcouchdb.

## Svenson and slf4j

Svenson now uses slf4j for logging, so you need to configure slf4j to log to your favourite logging API. see [The slf4j manual](http://www.slf4j.org/manual.html) for details.

If you use slf4j-log4j12, you still need a valid log4j configuration. svenson (especially parsing) will be slowed down considerably when that log4j configuration is missing.

### example log4j configuration
    log4j.rootLogger=ERROR, Console

    # uncomment to set svenson logging to DEBUG
    #log4j.logger.org.svenson=DEBUG

    # log to the console
    log4j.appender.Console=org.apache.log4j.ConsoleAppender
    log4j.appender.Console.layout=org.apache.log4j.PatternLayout
    log4j.appender.Console.layout.ConversionPattern=%-5p %c: %m%n

## Maven Repository

Now that we're sync by the big repositories, you just need to add the following dependency to your pom.xml:

    <dependency>
      <groupId>com.google.code.svenson</groupId>
      <artifactId>svenson</artifactId>
      <version>1.4.5</version>
      <type>jar</type>
    </dependency>


## Improve Svenson
I'm always interested in getting new ideas to improve my projects, so
if your requirements are not met by svenson, you could try contacting me to give me a clearer idea of what could be added.
