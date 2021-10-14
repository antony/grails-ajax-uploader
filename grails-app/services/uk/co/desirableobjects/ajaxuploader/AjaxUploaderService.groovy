package uk.co.desirableobjects.ajaxuploader

import grails.util.Holders
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploaderService {
    void upload(InputStream inputStream) {
        File uploaded = Holders.config.imageUpload?.containsKey('temporaryFile') ?
                new File("${Holders.config.imageUpload.temporaryFile}") :
                File.createTempFile('grails', 'ajaxupload')
        upload(inputStream, uploaded)
    }

    void upload(InputStream inputStream, File file) {
        try {
            file << inputStream
        } catch (Exception e) {
            throw new FileUploadException(e)
        }
    }
}