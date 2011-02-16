package uk.co.desirableobjects.ajaxuploader.exception

class InvalidAttributeValueException extends RuntimeException {

    InvalidAttributeValueException(String attribute, String value, Class expectedClass) {
        super(String.format("Invalid value (%s) for atribute %s. Expected an instance of %s", value, attribute, expectedClass.simpleName))
    }

    InvalidAttributeValueException(String attribute, String value, List allowedValues) {
        super(String.format("Invalid value (%s) for attribute %s. Try one of %s", value, attribute, allowedValues.toListString()))
    }

}
