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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import org.json.*;

import java.util.Stack;

public class JSONSerializerImpl implements JSONSerializer {

    static public String serialize(CallContext context, Object object) {
        JSONSerializerImpl jsi = new JSONSerializerImpl(context);
        return jsi.serialize(context, object, null);
    }

    protected Stack<JSONObject> jos;

    public JSONSerializerImpl(CallContext context) {
    }

    public String serialize(CallContext context, Object object, String name) {
        if ((object instanceof JSONSerializable) == false) {
            // not serializable
        }

        try {
            JSONObject jo;
            if (name == null) {
                jo = new JSONObject();
                this.jos = new Stack<JSONObject>();
                this.jos.push(jo);
            } else {
                jo = this.jos.peek();
            }

            if (object instanceof String) {
                jo.put(name, (String) object);
                return null;
            } else {

                if (name != null) { 
                    JSONObject new_jo = new JSONObject();
                    jo.put(name, new_jo);
                    this.jos.push(new_jo);
                    jo = new_jo;
                }
            
                if (object instanceof JSONSerializable) {
                    JSONSerializable jsons = (JSONSerializable) object;
                    jsons.jsonSerialize(context, this);
                } else {
                    jo.put(name, MessageTextClass.convertToString(context, object));
                }
            
                if (name != null) { 
                    this.jos.pop();
                    return null;
                } else {
                    return jo.toString();
                }
            }
        } catch (JSONException je) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, je, "JSON serialisation failed");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }
}
