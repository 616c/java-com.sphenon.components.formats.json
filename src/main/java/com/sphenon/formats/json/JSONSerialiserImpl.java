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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.variatives.*;
import com.sphenon.basics.quantities.*;
import com.sphenon.basics.state.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import java.io.InputStream;
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

    static public Scope serialisation_attribute_scope      = new Class_Scope(RootContext.getFallbackCallContext(), (String) null, (Scope) null, "this.IsAttribute", true);
    static public Scope serialisation_owning_end_scope     = new Class_Scope(RootContext.getFallbackCallContext(), (String) null, (Scope) null, "this.IsOwningEnd", true);
    static public Scope serialisation_non_owning_end_scope = new Class_Scope(RootContext.getFallbackCallContext(), (String) null, (Scope) null, "this.IsNonOwningEnd", true);

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

        jsi.serialise(context, object, (String) null);

        if (wrap != null) {
            appendable.append("}");
        }
    }

    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7, String n8, Object o8, String n9, Object o9) throws IOException {
        this.openObject(context, null);
        if (n1 != null) { this.serialise(context, o1, n1); }
        if (n2 != null) { this.serialise(context, o2, n2); }
        if (n3 != null) { this.serialise(context, o3, n3); }
        if (n4 != null) { this.serialise(context, o4, n4); }
        if (n5 != null) { this.serialise(context, o5, n5); }
        if (n6 != null) { this.serialise(context, o6, n6); }
        if (n7 != null) { this.serialise(context, o7, n7); }
        if (n8 != null) { this.serialise(context, o8, n8); }
        if (n9 != null) { this.serialise(context, o9, n9); }
        this.closeObject(context);
    }

    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7, String n8, Object o8) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, n7, o7, n8, o8, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, n7, o7, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1, String n2, Object o2) throws IOException {
        serialise(context, n1, o1, n2, o2, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }
    public void serialise(CallContext context, String n1, Object o1) throws IOException {
        serialise(context, n1, o1, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null, (String) null, (Object) null);
    }

    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7, String n8, Object o8, String n9, Object o9, Appendable appendable, String... properties) throws IOException {
        JSONSerialiserImpl jsi = new JSONSerialiserImpl(context, appendable, properties);
        jsi.serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, n7, o7, n8, o8, n9, o9);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7, String n8, Object o8, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, n7, o7, n8, o8, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, n7, o7, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, n6, o6, null, null, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, n5, o5, null, null, null, null, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, n4, o4, null, null, null, null, null, null, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, String n3, Object o3, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, n3, o3, null, null, null, null, null, null, null, null, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, String n2, Object o2, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, n2, o2, null, null, null, null, null, null, null, null, null, null, null, null, null, null, appendable, properties);
    }
    static public void serialise(CallContext context, String n1, Object o1, Appendable appendable, String... properties) throws IOException {
        serialise(context, n1, o1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, appendable, properties);
    }

    protected Appendable appendable;
    protected boolean first;
    protected boolean named;
    protected Stack<Boolean> first_stack;
    protected Stack<Boolean> named_stack;

    protected Encoding source_encoding;
    protected Encoding target_encoding;
    protected boolean  resolve_variatives;

    public JSONSerialiserImpl(CallContext context, Appendable appendable, Map<String,String> properties) {
        this(context, appendable, properties, null);
    }

    public JSONSerialiserImpl(CallContext context, Appendable appendable, Map<String,String> properties, Map<String,Object> attachments) {
        this.appendable  = appendable; 
        this.properties  = properties;
        this.attachments = attachments;
        this.first = true;
        this.first_stack = new Stack<Boolean>();
        this.named = false;
        this.named_stack = new Stack<Boolean>();
        this.current_level = 0;
    }

    public JSONSerialiserImpl(CallContext context, Appendable appendable, String... properties) {
        this(context, appendable, makeMap(context, properties));

        String se = this.getProperty(context, "json-source-encoding", (String) null);
        String te = this.getProperty(context, "json-target-encoding", (String) null);
        if (se != null || te != null) {
            this.source_encoding = Encoding.getEncoding(context, se == null ? "UTF8" : se);
            this.target_encoding = Encoding.getEncoding(context, te == null ? "UTF8" : te);
        }

        this.resolve_variatives = this.getProperty(context, "json-resolve-variatives", false);
    }

    protected String recode(CallContext context, String string) {
        return (this.source_encoding != null ? Encoding.recode(context, string, this.source_encoding, this.target_encoding) : string);
    }

    public void openObject(CallContext context, String name) throws IOException {
        this.current_level++;

        if (this.first == false && this.named == false) { 
            appendable.append(',');
        }

        if (name != null && this.named == true) { 
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Entry in JSONSerialiser is named twice, current name is '%(name)'", "name", name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        if (name != null) { 
            appendable.append('"');
            appendable.append(recode(context, name));
            appendable.append('"');
            appendable.append(':');
        }

        this.appendable.append('{');

        this.named = false;
        this.first = true;
    }

    public void closeObject(CallContext context) throws IOException {
        if (this.named == true) { 
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Entry in JSONSerialiser is named, but no value provided");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        this.appendable.append('}');
        this.first = false;
        this.current_level--;
    }

    public void openArray(CallContext context, String name) throws IOException {
        this.current_level++;

        if (this.first == false && this.named == false) { 
            appendable.append(',');
        }

        if (name != null && this.named == true) { 
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Entry in JSONSerialiser is named twice, current name is '%(name)'", "name", name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        if (name != null) { 
            appendable.append('"');
            appendable.append(recode(context, name));
            appendable.append('"');
            appendable.append(':');
        }

        this.appendable.append('[');

        this.named = false;
        this.first = true;
    }

    public void closeArray(CallContext context) throws IOException {
        if (this.named == true) { 
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Entry in JSONSerialiser is named, but no value provided");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        this.appendable.append(']');
        this.first = false;
        this.current_level--;
    }

    public void serialise(CallContext context, Object object, String name) throws IOException {

        if (this.first == false && this.named == false) { 
            appendable.append(',');
        }

        if (name != null && this.named == true) { 
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Entry in JSONSerialiser is named twice, current name is '%(name)'", "name", name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        this.first = true;

        if (name != null) { 
            appendable.append('"');
            appendable.append(recode(context, name));
            appendable.append('"');
            appendable.append(':');

            this.named = true;
        }

        if (this.resolve_variatives && object instanceof Variative) {
            object = ((Variative) object).getVariant(context);
        }

        if (object == null) {
            appendable.append("null");
        } else if (object instanceof String) {
            appendable.append('"');
            appendable.append(Encoding.recode(context, (String) object, Encoding.UTF8, Encoding.JSON));
            appendable.append('"');
        } else if (object instanceof Character) {
            appendable.append('"');
            appendable.append(Encoding.recode(context, String.valueOf((Character) object), Encoding.UTF8, Encoding.JSON));
            appendable.append('"');
        } else if (object instanceof RichText) {
            appendable.append("{\"" + recode(context, "@Class") + "\":\"RichText\",\"" + recode(context, "Text") + "\":\"");
            appendable.append(Encoding.recode(context, ((RichText) object).getText(context), Encoding.UTF8, Encoding.JSON));
            appendable.append("\",\"" + recode(context, "MediaType") + "\":\"");
            appendable.append(((RichText) object).getMediaType(context));
            appendable.append("\"}");
        } else if (    object instanceof Boolean
                    || object instanceof Byte
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
        } else if (object.getClass().isArray()) {
            this.openArray(context, null);
            for (int i=0; i<java.lang.reflect.Array.getLength(object); i++) {
                this.serialise(context, java.lang.reflect.Array.get(object, i), null);
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
        } else if (    object instanceof Data_MediaObject
                    && isJSONMedia(context, (Data_MediaObject) object)
                  ) {
            InputStream is = ((Data_MediaObject) object).getInputStream(context);
            if (is == null) {
                appendable.append("null");
            } else {
                FileUtilities.copy(context, is, appendable);
            }
        } else if (object instanceof Execution) {
            this.openObject(context, null);
            Execution execution = (Execution) object;
            if (object instanceof Execution_BasicSequence) {
                this.serialise(context, (Object) "ExecutionSequence", "@Class");
            } else {
                this.serialise(context, (Object) "Execution", "@Class");
            }
            this.serialise(context, execution.getProblemState(context), "ProblemState");
            if (execution.getProblemCategory(context) != null) {
                this.serialise(context, execution.getProblemCategory(context), "ProblemCategory");
            }
            this.serialise(context, execution.getActivityState(context), "ActivityState");
            if (execution.getInstruction(context) != null) {
                this.serialise(context, execution.getInstruction(context), "Instruction");
            }
            if (execution.getProgression(context) != null) {
                this.serialise(context, execution.getProgression(context), "Progression");
            }
            if (execution.getProblemState(context) != null && execution.getProblemState(context).isOk(context) == false) {
                if (execution.getProblem(context) != null) {
                    this.serialise(context, execution.getProblem(context), "Problem");
                }
                if (execution.getRecord(context) != null) {
                    this.serialise(context, execution.getRecord(context), "Record");
                }
                if (execution.getPerformance(context) != null) {
                    this.serialise(context, execution.getPerformance(context), "Performance");
                }
                if (object instanceof Execution_BasicSequence) {
                    this.openArray(context, "Executions");
                    for (Execution e : ((Execution_BasicSequence) object).getExecutions(context)) {
                        this.serialise(context, e, null);
                    }
                    this.closeArray(context);
                }
            }
            this.closeObject(context);
        } else {
            if (object instanceof JSONSerialisable) {
                JSONSerialisable jsons = (JSONSerialisable) object;

                jsons.jsonSerialise(context, this);
            } else {
                appendable.append('"');
                appendable.append(Encoding.recode(context, MessageTextClass.convertToString(context, object), Encoding.UTF8, Encoding.JSON));
                appendable.append('"');
            }
        }

        this.named = false;
        this.first = false;
    }

    protected boolean isJSONMedia(CallContext context, Data_MediaObject dmo) {
        String mt = dmo.getMediaType(context);
        return (    mt != null
                 && mt.matches("^(text|application)/json$")
               ) ? true: false;
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

    protected Stack<Map<String,String>> property_stack;

    public void pushProperties(CallContext context, String... properties) {
        if (this.property_stack == null) {
            this.property_stack = new Stack<Map<String,String>>();
        }
        Map<String,String> map = new HashMap<String,String>();
        if (properties != null) {
            for (int i=0; i<properties.length; i+=2) {
                String name = properties[i];
                if (name != null) {
                    String previous = this.setProperty(context, name, properties[i+1]);
                    map.put(name, previous);
                }
            }
        }
        this.property_stack.push(map);
    }

    public void popProperties (CallContext context) {
        Map<String,String> map = this.property_stack.pop();
        for (Map.Entry<String,String> entry : map.entrySet()) {
            this.setProperty(context, entry.getKey(), entry.getValue());
        }
    }

    protected Map<String,Object> attachments;

    public Object getAttachment(CallContext context, String name) {
        return (this.attachments == null ? null : this.attachments.get(name));
    }

    public Object setAttachment(CallContext context, String name, Object attachment) {
        if (this.attachments == null) {
            this.attachments = new HashMap<String,Object>();
        }
        return this.attachments.put(name, attachment);
    }

    public void removeAttachment(CallContext context, String name) {
        if (this.attachments == null) { return; }
        this.attachments.remove(name);
    }

    public void setAttachments(CallContext context, Object... arguments) {
        Map<String,Object> attachments = new HashMap<String,Object>();
        
        if (arguments != null) {
            if (arguments.length % 2 != 0) {
                CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Setup of JSONSerialiser with variable arguments failed, number of attachments arguments is uneven");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            for (int a = 0; a < arguments.length; a += 2) {
                String name       = (String) arguments[a];
                Object attachment = arguments[a + 1];
                if (attachment != null) {
                    this.setAttachment(context, name, attachment);
                }
            }
        }

    }

    protected Stack<Map<String,Object>> attachment_stack;

    public void pushAttachments(CallContext context, Object... attachments) {
        if (this.attachment_stack == null) {
            this.attachment_stack = new Stack<Map<String,Object>>();
        }
        Map<String,Object> map = new HashMap<String,Object>();
        if (attachments != null) {
            for (int i=0; i<attachments.length; i+=2) {
                String name = (String) attachments[i];
                if (name != null) {
                    Object previous = this.setAttachment(context, name, attachments[i+1]);
                    map.put(name, previous);
                }
            }
        }
        this.attachment_stack.push(map);
    }

    public void popAttachments (CallContext context) {
        Map<String,Object> map = this.attachment_stack.pop();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            this.setAttachment(context, entry.getKey(), entry.getValue());
        }
    }

    static protected Map makeMap(CallContext context, String... arguments) {
        Map<String,String> parameters = new HashMap<String,String>();
        
        if (arguments != null) {
            if (arguments.length % 2 != 0) {
                CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Setup of JSONSerialiser with variable arguments failed, number of property arguments is uneven");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            for (int a = 0; a < arguments.length; a += 2) {
                String name  = arguments[a];
                String value = arguments[a + 1];
                if (value != null) {
                    parameters.put(name, value);
                }
            }
        }

        return parameters;
    }

    protected Stack<State> serialisation_states;

    public State getSerialisationState(CallContext context) {
        return (this.serialisation_states == null || this.serialisation_states.empty() ? null : this.serialisation_states.peek());
    }

    public State popSerialisationState(CallContext context) {
        if (this.serialisation_states == null || this.serialisation_states.empty()) {
            CustomaryContext.create((Context)context).throwInvalidState(context, "Serialisation stack empty");
            throw (ExceptionInvalidState) null; // compiler insists
        }

        return (this.serialisation_states == null || this.serialisation_states.empty() ? null : this.serialisation_states.pop());
    }

    public void pushSerialisationState(CallContext context, State serialisation_state) {
        if (this.serialisation_states == null) {
            this.serialisation_states = new Stack<State>();
        }
        this.serialisation_states.push(serialisation_state);
    }

    public void verfiySerialisationStateIsEmpty(CallContext context) {
        if (this.serialisation_states != null || this.serialisation_states.empty() == false) {
            CustomaryContext.create((Context)context).throwInvalidState(context, "Serialisation stack not empty");
            throw (ExceptionInvalidState) null; // compiler insists
        }
    }
}
