package uk.co.desirableobjects.ajaxuploader

import grails.plugins.Plugin

class AjaxUploaderGrailsPlugin extends Plugin {
    def grailsVersion = "4.0.0 > *"
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Ajax Uploader"
    def author = "Antony Jones"
    def authorEmail = "aj@desirableobjects.co.uk"
    def description = '''\
Highly configurable cross-browser, ajax-based file uploader. Displays a progress bar whilst uploading,\
allows restriction by filetype, and multiple file upload.

Based on Andrew Valums' Javascript [ajax file uploader|http://valums.com/ajax-upload/].'''

    def profiles = ['web']
    def documentation = "http://grails.org/plugin/ajax-uploader"
    def license = "ASL"
    def issueManagement = [system: "GitHub", url: "https://github.com/aiten/grails-ajax-uploader/issues"]
    def scm = [url: "https://github.com/aiten/grails-ajax-uploader"]
}