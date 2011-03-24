package uk.co.desirableobjects.ajaxuploader


import grails.test.*
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class AjaxUploaderServiceTests extends GrailsUnitTestCase {

    AjaxUploaderService ajaxUploaderService
    private static final byte[] input = "Hello, my name is alf".bytes

    protected void setUp() {
        super.setUp()
        ajaxUploaderService = new AjaxUploaderService()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testUploadFile() {
        File destination = File.createTempFile('abc', 'def')
        destination.deleteOnExit()

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input)

        ajaxUploaderService.upload(inputStream, destination)

        assertEquals new String(input), new String(destination.bytes)
    }

    void testBrokenUpload() {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input)

        shouldFail(FileUploadException.class) {
            ajaxUploaderService.upload(inputStream, null)
        }

    }
}
