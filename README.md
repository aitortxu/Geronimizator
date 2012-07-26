Geronimizator
=============

(Handle with care, this is a ultraalpha version)

Simple Grails plugin to generate some files in order to deploy in a Geronimo (IBM WAS CE) server. It generates a geronimo-web.xml file in the ./web-app/WEB-INF directory and a repository directory to copy into your Geronimo directory, in order to user it in the deploy plan. 

You can also use the --withModule option not to put the dependencies with the Grails version, if you have your server organized that way, with a module for every Grails version.

You needs to declare some variables in your BuildConfig.groovy:

* deploy.host => host name where your application is being deploying.

Only need if you're using a jndi datasource:

* deploy.datasource.refname => ref name of your datasource
* deploy.datasource.resourcelink=> resource link or your datasource. This one must match your datasource jndiname, so be carful, otherwise would not work. 