package uk.co.desirableobjects.ajaxuploader

import grails.test.*
import org.junit.Test
import org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib
import uk.co.desirableobjects.ajaxuploader.exception.MissingRequiredAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.UnknownAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.InvalidAttributeValueException
import java.util.regex.Matcher

class AjaxUploaderTagLibTests extends TagLibUnitTestCase {

    static final String uploaderUid = 'testAjaxUploader'
    static final Map EXAMPLE_PARAMETERS = [myKey: 'myValue', myOtherKey: 5]
    static final String DUMMY_PLUGIN_CONTEXT_PATH = 'plugins/ajax-uploader-1.0'
    static final String DUMMY_CALLBACK = "alert(filename+' yadda yadda')"
    static final Closure BLANK_TAG_BODY = { return "" }

    protected void setUp() {
        super.setUp()
        // TODO: If grails taglib testing wasn't so utterly obscure, this could be better.
        AjaxUploaderTagLib.metaClass.createLink = { attrs -> return "/file/upload" }
        AjaxUploaderTagLib.metaClass.javascript = { attrs, body ->
            if (attrs.library) {
                return """<script type="text/javascript" src="${attrs.plugin}/js/fileuploader.js"></script>"""
            }
            return """<script type="text/javascript">${body}</script>"""
        }
        AjaxUploaderTagLib.metaClass.resource = { attrs -> return "/${attrs.dir}/${attrs.file}" }
        AjaxUploaderTagLib.metaClass.pluginContextPath = DUMMY_PLUGIN_CONTEXT_PATH

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCssInclude() {

        tagLib.head([:], BLANK_TAG_BODY)

        assertContains '<style type="text/css" media="screen">'
        assertContains "@import url( /${DUMMY_PLUGIN_CONTEXT_PATH}/css/uploader.css );"
        assertContains '</style>'

    }

    void testJsInclude() {

         tagLib.head([:], BLANK_TAG_BODY)

         assertContains """<script type="text/javascript" src="ajax-uploader/js/fileuploader.js">"""

    }

    void testExcludeCss() {

        tagLib.head([css:'/myapp/mycss.css'], BLANK_TAG_BODY)

        assertDoesNotContain '@import url( /css/uploader.css )'
        assertContains '@import url( /myapp/mycss.css )'

    }

    void testTagRequiresId() {

       shouldFail(MissingRequiredAttributeException.class) {
            tagLib.uploader([:], "")
       }

    }

    void testOutputsBasicUploaderContainer() {

        tagLib.uploader([id:uploaderUid, url:[]], BLANK_TAG_BODY)

        assertContains """<div id="au-${uploaderUid}">"""

    }

    void testCreatesJavascriptFileUploader() {

        tagLib.uploader([id:uploaderUid, url:[controller:'file', action:'upload']], BLANK_TAG_BODY)

        assertContains '<script type="text/javascript">'
        assertContains 'var au_testAjaxUploader = new qq.FileUploader({'
        assertContains "element: document.getElementById('au-testAjaxUploader')"
        assertContains "action: '/file/upload'"
        assertContains '</script>'

    }

    void testMissingUrlParameterUsesDefaultController() {

        tagLib.uploader([id:uploaderUid], BLANK_TAG_BODY)

        assertContains "action: '/ajaxUpload/upload'"

    }

    void testValidAttributeValue() {

        tagLib.uploader([id:uploaderUid, url:[], debug:'false'], BLANK_TAG_BODY)

        assertContains "debug: false"

    }

    void testInvalidAttributeValue() {

        shouldFail(InvalidAttributeValueException.class) {
            tagLib.uploader([id:uploaderUid, url:[], debug:'unfail'], BLANK_TAG_BODY)
        }

    }

    void testUnknownAttribute() {

        shouldFail(UnknownAttributeException.class) {
            tagLib.uploader([id:uploaderUid, url:[], notknown:'anyvalue'], BLANK_TAG_BODY)
        }

    }

    void testInvalidParamsBlock() {

        shouldFail(InvalidAttributeValueException.class) {
            tagLib.uploader([id:uploaderUid, url:[], params:"invalid"], BLANK_TAG_BODY)
        }

    }

    void testParamsBlock() {

        tagLib.uploader([id:uploaderUid, url:[], params:EXAMPLE_PARAMETERS], BLANK_TAG_BODY)

        assertContains '''myKey: 'myValue', myOtherKey: 5'''

    }

    void testNoParamsMeansNoParamsBlock() {

        tagLib.uploader([id:uploaderUid, url:[]], BLANK_TAG_BODY)

        assertDoesNotContain 'params:'

    }

    void testSeparatelyHandledAttributes() {

        tagLib.uploader([id:uploaderUid, url:[], params:EXAMPLE_PARAMETERS], BLANK_TAG_BODY)

        assertDoesNotContain "[myKey: myValue, myOtherKey: 5]"
        assertDoesNotContain "id:testAjaxUploader"
        assertDoesNotContain "url:[]"

    }

    void testOnCompleteCallback() {

        String onCompleteFunction = "alert(filename+' is complete')"

        tagLib.uploader([id:uploaderUid],
                { return tagLib.onComplete([:], { return onCompleteFunction }) }
        )

        assertContains "onComplete: function(id, fileName, responseJSON) { ${onCompleteFunction} }"

    }

    void testOnSubmitCallback() {

        String onSubmitFunction = "alert(filename+' is submitted')"

        tagLib.uploader([id:uploaderUid],
                { return tagLib.onSubmit([:], { return onSubmitFunction }) }
        )

        assertContains "onSubmit: function(id, fileName) { ${onSubmitFunction} }"

    }

    void testOnProgressCallback() {

        tagLib.uploader([id:uploaderUid],
                { return tagLib.onProgress([:], { return DUMMY_CALLBACK }) }
        )

        assertContains "onProgress: function(id, fileName, loaded, total) { ${DUMMY_CALLBACK} }"

    }

    void testOnCancelCallback() {

        tagLib.uploader([id:uploaderUid],
                { return tagLib.onCancel([:], { return DUMMY_CALLBACK }) }
        )

        assertContains "onCancel: function(id, fileName) { ${DUMMY_CALLBACK} }"

    }

    void testShowMessage() {

        tagLib.uploader([id:uploaderUid],
                { return tagLib.showMessage([:], { return DUMMY_CALLBACK }) }
        )

        assertContains "showMessage: function(message) { ${DUMMY_CALLBACK} }"

    }

    void testCallbackTagWithoutEnclosingUploader() {

        shouldFail(IllegalStateException.class) {
            return tagLib.showMessage([:], { return DUMMY_CALLBACK })
        }

    }

    void testEnsureStateIsResetAfterTag() {

        testOnSubmitCallback()
        testCallbackTagWithoutEnclosingUploader()

    }

    void testNoScriptBlock() {

        final String NOSCRIPT_BLOCK = "<h1>No JS!?</h1>"

        tagLib.uploader([id:uploaderUid],
                { return tagLib.noScript([:], { return NOSCRIPT_BLOCK }) }
        )

        assertContains """<noscript>
                    ${NOSCRIPT_BLOCK}
                </noscript>"""

    }

    void testEnsureCallbacksAppearOnlyOnce() {

        String onCompleteFunction = "alert(filename+' is complete')"

        2.times {
            tagLib.uploader([id:uploaderUid],
                    { return tagLib.onComplete([:], { return onCompleteFunction }) }
            )
        }

        Matcher matcher = tagLib.out.toString() =~ "onComplete: "
        assert matcher.size() == 2

    }

    private assertContains(String expected) {
        assert tagLib.out.toString().contains(expected)
    }

    private assertDoesNotContain(String unexpected) {
        assert !tagLib.out.toString().contains(unexpected)
    }
}
