package uk.co.desirableobjects.ajaxuploader

import grails.test.ControllerUnitTestCase

class AjaxUploadControllerTests extends ControllerUnitTestCase {

    private boolean randomTemporaryFileCreated = false
    private boolean preconfiguredTemporaryFileCreated = false

    void setUp() {
        super.setUp()

        File.metaClass.static.createTempFile = { String prefix, String suffix ->
            randomTemporaryFileCreated = true
            return new File('xyz', 'my.file')
        }

        File.metaClass.constructor = { String fileName ->
            preconfiguredTemporaryFileCreated = true
            return new File('xyz', 'preconfigured.file')
        }
        
        assert !randomTemporaryFileCreated && !preconfiguredTemporaryFileCreated
    }

    void tearDown() {
        super.tearDown()
        assert !(randomTemporaryFileCreated && preconfiguredTemporaryFileCreated)
    }

    void testJavaTempDirUsedWhenConfigurationMissing() {

        mockConfig ''

        controller.upload()
        assert randomTemporaryFileCreated

    }

    void testConfiguredTemporaryFileUsedWhenConfigurationPresent() {

        mockConfig "imageUpload.temporaryFile = '/tmp/preconfigured.file'"

        controller.upload()
        assert preconfiguredTemporaryFileCreated

    }

}
