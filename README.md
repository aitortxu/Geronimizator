Geronimizator
=============

Simple Grails plugin to generate some files in order to deploy in a Geronimo (IBM WAS CE) server.

You needs to declare some variables in your BuildConfig.groovy:

* deploy.host => host name where your application is being deploying.

Only need if you're using a jndi datasource:

* deploy.datasource.refname => ref name of your datasource
* deploy.datasource.resourcelink=> resource link or your datasource. This one must match your datasource jndiname. 