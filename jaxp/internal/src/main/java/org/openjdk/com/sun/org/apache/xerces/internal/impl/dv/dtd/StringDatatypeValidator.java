/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
 */
/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openjdk.com.sun.org.apache.xerces.internal.impl.dv.dtd;

import org.openjdk.com.sun.org.apache.xerces.internal.impl.dv.*;
import org.openjdk.com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import org.openjdk.com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import org.openjdk.com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

/**
 * StringValidator validates that XML content is a W3C string type.
 *
 * <p>The string datatype represents character strings in XML. The value space of string is the set
 * of finite-length sequences of characters (as defined in [XML 1.0 Recommendation (Second
 * Edition)]) that match the Char production from [XML 1.0 Recommendation (Second Edition)]. A
 * character is an atomic unit of communication; it is not further specified except to note that
 * every character has a corresponding Universal Code Set code point ([ISO 10646],[Unicode] and
 * [Unicode3]), which is an integer.
 *
 * @xerces.internal
 */
public class StringDatatypeValidator implements DatatypeValidator {

    // construct a string datatype validator
    public StringDatatypeValidator() {}

    /**
     * Checks that "content" string is valid string value. If invalid a Datatype validation
     * exception is thrown.
     *
     * @param content the string value that needs to be validated
     * @param context the validation context
     * @throws InvalidDatatypeException if the content is invalid according to the rules for the
     *     validators
     * @see InvalidDatatypeValueException
     */
    public void validate(String content, ValidationContext context)
            throws InvalidDatatypeValueException {}
}
