package uk.co.desirableobjects.ajaxuploader

import grails.converters.JSON
import grails.web.mime.MimeType
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploaderController {

    AjaxUploaderService ajaxUploaderService

    def upload = {
        Map status = [:]
        try {
            InputStream inputStream = request instanceof MultipartHttpServletRequest ?
                    request.getFile('qqfile').inputStream :
                    request.inputStream
            ajaxUploaderService.upload(inputStream)
            status = [success: true]
        } catch (FileUploadException e) {
            log.error("Failed to upload file.", e)
            status = [success: false]
        }
        render(text: status as JSON, contentType: MimeType.TEXT_JSON.name)
    }
}