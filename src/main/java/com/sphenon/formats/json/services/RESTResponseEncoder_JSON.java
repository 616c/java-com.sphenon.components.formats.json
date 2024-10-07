package com.sphenon.formats.json.services;

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
import com.sphenon.basics.cache.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.services.*;
import com.sphenon.server.ws.*;

import com.sphenon.formats.json.*;

public class RESTResponseEncoder_JSON implements RESTResponseEncoder {

    public RESTResponseEncoder_JSON(CallContext context) {
    }

    public void notifyNewConsumer(CallContext context, Consumer consumer) {
        // nice to see you
    }

    public boolean equals(Object object) {
        return (object instanceof RESTResponseEncoder_JSON);
    }

    static protected MIMEType my_mime_type_1;
    static protected MIMEType my_mime_type_2;

    public boolean canHandleMIMEType(CallContext context, MIMEType mime_type, Object data) {
        if (my_mime_type_1 == null) {
            my_mime_type_1 = new MIMEType(context, "application/json");
        }
        if (my_mime_type_2 == null) {
            my_mime_type_2 = new MIMEType(context, "text/json");
        }
        return (    mime_type.matches(context, my_mime_type_1)
                 || mime_type.matches(context, my_mime_type_2));
    }

    public String[] encodeResponse(CallContext context, Object data) {
        try {
            String json = JSONSerialiserImpl.serialiseToString(context, data, "json-wrap-within", "data");
            return new String[] { json, "application/json" };
        } catch (Throwable t) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not serialise '%(data)' to JSON", "data", data);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
