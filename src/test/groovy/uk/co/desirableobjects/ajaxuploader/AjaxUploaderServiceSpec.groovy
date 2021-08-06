package uk.co.desirableobjects.ajaxuploader

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import uk.co.desirableobjects.ajaxuploader.AjaxUploaderService
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploaderServiceSpec extends Specification implements ServiceUnitTest<AjaxUploaderService> {

    private static final byte[] input = "Hello, my name is alf".bytes

    void testUploadFile() {
        File destination = File.createTempFile('abc', 'def')
        destination.deleteOnExit()
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input)
        service.upload(inputStream, destination)
        assertEquals new String(input), new String(destination.bytes)
    }

    void testBrokenUpload() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input)
        shouldFail(FileUploadException.class) {
            service.upload(inputStream, null)
        }
    }
}