# Finagle Project
The project consist of a prime number server and a http proxy server. It is implemented using the Twitter Finagle Stack version 20.3.0 and Scala 2.12.7.

### Sbt and Scrooge
Sbt version 1.3.9 was chosen as build tool along with the TwitterScrooge Plugin for SBT to auto-generate the server interfaces from thrift.

### Architecture and limitations
The Prime Number Server is build using a thrift protocol based Finagle service utilizing a nonblocking architecture. 
The underlying prime number generator uses a memoization cache for algorithmic efficiency.
The Proxy Service uses a Twitter http server. The http server has set a limitation on the input in the number range of 1 - 1000000.
This is due to the cache size limitations and testing of the algorithm. The number 100000 has been set rather arbitrarily and due to testing time. The algorithm is for sure able to handle larger requests.     

### Tests 
Unit tests were applied using Scala Test 3.0.8 to test the Prime Number Generator. Finatra Test Framework 20.3.0 was used to feature test the Http Server.  

### How to
- Download the sources.
- Generate the thrift sources using the scroogeGen sbt task
- Compile the project
- Run com.prime.proxy.RunProxyHttpServer and com.prime.server.RunPrimeServer 


