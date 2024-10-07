package com.sphenon.formats.json;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;

import com.sphenon.formats.json.returncodes.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JSON {

    static public String toString(CallContext context, Object json_object) {
        if (json_object == null) {
            return null;
        }
        try {
            return (new ObjectMapper()).writeValueAsString(json_object);
        } catch (JsonProcessingException jpe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, jpe, "Could not serialise JSON '%(object)'", "object", json_object);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    static public Object fromString(CallContext context, String json_string) {
        if (json_string == null) {
            return null;
        }
        try {
            return JSONNode.createJSONNode(context, json_string).toTree(context);
        } catch (InvalidJSON ij) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ij, "Could not parse JSON '%(json)'", "json", json_string);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }
}
