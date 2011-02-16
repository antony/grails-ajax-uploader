package uk.co.desirableobjects.ajaxuploader

import grails.test.*
import org.junit.Test
import org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib
import uk.co.desirableobjects.ajaxuploader.exception.MissingRequiredAttributeException
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import uk.co.desirableobjects.ajaxuploader.exception.UnknownAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.InvalidAttributeValueException

class AjaxUploaderTagLibTests extends TagLibUnitTestCase {

    static final String uploaderUid = 'testAjaxUploader'
    static final Map EXAMPLE_PARAMETERS = [myKey: 'myValue', myOtherKey: 5]

    protected void setUp() {
        super.setUp()
        // TODO: If grails taglib testing wasn't so utterly obscure, this could be better.
        AjaxUploaderTagLib.metaClass.createLink = { attrs -> return "/file/upload" }
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTagRequiresId() {

       shouldFail(MissingRequiredAttributeException.class) {
            tagLib.uploader([:], "")
       }

    }

    void testOutputsBasicUploaderContainer() {

        tagLib.uploader([id:uploaderUid, url:[]], "")

        assertContains """<div id="au-${uploaderUid}">"""

    }

    void testCreatesJavascriptFileUploader() {

        tagLib.uploader([id:uploaderUid, url:[controller:'file', action:'upload']], "")

        assertContains 'var au_testAjaxUploader = new qq.FileUploader({'
        assertContains "element: document.getElementById('au-testAjaxUploader')"
        assertContains "action: '/file/upload'"

    }

    void testValidAttributeValue() {

        tagLib.uploader([id:uploaderUid, url:[], debug:'false'], "")

        assertContains "debug: false"

    }

    void testInvalidAttributeValue() {

        shouldFail(InvalidAttributeValueException.class) {
            tagLib.uploader([id:uploaderUid, url:[], debug:'unfail'], "")
        }

    }

    void testUnknownAttribute() {

        shouldFail(UnknownAttributeException.class) {
            tagLib.uploader([id:uploaderUid, url:[], notknown:'anyvalue'], "")
        }

    }

    void testInvalidParamsBlock() {

        shouldFail(InvalidAttributeValueException.class) {
            tagLib.uploader([id:uploaderUid, url:[], params:"invalid"], "")
        }

    }

    void testParamsBlock() {

        tagLib.uploader([id:uploaderUid, url:[], params:EXAMPLE_PARAMETERS], "")

        assertContains '''myKey: 'myValue', myOtherKey: 5'''

    }

    void testSeparatelyHandledAttributes() {

        tagLib.uploader([id:uploaderUid, url:[], params:EXAMPLE_PARAMETERS], "")

        assertDoesNotContain "[myKey: myValue, myOtherKey: 5]"
        assertDoesNotContain "id:testAjaxUploader"
        assertDoesNotContain "url:[]"

    }

    private assertContains(String expected) {

        println tagLib.out.toString()
        assertTrue tagLib.out.toString().contains(expected)

    }

    private assertDoesNotContain(String unexpected) {
        println tagLib.out.toString()
        assertFalse tagLib.out.toString().contains(unexpected)
    }
}
