package uk.co.desirableobjects.ajaxuploader

import grails.converters.JSON
import static org.codehaus.groovy.grails.commons.ConfigurationHolder.config as Config
import org.springframework.http.HttpStatus
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploadController {

    AjaxUploaderService ajaxUploaderService

    def upload = {
        try {

            File uploaded = createTemporaryFile()

            ajaxUploaderService.upload(request.inputStream, uploaded)

            return render([success:true] as JSON)

        } catch (FileUploadException e) {

            log.error("Failed to upload file.", e)
            return render(text: [success:false] as JSON)

        }

    }

    private File createTemporaryFile() {
        File uploaded
        if (Config.imageUpload.containsKey('temporaryFile')) {
            uploaded = new File("${Config.imageUpload.temporaryFile}")
        } else {
            uploaded = File.createTempFile('grails', 'ajaxupload')
        }
        return uploaded
    }

}
