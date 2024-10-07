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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import java.util.Iterator;
import java.lang.Iterable;

public class JSONNodeExistenceCheck {

    protected CallContext context;
    protected JSONNode json_node;

    public JSONNodeExistenceCheck (CallContext context, Object object) {
        this.context = context;
        this.json_node = (JSONNode) object;
    }

    public boolean exists(CallContext context) {
        return (this.json_node != null && this.json_node.exists(context));
    }

    public boolean notexists(CallContext context) {
        return ! exists(context);
    }

    public JSONNode getValue(CallContext context) {
        return this.json_node;
    }

    public boolean notempty(CallContext context) {
        return (    this.exists(context)
                 && (    (this.json_node.isArray(context) && this.json_node.getSize(context) != 0)
                      || (this.json_node.isObject(context))
                    )
               );
    }

    public boolean empty(CallContext context) {
        return ! notempty(context);
    }
}
