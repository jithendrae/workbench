This example is to place rest services as DNS services to represent micro-services
architecture where service discovery is possible by querying the DNS Server.

To demonstrate this, a spring REST example is taken and a BIND DNS Server is installed
and thereupon the services are thusly configured. The configuration, installation and
querying are self explanatory from the attached config files and screenshots mentioned below

1. named.conf --- This configures the BIND Server with functional details
2. zone.localhost --- a zone file describing a localhost
3. localhost.rev.zone --- a zone file describing the reverse lookup details for localhost
4. screenshot.png
5. serviceDiscovery.png --- Services Lifecycle

The following links provide the detailed information on the REST Example and the setting up
of Rest services on DNS Server for service discovery

1. http://drupalmotion.com/article/dev-environment-install-and-configure-bind-dns-server-windows-7

2. http://www.journaldev.com/2552/spring-restful-web-service-example-with-json-jackson-and-client-program

3. http://www.infoq.com/articles/rest-discovery-dns
 
