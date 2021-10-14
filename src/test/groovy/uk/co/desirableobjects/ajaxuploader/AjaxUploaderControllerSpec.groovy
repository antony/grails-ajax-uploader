package uk.co.desirableobjects.ajaxuploader

import grails.testing.web.controllers.ControllerUnitTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import spock.lang.Specification

//import org.gmock.WithGMock

//@WithGMock
class AjaxUploaderControllerSpec extends Specification implements ControllerUnitTest<AjaxUploaderController> {

    private boolean randomTemporaryFileCreated = false
    private boolean preconfiguredTemporaryFileCreated = false
    private File randomFile = new File('xyz', 'my.file')
    private File preconfiguredFile = new File('xyz', 'preconfigured.file')
    private File anyFile = new File('lerp', 'derp')

    def setup() {
//        super.setUp()
        File.metaClass.static.createTempFile = { String prefix, String suffix ->
            randomTemporaryFileCreated = true
            return randomFile
        }
        File.metaClass.constructor = { String fileName ->
            if (fileName.contains('.groovy')) {
                return anyFile
            }
            preconfiguredTemporaryFileCreated = true
            return preconfiguredFile
        }
        mockConfig ''
        controller.ajaxUploaderService = Mock(AjaxUploaderService)
        assert !randomTemporaryFileCreated && !preconfiguredTemporaryFileCreated
    }

    void tearDown() {
        super.tearDown()
        assert !(randomTemporaryFileCreated && preconfiguredTemporaryFileCreated)
        File.metaClass = null
    }

    void testJavaTempDirUsedWhenConfigurationMissing() {
        controller.ajaxUploaderService.upload(null, randomFile)
        play {
            controller.upload()
        }
        assert randomTemporaryFileCreated
    }

    void testConfiguredTemporaryFileUsedWhenConfigurationPresent() {
        mockConfig "imageUpload.temporaryFile = '/tmp/preconfigured.file'"
        controller.ajaxUploaderService.upload(null, preconfiguredFile)
        play {
            controller.upload()
        }
        assert preconfiguredTemporaryFileCreated
    }

    void testInternetExplorerUploadFunctionality() {
        String imgContentType = 'image/jpeg'
        byte[] imgContentBytes = '123' as byte[]
        controller.metaClass.request = new MockMultipartHttpServletRequest()
        controller.request.addFile(
                new MockMultipartFile('qqfile', 'myImage.jpg', imgContentType, imgContentBytes)
        )
        controller.ajaxUploaderService.upload(match { it instanceof ByteArrayInputStream }, randomFile)
        play {
            controller.upload()
        }
        assert randomTemporaryFileCreated
    }

    /**
     void testNonJavascriptUploadIsDetected() {def imgContentType = 'image/jpeg'
     def imgContentBytes = '123' as byte[]

     controller.metaClass.request = new MockMultipartHttpServletRequest()
     controller.request.addFile(
     new MockMultipartFile('image', 'myImage.jpg', imgContentType, imgContentBytes)
     )
     controller.upload()}**/
}