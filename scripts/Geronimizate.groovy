includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsSettings")

target(main: "Generate the deploy file and repository") {

	File geronimoWebXML = new File("./target/geronimo-web.xml")
	new File("./repository").mkdir()  
	geronimoWebXML.write("")
	geronimoWebXML.append ("""
<?xml version="1.0" encoding="UTF-8"?>

<web-app xmlns="http://geronimo.apache.org/xml/ns/j2ee/web-2.0"
xmlns:dep="http://geronimo.apache.org/xml/ns/deployment-1.2"
xmlns:naming="http://geronimo.apache.org/xml/ns/naming-1.2"
xmlns:security="http://geronimo.apache.org/xml/ns/security-1.2">

<dep:environment>
	<dep:moduleId>
		<dep:groupId>${grailsAppName}</dep:groupId>
		<dep:artifactId>${grailsAppName}</dep:artifactId>
		<dep:version>${grailsAppVersion}</dep:version>
		<dep:type>car</dep:type>
	</dep:moduleId>
 <dep:dependencies>
""")

    def dependencies = grailsSettings.runtimeDependencies
    Integer countDependencies = 0
    dependencies.each { dependency ->
    	if (isDependencyOutOfModule(dependency)){
    		countDependencies++
    		def dependenciesSplit = dependency.toString().split(File.separator)
	    	String groupId = dependenciesSplit[-4]
	    	String artifactId = dependenciesSplit[-3]
	    	String jar = dependenciesSplit[-1] 
	    	def matcher = jar =~ /(\d+\.)+\d+/
	    	String version = matcher[0][0]
	    	println dependency
	    	geronimoWebXML.append  ("""
	    		<dependency> 
	                <groupId>$groupId</groupId>
	                <artifactId>$artifactId</artifactId>
	                <version>$version</version>
	                <type>jar</type>
	            </dependency>\n""")

			def path = "./target" +"/repository/${groupId}/${artifactId}/${version}"
			new File(path).mkdirs() 
			grailsConsole.updateStatus "Copiando ${dependency}  a ${path}" 
			ant.copy(file: dependency, tofile: path +  "/" + dependency.name)
    	}
    	


    	
    }
    grailsConsole.updateStatus "Total dependencies: ${countDependencies} para la aplicación ${grailsAppName}"
    //grailsConsole.updateStatus "parámetros${argsMap}"

    geronimoWebXML.append  ("""
		</dep:dependencies>
		
		<dep:hidden-classes>
			<dep:filter>groovy.</dep:filter>
			<dep:filter>groovyjarjarasm.</dep:filter>
			<dep:filter>groovyjarjarantlr.</dep:filter>
			<dep:filter>org.codehaus.groovy.</dep:filter>
		</dep:hidden-classes>

		<dep:non-overridable-classes>
			<dep:filter>javax.transaction.</dep:filter>
			<dep:filter>org.slf4j.</dep:filter>
			<dep:filter>net.sf.cglib.</dep:filter>
		</dep:non-overridable-classes>

	</dep:environment>
        <host>${grailsSettings.config.deploy.hostname}</host>
	<resource-ref>
		<ref-name>${grailsSettings.config.deploy.datasource.refname}</ref-name>
		<resource-link>${grailsSettings.config.deploy.datasource.resourcelink}</resource-link>
	</resource-ref>

	<dep:gbean name="${grailsAppName}" class="org.apache.geronimo.system.sharedlib.SharedLib">
        	<dep:attribute name="classesDirs">/SRV/website/intra/conf,/SRV/website/genapp/conf</dep:attribute>
     		<dep:reference name="ServerInfo">
       			<dep:name>ServerInfo</dep:name>
     		</dep:reference>
	</dep:gbean> 
</web-app>
""")
}

setDefaultTarget(main)

boolean isDependencyOutOfModule(File dependency){
	return (argsMap.withModule && !(dependency.name in getJars210()))
}
def getJars210(){
	def jar210 = ["antlr-2.7.7.jar",
		"aopalliance-1.0.jar",
		"asm-3.1.jar",
		"aspectjrt-1.6.10.jar",
		"aspectjweaver-1.6.10.jar",
		"cglib-2.2.jar",
		"commons-beanutils-1.8.3.jar",
		"commons-codec-1.5.jar",
		"commons-collections-3.2.1.jar",
		"commons-dbcp-1.4.jar",
		"commons-el-1.0.jar",
		"commons-fileupload-1.2.2.jar",
		"commons-io-2.1.jar",
		"commons-lang-2.6.jar",
		"commons-pool-1.5.6.jar",
		"commons-validator-1.3.1.jar",
		"concurrentlinkedhashmap-lru-1.2_jdk5.jar",
		"dom4j-1.6.1.jar",
		"ehcache-core-2.4.6.jar",
		"grails-bootstrap-2.1.0.jar",
		"grails-core-2.1.0.jar",
		"grails-crud-2.1.0.jar",
		"grails-datastore-core-1.0.9.RELEASE.jar",
		"grails-datastore-gorm-1.0.9.RELEASE.jar",
		"grails-datastore-simple-1.0.9.RELEASE.jar",
		"grails-hibernate-2.1.0.jar",
		"grails-logging-2.1.0.jar",
		"grails-plugin-codecs-2.1.0.jar",
		"grails-plugin-controllers-2.1.0.jar",
		"grails-plugin-converters-2.1.0.jar",
		"grails-plugin-datasource-2.1.0.jar",
		"grails-plugin-domain-class-2.1.0.jar",
		"grails-plugin-filters-2.1.0.jar",
		"grails-plugin-gsp-2.1.0.jar",
		"grails-plugin-i18n-2.1.0.jar",
		"grails-plugin-log4j-2.1.0.jar",
		"grails-plugin-mimetypes-2.1.0.jar",
		"grails-plugin-scaffolding-2.1.0.jar",
		"grails-plugin-services-2.1.0.jar",
		"grails-plugin-servlets-2.1.0.jar",
		"grails-plugin-url-mappings-2.1.0.jar",
		"grails-plugin-validation-2.1.0.jar",
		"grails-resources-2.1.0.jar",
		"grails-spring-2.1.0.jar",
		"grails-web-2.1.0.jar",
		"groovy-all-1.8.6.jar",
		"h2-1.3.164.jar",
		"hibernate-commons-annotations-3.2.0.Final.jar",
		"hibernate-core-3.6.10.Final.jar",
		"hibernate-ehcache-3.6.10.Final.jar",
		"hibernate-jpa-2.0-api-1.0.1.Final.jar",
		"hibernate-validator-4.1.0.Final.jar",
		"javassist-3.12.0.GA.jar",
		"jcl-over-slf4j-1.6.2.jar",
		"jstl-1.1.2.jar",
		"jta-1.1.jar",
		"jul-to-slf4j-1.6.2.jar",
		"liquibase-core-2.0.5.jar",
		"log4j-1.2.16.jar",
		"oro-2.0.8.jar",
		"sitemesh-2.4.jar",
		"slf4j-api-1.6.2.jar",
		"spring-aop-3.1.0.RELEASE.jar",
		"spring-asm-3.1.0.RELEASE.jar",
		"spring-aspects-3.1.0.RELEASE.jar",
		"spring-beans-3.1.0.RELEASE.jar",
		"spring-context-3.1.0.RELEASE.jar",
		"spring-context-support-3.1.0.RELEASE.jar",
		"spring-core-3.1.0.RELEASE.jar",
		"spring-expression-3.1.0.RELEASE.jar",
		"spring-jdbc-3.1.0.RELEASE.jar",
		"spring-jms-3.1.0.RELEASE.jar",
		"spring-orm-3.1.0.RELEASE.jar",
		"spring-tx-3.1.0.RELEASE.jar",
		"spring-web-3.1.0.RELEASE.jar",
		"spring-webmvc-3.1.0.RELEASE.jar",
		"validation-api-1.0.0.GA.jar",
		"xpp3_min-1.1.4c.jar"]
	return jar210
}
