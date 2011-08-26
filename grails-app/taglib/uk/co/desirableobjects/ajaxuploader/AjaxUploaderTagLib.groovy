package uk.co.desirableobjects.ajaxuploader

import uk.co.desirableobjects.ajaxuploader.exception.MissingRequiredAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.UnknownAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.InvalidAttributeValueException

class AjaxUploaderTagLib {

    String currentUploaderUid = null
    StringWriter htmlOut = new StringWriter()
    StringWriter jsOut = new StringWriter()

    static final Map<String, List<String>> REQUIRED_ATTRIBUTES = [
            id: []
    ]

    static final Map<String, List<String>> OPTIONAL_ATTRIBUTES = [
            allowedExtensions: [],
            sizeLimit: [],
            minSizeLimit: [],
            debug: ['true', 'false'],
            params: [],
            messages: [],
            multiple: ['true', 'false'],
            url: []
    ]

    static final Map<String, List<String>> SEPARATELY_HANDLED_ATTRIBUTES = [
            id: [],
            url: [],
            params: []
    ]

    static Map<String, List<String>> ALL_ATTRIBUTES = [:]

    static namespace = 'uploader'

    def head = { attrs, body ->
        out << g.javascript([library:'fileuploader', plugin:'ajax-uploader'], "")
        String cssPath = attrs.css ?: resource(dir:"${pluginContextPath}/css", file:'uploader.css')
        out << '<style type="text/css" media="screen">'
        out << "   @import url( ${cssPath} );"
        out << "</style>"
    }

    def uploader = { attrs, body ->

        ALL_ATTRIBUTES.putAll(REQUIRED_ATTRIBUTES)
        ALL_ATTRIBUTES.putAll(OPTIONAL_ATTRIBUTES)

        validateAttributes(attrs)

        currentUploaderUid = attrs.id

        String url = attrs.url ? createLink(attrs) : resource(dir:'ajaxUpload',file:'upload')

        body()

        out << """
            <div id="au-${currentUploaderUid}">
                <noscript>
                    ${htmlOut}
                </noscript>
            </div>
        """

        out << g.javascript([:], """
            var au_${currentUploaderUid} = new qq.FileUploader({
            element: document.getElementById('au-${currentUploaderUid}'),
            action: '${url}'
        """+

        doAttributes(attrs)+
        doParamsBlock(attrs)+

        jsOut.toString()+

        """
            });
        """)

        reset()
    }

    private def reset() {

        currentUploaderUid = null
        [jsOut, htmlOut]*.buffer*.length = 0
        
    }

    private String doAttributes(Map<String, String> attrs) {
        StringBuffer attributesBlock = new StringBuffer()
        attrs.each { java.util.Map.Entry attribute ->
            if (!(SEPARATELY_HANDLED_ATTRIBUTES.containsKey(attribute.key as String))) {
                attributesBlock.append(""",
                    ${attribute.key as String}: ${attribute.value as String}
                """)
            }
        }
        return attributesBlock
    }

    private String doParamsBlock(Map<String, String> attrs) {

        def parameters = attrs.params

        if (!parameters) {
            return ''
        }

        if (!(parameters instanceof Map) && parameters) {
            throw new InvalidAttributeValueException('params', attrs.params, Map.class)
        }

        StringBuffer paramsBlock = new StringBuffer()

        paramsBlock.append(''',
                params: {''')
        parameters.each { java.util.Map.Entry entry ->
            if (entry.value instanceof String) {
                paramsBlock.append("""${entry.key}: '${entry.value}', """)
            } else {
                paramsBlock.append("""${entry.key}: ${entry.value}, """)
            }
        }
        paramsBlock.append('''
        }''')

        return paramsBlock.toString()
    }

    private validateAttributes(Map<String, String> attributes) {
        REQUIRED_ATTRIBUTES.each { java.util.Map.Entry attribute ->
            if (!attributes.containsKey(attribute.key)) {
                throw new MissingRequiredAttributeException(attribute.key)
            }
        }

        attributes.each { java.util.Map.Entry attribute ->
             if (!(ALL_ATTRIBUTES.containsKey(attribute.key))) {
                throw new UnknownAttributeException(attribute.key)
             } else {
                 validateAttribute(attribute)
             }
        }
    }

    private validateAttribute(java.util.Map.Entry attribute) {
        List allowedValues = ALL_ATTRIBUTES[attribute.key].value as List
        String attributeValue = attribute.value as String
        if (!(allowedValues).isEmpty()) {
            if (!allowedValues.find { value -> value.toString() == attributeValue }) {
                throw new InvalidAttributeValueException(attribute.key, attributeValue, allowedValues)
            }
        }
    }

    def onComplete = { attrs, body ->

        validateCallState()
        jsOut << """,
onComplete: function(id, fileName, responseJSON) { ${body()} }"""
        return ''

    }

    private void validateCallState() {
        if (!currentUploaderUid) {
            throw new IllegalStateException(":callback tags can only be used inside an enclosing :uploader tag.")
        }
    }

    def onSubmit = { attrs, body ->

        validateCallState()
        jsOut << """,
onSubmit: function(id, fileName) { ${body()} }"""
        return ''

    }

    def onProgress = { attrs, body ->

        validateCallState()
        jsOut << """,
onProgress: function(id, fileName, loaded, total) { ${body()} }"""
        return ''

    }

    def onCancel = { attrs, body ->

        validateCallState()
        jsOut << """,
onCancel: function(id, fileName) { ${body()} }"""
        return ''

    }

    def showMessage = { attrs, body ->

        validateCallState()
        jsOut << """,
showMessage: function(message) { ${body()} }"""
        return ''

    }

    def noScript = { attrs, body ->

        htmlOut << "${body()}"
        return ''

    }

}
