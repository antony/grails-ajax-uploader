package uk.co.desirableobjects.ajaxuploader

import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploaderService {

    static transactional = true

    void upload(InputStream inputStream, File file) {

        try {
            file << inputStream
        } catch (Exception e) {
            throw new FileUploadException(e)
        }

    }
}
