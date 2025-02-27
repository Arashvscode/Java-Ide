/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
 */
/*
 * Copyright 1999-2002,2004,2005 The Apache Software Foundation.
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

import java.util.StringTokenizer;

/**
 * For list types: ENTITIES, IDREFS, NMTOKENS.
 *
 * @xerces.internal
 * @author Jeffrey Rodriguez, IBM
 * @author Sandy Gao, IBM
 */
public class ListDatatypeValidator implements DatatypeValidator {

    // the type of items in the list
    DatatypeValidator fItemValidator;

    // construct a list datatype validator
    public ListDatatypeValidator(DatatypeValidator itemDV) {
        fItemValidator = itemDV;
    }

    /**
     * Checks that "content" string is valid. If invalid a Datatype validation exception is thrown.
     *
     * @param content the string value that needs to be validated
     * @param context the validation context
     * @throws InvalidDatatypeException if the content is invalid according to the rules for the
     *     validators
     * @see InvalidDatatypeValueException
     */
    public void validate(String content, ValidationContext context)
            throws InvalidDatatypeValueException {

        StringTokenizer parsedList = new StringTokenizer(content, " ");
        int numberOfTokens = parsedList.countTokens();
        if (numberOfTokens == 0) {
            throw new InvalidDatatypeValueException("EmptyList", null);
        }
        // Check each token in list against base type
        while (parsedList.hasMoreTokens()) {
            this.fItemValidator.validate(parsedList.nextToken(), context);
        }
    }
}
