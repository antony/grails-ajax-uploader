package uk.co.desirableobjects.ajaxuploader

import grails.converters.JSON
import static org.codehaus.groovy.grails.commons.ConfigurationHolder.config as Config

class AjaxUploadController {

    def upload = {
        try {

            File uploaded
            if (Config.imageUpload.containsKey('temporaryFile')) {
              uploaded = new File("${Config.imageUpload.temporaryFile}")
            } else {
              uploaded = File.createTempFile('grails', 'ajaxupload')
            }

            uploaded << request.inputStream

            return render([success:true] as JSON)

        } catch (Exception e) {

            log.error("Failed to upload file.", e)
            return render([success:false] as JSON)

        }

    }

}
