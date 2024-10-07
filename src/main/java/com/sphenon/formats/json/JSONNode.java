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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.io.*;

public class JSONNode implements GenericIterable<JSONNode>, ContextAware {
    static final public Class _class = JSONNode.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected CallContext      creation_context;
    protected Vector<JsonNode> nodes;
    protected JsonNode         first_node;
    protected Vector<JSONNode> json_nodes;
    protected String           name;

    static protected JsonNode parseJSON(CallContext context, String json_string) throws InvalidJSON {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json_string);
        } catch (java.io.IOException ioe) {
            InvalidJSON.createAndThrow(context, ioe, "Cannot parse JSON '%(json_string)'", "json_string", json_string);
            throw (InvalidJSON) null; // compiler insists
        }
    }

    static protected JsonNode parseJSON(CallContext context, InputStream json_stream) throws InvalidJSON {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json_stream);
        } catch (java.io.IOException ioe) {
            InvalidJSON.createAndThrow(context, ioe, "Cannot parse JSON stream");
            throw (InvalidJSON) null; // compiler insists
        }
    }

    static protected Vector<JsonNode> makeVector(JsonNode node) {
        Vector<JsonNode> vector = new Vector<JsonNode>();
        vector.add(node);
        return vector;
    }

    public JSONNode (CallContext context) {
        this(context, (Vector<JsonNode>) null, null);
    }

    public JSONNode (CallContext context, JsonNode node) {
        this(context, makeVector(node), null);
    }

    public JSONNode (CallContext context, JsonNode node, String name) {
        this(context, makeVector(node), name);
    }

    public JSONNode (CallContext context, Vector<JsonNode> nodes) {
        this(context, nodes, null);
    }

    public JSONNode (CallContext context, Vector<JsonNode> nodes, String name) {
        this.creation_context = context;
        this.nodes = nodes;
        this.name = name;
        this.first_node = (this.nodes != null && this.nodes.size() >= 1 ? this.nodes.get(0) : null);
    }

    // static public JSONNode createJSONNode(CallContext context, TreeLeaf tree_leaf) throws InvalidJSON {
    //     Data_MediaObject data = ((Data_MediaObject)(((NodeContent_Data)(tree_leaf.getContent(context))).getData(context)));
    //     return createJSONNode(context, data.getStream(context), data.getDispositionFilename(context));
    // }

    static public JSONNode createJSONNode(CallContext context, JsonNode node) throws InvalidJSON {
        return new JSONNode(context, node);
    }

    static public JSONNode createJSONNode(CallContext context, String json_string) throws InvalidJSON {
        return new JSONNode(context, parseJSON(context, json_string));
    }

    static public JSONNode createJSONNode(CallContext context, InputStream input_stream) throws InvalidJSON {
        JSONNode json_node = new JSONNode(context, parseJSON(context, input_stream));
        try {
            input_stream.close();
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not close stream after parsing");
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
        return json_node;
    }

    static public JSONNode createJSONNode(CallContext context, File file) throws InvalidJSON {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            CustomaryContext.create(Context.create(context)).throwPreConditionViolation(context, fnfe, "File '%(file)' does not exist (while creating JSON node)", "file", file.getPath());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        JSONNode json_node = createJSONNode(context, new BufferedInputStream(fis) /* , file.getPath() */);
        try {
            fis.close();
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not close stream after parsing");
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
        return json_node;
    }

    public Vector<JSONNode> getNodes(CallContext context) {
        if (this.json_nodes == null) {
            this.json_nodes = new Vector<JSONNode>();
            for (JsonNode node : this.nodes) {
                this.json_nodes.add(new JSONNode(context, node));
            }
        }
        return this.json_nodes;
    }

    public Vector<JsonNode> getJsonNodes(CallContext context) {
        return this.nodes;
    }

    public JsonNode getFirstNode(CallContext context) {
        return this.first_node;
    }

    public String getName(CallContext context) {
        if (this.name == null) {
            if (this.first_node.isObject()) {
                this.name = this.first_node.get("@Name").asText();
            }
            if (this.name == null) {
                this.name = "";
            }
        }
        return this.name;
    }

    public String getAttribute(CallContext context, String name) {
        return this.first_node.get(name).asText();
    }

    public boolean exists(CallContext context) {
        return this.first_node != null;
    }

    public boolean isArray(CallContext context) {
        if (this.nodes == null) { return false; }
        for (JsonNode node : this.nodes) {
            if (node.isArray()) {
                return true;
            }
            if (node.isObject()) {
            }
        }
        return false;
    }

    public boolean isObject(CallContext context) {
        if (this.nodes == null) { return false; }
        for (JsonNode node : this.nodes) {
            if (node.isObject()) {
                return true;
            }
        }
        return false;
    }

    protected Object toTree(CallContext context, JsonNode node) {
        if (node.isArray()) {
            List l = new ArrayList();
            for (JsonNode child : node) {
                if (child != null) {
                    l.add(this.toTree(context, child));
                }
            }
            return l;
        } else if (node.isObject()) {
            Map m = new HashMap();
            java.util.Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String,JsonNode> entry = fields.next();
                m.put(entry.getKey(), this.toTree(context, entry.getValue()));
            }
            return m;
        } else if (node.isIntegralNumber()) {
            return node.asLong();
        } else if (node.isFloatingPointNumber()) {
            return node.asDouble();
        } else if (node.isTextual()) {
            return node.asText();
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isNull()) {
            return null;
        }
        return null;
    }

    public Object toTree(CallContext context) {
        if (this.nodes == null) { return null; }
        if (this.nodes.size() == 0) { return null; }
        if (this.nodes.size() == 1) { return this.toTree(context, this.nodes.get(0)); }
        List l = new ArrayList();
        for (JsonNode node : this.nodes) {
            l.add(this.toTree(context, node));
        }
        return l;
    }

    public long getSize(CallContext context) {
        if (this.nodes == null) { return 0; }
        long size = 0;
        for (JsonNode node : this.nodes) {
            if (node.isArray()) {
                size += node.size();
            }
        }
        return size;
    }

    public JSONNode getChild(CallContext context, long index) {
        Vector<JsonNode> nodes = new Vector<JsonNode>();
        if (this.nodes != null && (int) index == index) {
            for (JsonNode node : this.nodes) {
                if (node.isArray()) {
                    JsonNode cjn = node.get((int) index);
                    if (cjn != null) {
                        nodes.add(cjn);
                    }
                }
            }
        }
        return new JSONNode(context, nodes);
    }

    public JSONNode getChild(CallContext context, String name) {
        Vector<JsonNode> nodes = new Vector<JsonNode>();
        Long index = null;
        if (this.nodes != null) {
            for (JsonNode node : this.nodes) {
                if (node.isObject()) {
                    JsonNode cjn = node.get(name);
                    if (cjn != null) {
                        nodes.add(cjn);
                    }
                } else if (    node.isArray()
                            && (index = (index != null ? index : name.matches("[0-9]+") ? Long.parseLong(name) : null)) != null) {
                    JsonNode cjn = node.get((int) (long) index);
                    if (cjn != null) {
                        nodes.add(cjn);
                    }
                }
            }
        }
        return new JSONNode(context, nodes);
    }

    public JSONNode getByPath(CallContext context, String path) {
        if (path == null || path.isEmpty()) { return this; }
        int slashpos = path.indexOf("/");
        if (slashpos == -1) {
            return getChild(context, path);
        } else {
            return getChild(context, path.substring(0, slashpos)).getByPath(context, path.substring(slashpos+1));
        }
    }

    public String toString (CallContext context) {
        return JSON.toString(context, this.first_node);
    }

    public String toString () {
        return this.first_node.asText();
    }

    // public String serialise(CallContext context) {
    //     return JSONUtil.serialise(context, this.nodes);
    // }

    // public String serialise(CallContext context, Writer writer) {
    //     return JSONUtil.serialise(context, this.nodes, writer);
    // }

    // public String serialise(CallContext context, TreeLeaf tree_leaf) {
    //     return JSONUtil.serialise(context, this.nodes, tree_leaf);
    // }

    // public String serialise(CallContext context, LSOutput ls_output) {
    //     return JSONUtil.serialise(context, this.nodes, ls_output);
    // }

    // public String serialiseContent(CallContext context) {
    //     return JSONUtil.serialiseContent(context, this.nodes);
    // }

    // public String serialiseContent(CallContext context, Writer writer) {
    //     return JSONUtil.serialiseContent(context, this.nodes, writer);
    // }

    // public String serialiseContent(CallContext context, TreeLeaf tree_leaf) {
    //     return JSONUtil.serialiseContent(context, this.nodes, tree_leaf);
    // }

    // public String serialiseContent(CallContext context, LSOutput ls_output) {
    //     return JSONUtil.serialiseContent(context, this.nodes, ls_output);
    // }

    // protected void processTextNode(CallContext context, Node node, StringBuilder sb) {
    //     if (    node.getNodeType() == Node.TEXT_NODE
    //          || node.getNodeType() == Node.CDATA_SECTION_NODE
    //        ) {
    //         sb.append(((Text) node).getData());
    //         return;
    //     }
    //     CustomaryContext.create((Context)context).throwConfigurationError(context, "JSONNode to render to text does contain DOM node not of type 'Text' (node '%(content)')", "content", this.toString(context));
    //     throw (ExceptionConfigurationError) null; // compiler insists
    // }

    public String toText (CallContext context) {
        if (this.nodes == null) { return ""; }
        StringBuilder sb = new StringBuilder();
        for (JsonNode node : this.nodes) {
            if (node.isValueNode()) {
                sb.append(node.asText());
            } else {
                sb.append(node.toString()); // what is it doing?
            }
        }
        return sb.toString();
    }

    /*
      Baustelle...
      haut so alles noch nicht hin; wegen field names,
      die müßten ja auch mitgespeichert werden, also
      parallel zu nodes array noch ein names_array
      oder so
     
    public JSONNode getChilds(CallContext context) {
        Vector<JsonNode> result_nodes = new Vector<Node>();
        
        int i=0;
        for (JsonNode node : this.nodes) {

            if (node.isObject()) {
                for (String field : node.fieldsNames()) {
                    JsonNode child = node.get(field);
                    result_nodes.add(result.item(n));
                }
            }
            if (node.isArray()) {
            }
        }

        return new JSONNode(context, result_nodes);
    }
    */

    // /**
    //   @param filter they match attributes if name is just name; json:nsuri and json:name as names
    //                 match the namespace uri and name
    // */
    // public JSONNode getChildElementsByRegExp(CallContext context, NamedRegularExpressionFilter... filters) {
    //     Vector<Node> result_nodes = new Vector<Node>();
        
    //     for (Node node : this.nodes) {

    //         NodeList result = node.getChildNodes();

    //         NODES: for (int n=0; n<result.getLength(); n++) {
    //             Node child = result.item(n);
    //             if (child.getNodeType() == Node.ELEMENT_NODE) {                    
    //                 if (filters != null && filters.length != 0) {
    //                     for (NamedRegularExpressionFilter nref : filters) {
    //                         String name = nref.getName(context);
    //                         if (nref.matches(context, 
    //                                          name.equals("json:nsuri") ? child.getNamespaceURI() :
    //                                          name.equals("json:name") ? child.getNodeName() :
    //                                          ((Element)child).getAttribute(name)
    //                                         ) == false) {
    //                             continue NODES;
    //                         }
    //                     }
    //                 }
    //                 result_nodes.add(child);
    //             }
    //         }
    //     }

    //     return new JSONNode(context, result_nodes);
    // }

    // public JSONNode getChildElementsByFilters(CallContext context, NamedRegularExpressionFilter[]... filters) {
    //     JSONNode current = this;
    //     if (filters != null) {
    //         for (NamedRegularExpressionFilter[] filter : filters) {
    //             current = current.getChildElementsByRegExp(context, filter);
    //         }
    //     }
    //     return current;
    // }

    // public boolean isText(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.CDATA_SECTION_NODE
    //                  || nt == Node.TEXT_NODE
    //                );
    //     }
    //     return false;
    // }

    // public boolean isElement(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.ELEMENT_NODE
    //                );
    //     }
    //     return false;
    // }

    // public boolean isComment(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.COMMENT_NODE
    //                );
    //     }
    //     return false;
    // }

    // public boolean isDocument(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.DOCUMENT_NODE
    //                );
    //     }
    //     return false;
    // }

    // public boolean isDocumentType(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.DOCUMENT_TYPE_NODE
    //                );
    //     }
    //     return false;
    // }

    // public boolean isProcessingInstruction(CallContext context) {
    //     if (nodes != null && nodes.size() == 1) {
    //         short nt = nodes.get(0).getNodeType();
    //         return (    nt == Node.PROCESSING_INSTRUCTION_NODE
    //                );
    //     }
    //     return false;
    // }

    // public String getNodeType(CallContext context) {
    //     String result = "";
    //     if (nodes != null) {
    //         for (Node node : nodes) {
    //             if (result != null && result.length() != 0) {
    //                 result += ",";
    //             }
    //             switch (node.getNodeType()) {
    //                 case Node.CDATA_SECTION_NODE :
    //                     result += "CDATA_SECTION_NODE"; break;
    //                 case Node.TEXT_NODE :
    //                     result += "TEXT_NODE"; break;
    //                 case Node.ELEMENT_NODE :
    //                     result += "ELEMENT_NODE"; break;
    //                 case Node.COMMENT_NODE :
    //                     result += "COMMENT_NODE"; break;
    //                 case Node.ATTRIBUTE_NODE  :
    //                     result += "ATTRIBUTE_NODE"; break;
    //                 case Node.DOCUMENT_FRAGMENT_NODE  :
    //                     result += "DOCUMENT_FRAGMENT_NODE"; break;
    //                 case Node.DOCUMENT_NODE  :
    //                     result += "DOCUMENT_NODE"; break;
    //                 case Node.DOCUMENT_TYPE_NODE  :
    //                     result += "DOCUMENT_TYPE_NODE"; break;
    //                 case Node.ENTITY_NODE  :
    //                     result += "ENTITY_NODE"; break;
    //                 case Node.ENTITY_REFERENCE_NODE  :
    //                     result += "ENTITY_REFERENCE_NODE"; break;
    //                 case Node.NOTATION_NODE  :
    //                     result += "NOTATION_NODE"; break;
    //                 case Node.PROCESSING_INSTRUCTION_NODE  :
    //                     result += "PROCESSING_INSTRUCTION_NODE"; break;
    //                 default :
    //                     result += "???"; break;
    //             }
    //         }
    //     }
    //     return result;
    // }

    // public String getNamespace(CallContext context) {
    //     return (nodes != null && nodes.size() == 1 ? nodes.get(0).getNamespaceURI() : null);
    // }

    // protected class MyNamespaceContext implements javax.json.namespace.NamespaceContext {
    //     protected Map<String,String> namespaces;
    //     public MyNamespaceContext(CallContext context, Map<String,String> namespaces) {
    //         this.namespaces = namespaces;
    //     }
    //     public String getNamespaceURI(String prefix) {
    //         if (prefix == null) throw new NullPointerException("Null prefix");
    //         else if ("pre".equals(prefix)) return "http://www.example.org/books";
    //         else if ("json".equals(prefix)) return JSONConstants.JSON_NS_URI;
    //         String nsuri = namespaces == null ? null : namespaces.get(prefix);
    //         if (nsuri != null) { return nsuri; }
    //         return JSONConstants.NULL_NS_URI;
    //     }

    //     // This method isn't necessary for XPath processing.
    //     public String getPrefix(String uri) {
    //         throw new UnsupportedOperationException();
    //     }

    //     // This method isn't necessary for XPath processing either.
    //     public Iterator getPrefixes(String uri) {
    //         throw new UnsupportedOperationException();
    //     }
    // }
    
    // static protected XPathFactory xpath_factory;

    // public JSONNode resolveXPath(CallContext context, String xpath) {
    //     return resolveXPath(context, xpath, null);
    // }

    // public JSONNode resolveXPath(CallContext context, String xpath, Map<String,String> namespaces) {
    //     if (xpath == null || xpath.length() == 0) { return this; }

    //     if (xpath_factory == null) {
    //         xpath_factory = XPathFactory.newInstance();
    //     }
    //     XPath xp = xpath_factory.newXPath();
    //     xp.setNamespaceContext(new MyNamespaceContext(context, namespaces));
    //     XPathExpression xpe = null;
    //     try {
    //         xpe = xp.compile(xpath);
    //     } catch (XPathExpressionException xpee) {
    //         CustomaryContext.create((Context)context).throwConfigurationError(context, xpee, "Could not compile XPath '%(xpath)'", "xpath", xpath);
    //         throw (ExceptionConfigurationError) null; // compiler insists
    //     }

    //     // XPathEvaluator evaluator = new XPathEvaluatorImpl(this.getOwnerDocument(context));

    //     Vector<Node> result_nodes = new Vector<Node>();
        
    //     for (Node node : this.nodes) {
    //         // XPathResult result = (XPathResult) evaluator.evaluate(xpath, node, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
    //         // Node result_node;
    //         // while ((result_node = result.iterateNext()) != null) {
    //         //     result_nodes.add(result_node);
    //         // }

    //         NodeList result = null;
    //         try {
    //             result = (org.w3c.dom.NodeList) xpe.evaluate(node, XPathConstants.NODESET);
    //         } catch (XPathExpressionException xpee) {
    //             CustomaryContext.create((Context)context).throwConfigurationError(context, xpee, "Could not evaluate XPath '%(xpath)'", "xpath", xpath);
    //             throw (ExceptionConfigurationError) null; // compiler insists
    //         }

    //         for (int n=0; n<result.getLength(); n++) {
    //             result_nodes.add(result.item(n));
    //         }
    //     }

    //     return new JSONNode(context, result_nodes);
    // }

    // public JSONNode transform(CallContext context, SourceWithTimestamp transformer_source, Object... parameters) throws TransformationFailure {

    //     Transformer transformer = JSONUtil.getTransformer(context, transformer_source, this.getOwnerDocument(context).getDocumentURI(), parameters);

    //     Vector<Node> result_nodes = new Vector<Node>();
    //     for (Node node : this.nodes) {
    //         DOMSource source = new DOMSource(node);
    //         DOMResult result = new DOMResult();
    //         JSONUtil.transform(context, transformer, source, result);
    //         result_nodes.add(result.getNode());
    //     }

    //     return new JSONNode(context, result_nodes);
    // }

    public java.util.Iterator<JSONNode> getIterator (CallContext context) {
        return null; // this.getNodes(context).iterator();
    }

    public java.lang.Iterable<JSONNode> getIterable (CallContext context) {
        return null; // this.getNodes(context);
    }

    // protected Element getSingleElement(CallContext context) {
    //     if (nodes == null || nodes.size() != 1) {
    //         CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot manipulate JSON nodes that do not contain exactly one DOM node");
    //         throw (ExceptionPreConditionViolation) null; // compiler insists
    //     }

    //     Node node = nodes.get(0);

    //     if (node.getNodeType() != Node.ELEMENT_NODE) {
    //         CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot append to JSON nodes that do not contain a DOM Element");
    //         throw (ExceptionPreConditionViolation) null; // compiler insists
    //     }

    //     return (Element) node;
    // }

    // public void appendElement(CallContext context, String element_name, String... attributes) {
    //     Element element = getSingleElement(context);

    //     Element new_element = getOwnerDocument(context).createElement(element_name);
    //     if (attributes != null) {
    //         for (int a=0; a<attributes.length; a+=2) {
    //             new_element.setAttribute(attributes[a], attributes[a+1]);
    //         }
    //     }

    //     element.appendChild(new_element);
    // }

    // public void setText(CallContext context, String text) {
    //     Element element = getSingleElement(context);
    //     Node child;
    //     while ((child = element.getFirstChild()) != null) {
    //         element.removeChild(child);
    //     }
    //     Text new_text = getOwnerDocument(context).createTextNode(text);        

    //     element.appendChild(new_text);
    // }
}
