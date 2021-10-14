package uk.co.desirableobjects.ajaxuploader.exception

class UnknownAttributeException extends RuntimeException {

    UnknownAttributeException(String attribute) {

        super(String.format("Unknown attribute %s", attribute))
    }

}
