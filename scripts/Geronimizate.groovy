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
		<dep:version>${grailsVersion}</dep:version>
		<dep:type>car</dep:type>
	</dep:moduleId>
 <dep:dependencies>
""")

    def dependencies = grailsSettings.runtimeDependencies
    Integer countDependencies = 0
    dependencies.each { dependency ->
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
		println "Copiando ${dependency}  a ${path}" 
		ant.copy(file: dependency, tofile: path +  "/" + dependency.name)


    	
    }
    println "Total dependencies: ${countDependencies} para la aplicación ${grailsAppName}"

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

	<dep:gbean name="d03" class="org.apache.geronimo.system.sharedlib.SharedLib">
        	<dep:attribute name="classesDirs">/SRV/website/intra/conf,/SRV/website/genapp/conf</dep:attribute>
     		<dep:reference name="ServerInfo">
       			<dep:name>ServerInfo</dep:name>
     		</dep:reference>
	</dep:gbean> 
</web-app>
""")
}

setDefaultTarget(main)
