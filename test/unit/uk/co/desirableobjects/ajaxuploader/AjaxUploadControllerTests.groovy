package uk.co.desirableobjects.ajaxuploader

import grails.test.ControllerUnitTestCase

class AjaxUploadControllerTests extends ControllerUnitTestCase {

    void testJavaTempDirUsedWhenConfigurationMissing() {

        mockConfig ''
        File tempFile = new File().createTemporaryFile('x', 'y')

        String intendedPath = tempFile.absolutePath

        tempFile.delete()

        assert !new File(intendedPath).exists()

        File.metaClass.createTemporaryFile { String prefix, String suffix -> return new File(intendedPath) }

        controller.upload()

        assert new File(intendedPath).exists()

    }

}
