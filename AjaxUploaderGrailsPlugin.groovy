class AjaxUploaderGrailsPlugin {
    // the plugin version
    def version = "1.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.6 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Antony Jones"
    def authorEmail = "aj@desirableobjects.co.uk"
    def title = "Ajax Uploader Plugin"
    def description = '''\
Highly configurable cross-browser, ajax-based file uploader. Displays a progress bar whilst uploading,\
allows restriction by filetype, and multiple file upload.

Based on Andrew Valums' Javascript [ajax file uploader|http://valums.com/ajax-upload/].'''

    def license = "ASL"
    def documentation = "http://grails.org/plugin/ajax-uploader"
    def issueManagement = [system: "GitHub", url: "https://github.com/aiten/grails-ajax-uploader/issues"]
    def scm = [url: "https://github.com/aiten/grails-ajax-uploader"]
}
