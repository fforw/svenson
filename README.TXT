venson is a Java5 JSON generator/parser. It lets you convert Java object graphs into JSON and vice versa. svenson offers an API and annotations to aid you in this. 

== Features ==

 * Free choice of java side objects: From total generic list/map scenarios to mapping POJOs to/from JSON or a mix between those two, from JSON generation being something your classes handle themselves to something JSONifying them.

 * Annotations and interfaces to help you to create your own applications that work with JSON.

 * Quality Assurance with unit tests ensures svenson keeps working after changes.

<wiki:gadget url="http://svenson.googlecode.com/svn/trunk/misc/svenson-wiki-gadget.xml" width="639" height="402" border="0" />


[http://fforw.de/static/svenson-javadoc/ Current release javadoc]

[http://fforw.de/post/Memory-consumption-changes-in-svenson-13/ Read more about memory consumption changes in svenson 1.3]

== Genesis == 

The initial code for svenson grew out of a simple generator tool class I once wrote for my projects. I wrote JSON parsing code while working on my own couchdb driver called [http://code.google.com/p/jcouchdb/ jcouchdb]. Later I decided to create a standalone svenson project because the outcome seemed to be sufficient for all kinds of scenarios not involving jcouchdb.

== Svenson and slf4j ==

Svenson now uses slf4j for logging, so you need to configure slf4j to log to your favourite logging API. see [http://www.slf4j.org/manual.html The slf4j manual] for details.

If you use slf4j-log4j12, you still need a valid log4j configuration. svenson (especially parsing) will be slowed down considerably when that log4j configuration is missing.

=== example log4j configuration ===
{{{
log4j.rootLogger=ERROR, Console

# uncomment to set svenson logging to DEBUG
#log4j.logger.org.svenson=DEBUG

# log to the console
log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout
log4j.appender.Console.layout.ConversionPattern=%-5p %c: %m%n
}}}

== Maven Repository ==

Now that we're sync by the big repositories, you just need to add the following dependency to your pom.xml:

{{{
    <dependency>
      <groupId>com.google.code.svenson</groupId>
      <artifactId>svenson</artifactId>
      <version>1.3.7</version>
      <type>jar</type>
    </dependency>
}}}


== Changelog ==
{{{
Changes from 1.3.6 to 1.3.7:
 
 * use BigDecimal for larger than long or fractional numbers
 * @JSONReference mechanism to dump only a replacement value for linked values.
 * Support for JavaScript-like property-path access to java object graphs ( org.svenson.util.JSONPathUtil )  

Changes from 1.3.5 to 1.3.6:

 * Support Set and Collection for JSONTypeHint. Intermediary list in JSON parser gets converted to HashSet. added Set => HashSet as known interface impl.
 * Support all standard JDK set implementation for @JSONTypeHint
 * Find best replacement interface, instead of taking the first possible. Fixes broken replacement interface behaviour
 * Add Array support
 * Properly regard initial type mapping to enable SubType-Matched parsing from JSON root
 * Svenson no longer reads ignored properties. 

Changes from 1.3.4 to 1.3.5:

 * add support for non-annotation based TypeConversion. 
   See org.svenson.JSON.registerTypeConversion(Class<?>, TypeConverter) and
   org.svenson.JSONParser.registerTypeConversion(Class, TypeConverter)
 * add SinkAwareJSONifier for sink based JSONifying
 * add default ignore properties. See org.svenson.JSON.setIgnoredProperties(Collection<String>)
 * change List<String> ignoredProps to Collection<String> ignoredProps to enable using Set<String> but keep source compatibility. 

Changes from 1.3.3 to 1.3.4:
 * Add a copy constructor to JSONParser that creates a copy of the JSONParser with the same config. Fixes serious bug in jcouchdb and other use cases.

Changes from 1.3.2 to 1.3.3:
 * added JSONConfig to represent both a parser and a generator config.
 * enable path matching based on previous type hint, add sub type matcher
 * took over common-beanutil-1.8 depedency from jcouchdb, refactored type conversion, Fixes Issue #5.

Changes from 1.3.1 to 1.3.2:
 * now uses slf4j as logging API
 * now available via Maven
 * support for interface based JSONifiers

Changes from 1.3.0 to 1.3.1:
 * support for streaming output 
 * added ClassNameBasedTypeMapper
 * added @JSONConverter annotation allowing you to convert bean properties
 * added PathMatcher to flexibilize type hints and type mappers. This feature was sponsored by [http://quinscape.de QuinScape]
 * about 20% better JSON parser performance when parsing into map/list graphs
}}}
See more changes in the project sources
== Improve Svenson == 
I'm always interested in getting new ideas to improve my projects, so
if your requirements are not met by svenson, you could try contacting me to give me a clearer idea of what could be added.

