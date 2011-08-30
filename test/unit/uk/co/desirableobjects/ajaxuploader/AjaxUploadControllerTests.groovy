package uk.co.desirableobjects.ajaxuploader

import grails.test.ControllerUnitTestCase
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import javax.servlet.http.HttpServletResponse
import org.gmock.WithGMock
import java.lang.reflect.Method
import org.codehaus.groovy.runtime.MethodClosure
import org.springframework.web.multipart.MultipartHttpServletRequest

@WithGMock
class AjaxUploadControllerTests extends ControllerUnitTestCase {

    private boolean randomTemporaryFileCreated = false
    private boolean preconfiguredTemporaryFileCreated = false
    private File randomFile = new File('xyz', 'my.file')
    private File preconfiguredFile = new File('xyz', 'preconfigured.file')
    private File anyFile = new File('lerp', 'derp')

    void setUp() {
        super.setUp()

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
        AjaxUploaderService ajaxUploaderServiceMock = mock(AjaxUploaderService)
        controller.ajaxUploaderService = ajaxUploaderServiceMock
        
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
    void testNonJavascriptUploadIsDetected() {

        def imgContentType = 'image/jpeg'
        def imgContentBytes = '123' as byte[]

        controller.metaClass.request = new MockMultipartHttpServletRequest()
        controller.request.addFile(
            new MockMultipartFile('image', 'myImage.jpg', imgContentType, imgContentBytes)
        )

        controller.upload()

    }
 **/

}
