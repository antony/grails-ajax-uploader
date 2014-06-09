grails.project.class.dir = "target/$grailsVersion/classes"
grails.project.test.class.dir = "target/$grailsVersion/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.release.scm.enabled=false

grails.project.dependency.resolution = {
    inherits "global"
    log "warn"

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.desirableobjects.co.uk"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        test 'org.gmock:gmock:0.8.2', { export = false }
    }
    plugins {

	build ':release:2.2.1', ':rest-client-builder:1.0.3', {
      		export = false
   	}

    }
}
