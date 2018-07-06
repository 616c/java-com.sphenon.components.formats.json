package com.sphenon.formats.json;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.quantities.*;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Stack;

/**
   @ignore ==============================================================================================
   @doclet {@Category Reference} {@Audience Administrator} {@Maturity Final} {@SecurityClass SphenonOnly}

   @configuration JSONSerialiser:json-wrap-within  if non null, the serialised JSON is wrapped within
                                                   an additional JSON object and attached to an attribute
                                                   of that object with the name given by this property;
                                                   otherwise, the serialised JSON is returned unmodified

   @configuration JSONSerialiser:json-source-encoding if non null, all JSON attribute names will be recoded
                                                      from this encoding to the json-target-encoding, one of
                                                      both can be omitted and then it defaults to UTF8, if
                                                      both are omitted obviously no recoding takes place

   @configuration JSONSerialiser:json-target-encoding if non null, all JSON attribute names will be recoded
                                                      from the json-source-encoding to this encoding, one of
                                                      both can be omitted and then it defaults to UTF8, if
                                                      both are omitted obviously no recoding takes place

*/
public class JSONSerialiserImpl implements JSONSerialiser {    

    static public String serialiseToString(CallContext context, Object object, String... properties) {
        StringBuilder sb = new StringBuilder();
        try {
            serialise(context, sb, object, properties);
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentError(context, ioe, "could not serialise to JSON");
            throw (ExceptionEnvironmentError) null; // compiler insists
        }
        return sb.toString();
    }

    static public void serialise(CallContext context, Appendable appendable, Object object, String... properties) throws IOException {
        JSONSerialiserImpl jsi = new JSONSerialiserImpl(context, appendable, properties);

        String wrap = jsi.getProperty(context, "json-wrap-within", (String) null);
        if (wrap != null) {
            appendable.append("{\"").append(wrap).append("\":");
        }

        String se = jsi.getProperty(context, "json-source-encoding", (String) null);
        String te = jsi.getProperty(context, "json-target-encoding", (String) null);
        if (se != null || te != null) {
            jsi.source_encoding = Encoding.getEncoding(context, se == null ? "UTF8" : se);
            jsi.target_encoding = Encoding.getEncoding(context, te == null ? "UTF8" : te);
        }

        jsi.serialise(context, object, (String) null);

        if (wrap != null) {
            appendable.append("}");
        }
    }

    protected Appendable appendable;
    protected boolean first;
    protected Stack<Boolean> first_stack;

    protected Encoding source_encoding;
    protected Encoding target_encoding;

    public JSONSerialiserImpl(CallContext context, Appendable appendable, Map<String,String> properties) {
        this.appendable = appendable; 
        this.properties = properties;
        this.first = true;
        this.first_stack = new Stack<Boolean>();
        this.current_level = 0;
    }

    public JSONSerialiserImpl(CallContext context, Appendable appendable, String... properties) {
        this(context, appendable, makeMap(context, properties));
    }

    public void openObject(CallContext context, String name) throws IOException {
        this.current_level++;

        if (this.first == false) { 
            appendable.append(',');
        }

        if (name != null) { 
            appendable.append('"');
            if (this.source_encoding != null) {
                appendable.append(Encoding.recode(context, name, this.source_encoding, this.target_encoding));
            } else {
                appendable.append(name);
            }
            appendable.append('"');
            appendable.append(':');
        }

        this.appendable.append('{');

        this.first = true;
    }

    public void closeObject(CallContext context) throws IOException {
        this.appendable.append('}');
        this.first = false;
        this.current_level--;
    }

    public void openArray(CallContext context, String name) throws IOException {
        this.current_level++;

        if (this.first == false) { 
            appendable.append(',');
        }

        if (name != null) { 
            appendable.append('"');
            if (this.source_encoding != null) {
                appendable.append(Encoding.recode(context, name, this.source_encoding, this.target_encoding));
            } else {
                appendable.append(name);
            }
            appendable.append('"');
            appendable.append(':');
        }

        this.appendable.append('[');

        this.first = true;
    }

    public void closeArray(CallContext context) throws IOException {
        this.appendable.append(']');
        this.first = false;
        this.current_level--;
    }

    public void serialise(CallContext context, Object object, String name) throws IOException {
        if (this.first == false) { 
            appendable.append(',');
        }

        if (name != null) { 
            appendable.append('"');
            if (this.source_encoding != null) {
                appendable.append(Encoding.recode(context, name, this.source_encoding, this.target_encoding));
            } else {
                appendable.append(name);
            }
            appendable.append('"');
            appendable.append(':');
        }

        if (object == null) {
            appendable.append("null");
        } else if (object instanceof String) {
            appendable.append('"');
            appendable.append(Encoding.recode(context, (String) object, Encoding.UTF8, Encoding.JSON));
            appendable.append('"');
        } else if (object instanceof RichText) {
            appendable.append("{\"@Class\":\"RichText\",Text:\"");
            appendable.append(Encoding.recode(context, ((RichText) object).getText(context), Encoding.UTF8, Encoding.JSON));
            appendable.append("\",MediaType:\"");
            appendable.append(((RichText) object).getMediaType(context));
            appendable.append("\"]");
        } else if (    object instanceof Boolean
                    || object instanceof Byte
                    || object instanceof Character
                    || object instanceof Short
                    || object instanceof Integer
                    || object instanceof Long
                    || object instanceof Float
                    || object instanceof Double
                  ) {
            appendable.append(object.toString());
        } else if (object instanceof List) {
            this.openArray(context, null);
            for (Object item : (List) object) {
                this.serialise(context, item, null);
            }
            this.closeArray(context);
        } else if (object instanceof Map) {
            this.openObject(context, null);
            for (Map.Entry entry : (Set<Map.Entry>) (((Map) object).entrySet())) {
                this.serialise(context, entry.getValue(), (String) (entry.getKey()));
            }
            this.closeObject(context);
        } else if (object instanceof GenericIterable) {
            this.openArray(context, null);
            for (Object item : ((GenericIterable) object).getIterable(context)) {
                this.serialise(context, item, null);
            }
            this.closeArray(context);
        } else {
            if (object instanceof JSONSerialisable) {
                JSONSerialisable jsons = (JSONSerialisable) object;

                this.first = true;
                jsons.jsonSerialise(context, this);
            } else {
                appendable.append('"');
                appendable.append(Encoding.recode(context, MessageTextClass.convertToString(context, object), Encoding.UTF8, Encoding.JSON));
                appendable.append('"');
            }
        }

        this.first = false;
    }

    protected int current_level;

    public int  getCurrentLevel(CallContext context) {
        return this.current_level;
    }

    protected Map<String,String> properties;

    protected String getProperty(CallContext context, String name) {
        return this.properties == null ? null : this.properties.get(name);
    }

    public boolean getProperty(CallContext context, String name, boolean default_value) {
        String property = this.getProperty(context, name);
        return (property == null ? default_value : Boolean.parseBoolean(property));
    }

    public int getProperty(CallContext context, String name, int default_value) {
        String property = this.getProperty(context, name);
        return (property == null ? default_value : Integer.parseInt(property));
    }

    public String getProperty(CallContext context, String name, String default_value) {
        String property = this.getProperty(context, name);
        return (property == null ? default_value : property);
    }

    public String setProperty(CallContext context, String name, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<String,String>();
        }
        return this.properties.put(name, value);
    }

    static protected Map makeMap(CallContext context, String... arguments) {
        Map<String,String> parameters = new HashMap<String,String>();
        
        if (arguments != null) {
            if (arguments.length % 2 != 0) {
                CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Setup of JSONSerialiser with variable arguments failed, number of arguments is uneven");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            for (int a = 0; a < arguments.length; a += 2) {
                String name  = arguments[a];
                String value = arguments[a + 1];
                if (value == null) {
                    CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Setup of JSONSerialiser failed, argument '%(name)' is null", "name", name);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
                parameters.put(name, value);
            }
        }

        return parameters;
    }

    protected Map<String,Object> attachments;

    public Object getAttachment(CallContext context, String name) {
        return (this.attachments == null ? null : this.attachments.get(name));
    }

    public void   setAttachment(CallContext context, String name, Object attachment) {
        if (this.attachments == null) {
            this.attachments = new HashMap<String,Object>();
        }
        this.attachments.put(name, attachment);
    }
}
