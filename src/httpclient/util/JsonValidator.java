package httpclient.util;

/**
 * An utility class for validating a json content. This class uses state charts defined at
 * <a href="https://www.json.org/json-en.html">https://www.json.org/json-en.html</a>
 */
public class JsonValidator {
    /**
     * Validates a json content.
     *
     * @param jsonContent json contend for validation
     * @return {@code true} if input json is valid, {@code false} otherwise
     */
    public static boolean isValidJson(String jsonContent) {
        //a content is json, if meets a json object or a json array
        return meetObject(jsonContent) == jsonContent.length() || meetArray(jsonContent) == jsonContent.length();
    }

    /**
     * Gets a content as input and meets a json array structure using
     * <a href="https://www.json.org/img/array.png">https://www.json.org/img/array.png</a> state machine and returns
     * number of character that it consumed to meet the json array. If the structure does not meet a json array, it
     * returns -1.
     *
     * @param jsonContent content that can start with a json array
     * @return number of consumed characters to meet the json array or -1 if the structure does not meet a json array
     */
    private static int meetArray(String jsonContent) {
        if (jsonContent.isEmpty()) return -1;
        String content = jsonContent;
        if (content.charAt(0) == '[') {
            content = content.substring(1);
            int index = 1;

            int meetResult = meetWhitespace(content);
            if (meetResult < 0) {
                return -1;
            }
            index += meetResult;
            content = content.substring(meetResult);

            if (content.isEmpty()) {
                return -1;
            }
            if (content.charAt(0) == ']') {
                return index + 1;
            }

            index = 1;
            content = jsonContent.substring(1);
            do {
                meetResult = meetWhitespace(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);

                meetResult = meetValue(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);
                if (content.isEmpty()) {
                    return -1;
                }
                if (content.charAt(0) == ']') {
                    return index + 1;
                } else if (content.charAt(0) != ',') {
                    return -1;
                }
                content = content.substring(1);
                index++;
            } while (index < jsonContent.length());
        }
        return -1;
    }

    /**
     * Gets a content as input and meets a json object structure using
     * <a href="https://www.json.org/img/object.png">https://www.json.org/img/object.png</a> state machine and returns
     * number of character that it consumed to meet the json object. If the structure does not meet a json object, it
     * returns -1.
     *
     * @param jsonContent content that can start with a json object
     * @return number of consumed characters to meet the json object or -1 if the structure does not meet a json object
     */
    private static int meetObject(String jsonContent) {
        if (jsonContent.isEmpty()) return -1;
        String content = jsonContent;
        if (content.charAt(0) == '{') {
            content = content.substring(1);
            int index = 1;

            int meetResult = meetWhitespace(content);
            if (meetResult < 0) {
                return -1;
            }
            index += meetResult;
            content = content.substring(meetResult);

            if (content.isEmpty()) {
                return -1;
            }

            if (content.charAt(0) == '}') {
                return index + 1;
            }

            index = 1;
            content = jsonContent.substring(1);
            do {
                meetResult = meetWhitespace(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);

                meetResult = meetString(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);

                meetResult = meetWhitespace(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);

                if (content.isEmpty() || content.charAt(0) != ':') {
                    return -1;
                }
                index++;
                content = content.substring(1);

                meetResult = meetValue(content);
                if (meetResult < 0) {
                    return -1;
                }
                index += meetResult;
                content = content.substring(meetResult);
                if (content.isEmpty()) {
                    return -1;
                }
                if (content.charAt(0) == '}') {
                    return index + 1;
                } else if (content.charAt(0) != ',') {
                    return -1;
                }
                content = content.substring(1);
                index++;
            } while (index < jsonContent.length());
        }
        return -1;
    }

    /**
     * Gets a content as input and meets a json value structure using
     * <a href="https://www.json.org/img/value.png">https://www.json.org/img/value.png</a> state machine and returns
     * number of character that it consumed to meet the json value. If the structure does not meet a json value, it
     * returns -1.
     *
     * @param jsonContent content that can start with a json value
     * @return number of consumed characters to meet the json value or -1 if the structure does not meet a json value
     */
    private static int meetValue(String jsonContent) {
        if (jsonContent.isEmpty()) return -1;
        String content = jsonContent;
        int index = 0;

        int meetResult = meetWhitespace(content);
        if (meetResult < 0) {
            return -1;
        }
        index += meetResult;
        content = content.substring(meetResult);

        if (content.startsWith("null")) {
            index += 4;
            content = content.substring(4);
        } else if (content.startsWith("true")) {
            index += 4;
            content = content.substring(4);
        } else if (content.startsWith("false")) {
            index += 5;
            content = content.substring(5);
        } else if ((meetResult = meetString(content)) >= 0) {
            index += meetResult;
            content = content.substring(meetResult);
        } else if ((meetResult = meetNumber(content)) >= 0) {
            index += meetResult;
            content = content.substring(meetResult);
        } else if ((meetResult = meetObject(content)) >= 0) {
            index += meetResult;
            content = content.substring(meetResult);
        } else if ((meetResult = meetArray(content)) >= 0) {
            index += meetResult;
            content = content.substring(meetResult);
        } else {
            return -1;
        }

        meetResult = meetWhitespace(content);
        if (meetResult < 0) {
            return -1;
        }
        index += meetResult;
        return index;
    }

    /**
     * Gets a content as input and meets a json number structure using
     * <a href="https://www.json.org/img/number.png">https://www.json.org/img/number.png</a> state machine and returns
     * number of character that it consumed to meet the json number. If the structure does not meet a json number, it
     * returns -1.
     *
     * @param jsonContent content that can start with a json number
     * @return number of consumed characters to meet the json number or -1 if the structure does not meet a json number
     */
    private static int meetNumber(String jsonContent) {
        if (jsonContent.isEmpty()) return -1;
        final int minusEmptyDetect = 1;
        final int zeroDigit1To9Detect = 2;
        final int digit1To9Consume = 3;
        final int digitsFractionDetect = 4;
        final int fractionExponentDetect = 5;
        final int dotConsume = 6;
        final int fractionDigitConsume = 7;
        final int digitExponentDetect = 8;
        final int exponentEndDetect = 9;
        final int eConsume = 10;
        final int plusMinusDetect = 11;
        final int exponentDigitConsume = 12;
        final int digitsEndDetect = 13;

        int index = 0;
        boolean end = false;
        int state = 1;
        while (index < jsonContent.length() && !end) {
            String current = String.valueOf(jsonContent.charAt(index));
            switch (state) {
                case minusEmptyDetect:
                    if (current.equals("-")) {
                        index++;
                    }
                    state = zeroDigit1To9Detect;
                    break;
                case zeroDigit1To9Detect:
                    if (current.equals("0")) {
                        index++;
                        state = fractionExponentDetect;
                    } else {
                        state = digit1To9Consume;
                    }
                    break;
                case digit1To9Consume:
                    if (current.matches("[1-9]")) {
                        index++;
                        state = digitsFractionDetect;
                    } else {
                        return -1;
                    }
                    break;
                case digitsFractionDetect:
                    if (current.matches("[0-9]")) {
                        index++;
                    } else if (current.equals(".")) {
                        state = dotConsume;
                    } else {
                        state = exponentEndDetect;
                    }
                    break;
                case fractionExponentDetect:
                    if (current.equals(".")) {
                        state = dotConsume;
                    } else {
                        state = exponentEndDetect;
                    }
                    break;
                case dotConsume:
                    if (current.equals(".")) {
                        index++;
                        state = fractionDigitConsume;
                    } else {
                        return -1;
                    }
                    break;
                case fractionDigitConsume:
                    if (current.matches("[0-9]")) {
                        index++;
                        state = digitExponentDetect;
                    } else {
                        return -1;
                    }
                    break;
                case digitExponentDetect:
                    if (current.matches("[0-9]")) {
                        index++;
                    } else {
                        state = exponentEndDetect;
                    }
                    break;
                case exponentEndDetect:
                    if (current.toLowerCase().equals("e")) {
                        if (index + 1 < jsonContent.length()) {
                            if (jsonContent.charAt(index + 1) == '-' || jsonContent.charAt(index + 1) == '+') {
                                if (index + 2 < jsonContent.length()) {
                                    if (String.valueOf(jsonContent.charAt(index + 2)).matches("[0-9]")) {
                                        state = eConsume;
                                    } else {
                                        end = true;
                                    }
                                } else {
                                    end = true;
                                }
                            } else if (String.valueOf(jsonContent.charAt(index + 1)).matches("[0-9]")) {
                                state = eConsume;
                            } else {
                                end = true;
                            }
                        } else {
                            end = true;
                        }
                    } else {
                        end = true;
                    }
                    break;
                case eConsume:
                    if (current.toLowerCase().equals("e")) {
                        index++;
                        state = plusMinusDetect;
                    } else {
                        return -1;
                    }
                    break;
                case plusMinusDetect:
                    if (current.equals("+") || current.equals("-")) {
                        index++;
                    }
                    state = exponentDigitConsume;
                    break;
                case exponentDigitConsume:
                    if (current.matches("[0-9]")) {
                        index++;
                        state = digitsEndDetect;
                    } else {
                        return -1;
                    }
                    break;
                case digitsEndDetect:
                    if (current.matches("[0-9]")) {
                        index++;
                    } else {
                        end = true;
                    }
                    break;
            }
        }
        return index;
    }

    /**
     * Gets a content as input and meets a json string structure  and returns number of character that it consumed
     * to meet the json string. If the structure does not meet a json string, it returns -1.
     *
     * @param jsonContent content that can start with a json string
     * @return number of consumed characters to meet the json string or -1 if the structure does not meet a json string
     */
    private static int meetString(String jsonContent) {
        if (jsonContent.isEmpty()) return -1;
        if (!jsonContent.startsWith("\"")) {
            return -1;
        }
        int index = 1;
        boolean backSlashSeen = false;
        while (index < jsonContent.length()) {
            if (jsonContent.charAt(index) == '\\') {
                backSlashSeen = !backSlashSeen;
            }
            if (jsonContent.charAt(index) == '\"') {
                if (!backSlashSeen) {
                    return index + 1;
                }
            }
            index++;
        }
        return -1;
    }

    /**
     * Gets a content as input and meets a json whitespace structure  and returns number of character that it consumed
     * to meet the json whirespace. If the structure does not meet a json whitespace, it returns -1.
     *
     * @param jsonContent content that can start with a json whitespace
     * @return number of consumed characters to meet the json whitespace or -1 if the structure does not meet a
     * json whitespace
     */
    private static int meetWhitespace(String jsonContent) {
        int index = 0;
        while (index < jsonContent.length() && jsonContent.substring(0, index + 1).matches("\\s+")) {
            index++;
        }
        return index;
    }
}
