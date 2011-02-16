package uk.co.desirableobjects.ajaxuploader

import uk.co.desirableobjects.ajaxuploader.exception.MissingRequiredAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.UnknownAttributeException
import uk.co.desirableobjects.ajaxuploader.exception.InvalidAttributeValueException

class AjaxUploaderTagLib {

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
        out << g.javascript([library:'fileuploader', plugin:'grails-image-upload'])
        if (attrs.exclude) {
            if (attrs.exclude.toString()!='css') {
                throw new InvalidAttributeValueException('exclude', attrs.exclude, ['css'])
            }
        } else {
            out << '<style type="text/css" media="screen">'
            out << "   @import url( ${resource(dir:'css', file:'uploader.css')} );"
            out << "</style>"
        }
    }

    def uploader = { attrs, body ->

        ALL_ATTRIBUTES.putAll(REQUIRED_ATTRIBUTES)
        ALL_ATTRIBUTES.putAll(OPTIONAL_ATTRIBUTES)

        validateAttributes(attrs)

        String uploaderUid = attrs.id

        out << """
            <div id="au-${uploaderUid}">
                <noscript>
                    <p>Please enable JavaScript to use file uploader.</p>
                </noscript>
            </div>
        """

        out << """
            var au_${uploaderUid} = new qq.FileUploader({
            element: document.getElementById('au-${uploaderUid}'),
        """

        String url = attrs.url ? createLink(attrs) : '/ajaxUpload/upload'
        out << """
            action: '${url}'
        """

        out << doAttributes(attrs)
        out << doParamsBlock(attrs)

        out << """
            });
        """
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

}
