package grails.ajax.uploader

import grails.converters.JSON
import static org.codehaus.groovy.grails.commons.ConfigurationHolder.config as Config

class AjaxUploadController {

    def upload = {

        try {
            File uploaded = new File("${Config.imageUpload.temporaryFile}")
            uploaded << request.inputStream

            return render([success:true] as JSON)

        } catch (Exception e) {

            return render([success:false] as JSON)

        }

    }

}
