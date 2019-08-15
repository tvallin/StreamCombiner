# StreamCombiner

Stream Combiner is an application that allows you to create a combined stream. This stream combines / merges entries from all (original) individual streams.

# Description (15/08/2019)

Main task : Simulate N server which send xml stream, treat the data, and convert them into JSON format

About Server/Client behavior:

  * All server are created from the main Thread but they run on a separated thread
  * The Client request is also treated on a different thread
  * The Client send request from a unique thread
  * XML data is stored in a local list for each Client
  * The main thread is waiting for all Client to finish their work and gather xml into a unique list in the main thread
  
About XML treatment:

XML format: ```xml <data> <timeStamp>123456789</timeStamp> <amount>1234.567890</amount> </data> ```
  
  * The XML data is converted to POJO (Data class)
  * For POJO with same timeStamp, the amount is merged
  * The POJOs are sorted by timeStamp
  * POJOs are converted to JSON format : ``` { "data": { "timestamp":123456789, "amount":"1234.567890" }}```
  
