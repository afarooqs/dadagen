# DadaGen - Random Data Generator
![alt tag](https://raw.github.com/inosion/dadagen/master/assets/dadagen-logo.jpg)
## What ? 

A Random Data Generator, with examples and templates to use in a variety of scenarios.

## History

This is the second iteration of a random data library that I have written.
The first, somewhat "pushed and left" version is http://random-data-generator.googlecode.com/

This is a much more feature rich Scala version. Soon I will have the same features as the old.

# So what can it do ?

## Gatling Feeder Support

Feeders in Gatling are the method of providing "data" as an iterator, (loading from a CSV for example).
This of course is very easy for dadagen.

First, import the dadagen Scala DSL.

```scala
import org.inosion.dadagen.api.scaladsl._
```
    
Next, create your dadgen defintion (what types of random data you want).

    val feeder = dadagen asMaps {
        field { "id".rownumber }.
        field { "gender".gender }.
        field { "firstname".name firstname }.
        field { "surname".name surname }.
         // Combine all the values together .. order (what it depends on) does not matter
        field { "message".template("${id} - ${firstname} ${surname} (${gender}) i:${int} ${ref}")}.
        field { "int".number between 10 and 99876 }.
        field { "ref".regexgen("[a-f]{6}-[0-9a-f]{8}") }
    } generate() // call generate to make the Iterator

Then use the Feeder in your script setup.

    val scn = scenario("scenario1").feed(feeder).exec(http(....))

Each "field" name will be a session attribute that you can use.

     // this would return the "message" which above, is defined as a Template. 
     session.attributes.get("message")

If you need to have a "limited" or restricted set of data, you can use the method generateAll

    dadagen asMaps { ... } generateAll(100)

instead to generate 100 Map[String,String] entries.

## JMeter Data Provider

Stay Tuned, a JMeter Plugin is coming that will allow you to use the dadagen configuration in place of a CSV Reader.

## Native Scala Class Creation

Stay Tuned, a native "Class" generator is coming where you specify the class and it will create new instances of it.
Matching each field that it finds to a predefined (convention over configuration) set of named field generators.
(this design comes from the older project http://random-data-generator.googlecode.com/
    
    datagen.object(classOf[ClassName]).generate()

## Native Scala Case Class Creation

As above, we will also auto generate Case Classes.

## Generate a CSV

You can create a CSV with the following snippet of code (using Jackson for CSV generation), or your favourite other CSV generator).

    import org.inosion.dadagen.api.scaladsl._

    // note that "col" is the same/synonomous to "field"
    val generator = dadagen asLists {
          col { "id".rownumber }.
          col { "col title".name title }.
          col { "firstname".name firstname }.
          col { "surname".name surname }.
          col { "int".number between 10 and 1001 }.
          col { "money".number between 1.0 and 10 }.
          col { "gender". gender }.
          col { "random-string".regexgen ("""TEsting [0-9] [a-zA-z_';:"\[\]]{5}""")  }.
          col { "addr_street_line".address street }.
          col { "addr_suburb" .address suburb }.
          col { "addr_city".   address city }.
          col { "addr_district". address district }.
          col { "addr_postcode". address postcode }.
          //col { "list". listFrom ("This row is ${firstname} ${id}") },
          col { "template". template ("This row is ${firstname} ${id}") }
    }

    val header = generator.fieldNames
    val arrayData = generator.generateAll(5)

    // Create a new Jackson CSV Mapper
    val mapper = new CsvMapper()
    val csvSchema:CsvSchema = CsvSchema.emptySchema()
      .withoutHeader()
      .withQuoteChar('"')
      .withColumnSeparator(',')
      .withLineSeparator("\n")
    
    val writer:ObjectWriter = mapper.writer(csvSchema)

    // Jackson needs an "Array"
    print(writer.writeValueAsString(header.toArray)  ) // column names - first (Header) row
    print(writer.writeValueAsString(arrayData.map(_.toArray).toArray)  ) // the data

# Scala DSL and the Generators

A Small note about the DSL. 
The keyword "col" or "field" is just syntatic sugar to accept a Generator and concatenating them into a list.
The Generators are the heart of dadagen.

    col { "fieldname" name firstname } 

is equivalent to 

    col { FirstNameGenerator(fieldName) }
    
So you can write your own generator and drop it in place
    
    col { MyCustomGenerator(someFieldName) }

Just extend 

    DataGenerator[ T ]


# Context / Binding

dadagen has a clever technique of "binding" values on one field, to values in another.
For example, if you include "gender" with "firstname", then dadagen knows to only supply Female names when the Gender is female
and similarly, Male names when the Gender is Male.

Future use will be around Country binding (only provide UK Tax File Numbers (NINO) when the Country is UK, USA when USA etc).
In addition, Country binding can be used in Address Styles etc.. (so that is to come.. infrastructure is there)

# Data Types

You will have noticed, by looking at the above examples that there is a lot of "sample" data embedded.
This is the core idea of dadagen, that we can provide all the data you need. 

Each embedded data type is supported by a simple Case Class Generator. So you can both write your own generator
and mix and match other ones to your hearts content.

The lists are defined in the dadgen config file (src/main/resources/reference.conf)

The lists can be used arbitrarily, outside of a defined "Configured Generator" and you can even add you own lists by extending the Typesafe Config.

The current lists defined are

!inc(src/main/resources/reference.conf)

## Person Data

### Gender

Male or Female (M or F)
DSL: gender
Scala: GenderGenerator(name,style)
style can be "word" or "char"; M/F or Male/Female

### Name - First Name

An Anglo Saxon Name drawn from a list of 2000, 4000 or 10000 (default is 2000)
DSL: name firstname
Scala: FirstNameGenerator(name,genderFieldName,listName)
genderFieldName = "will default to gender"
listName (a keyed name of the "list" of names to use) 

## Data Lists

A lot of the Data Generators use a custom "list" of data to provide real test data. For example a list of firstnames is available.

## Gender

The GenderGenerator(colName) will create a string either "M" or "F".
You can optionally


### And More

Running out of time right now to comment them all. So instead, look at org.inosion.dadagen.randomtypes for the current configured ones. 

# Suggestions?

It is VERY Alpha at the moment but more is coming. Drop me a line or raise an issue and I will attend to it.

Regards
Ramon
