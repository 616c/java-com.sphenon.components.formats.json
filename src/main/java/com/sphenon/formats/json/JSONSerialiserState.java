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
import com.sphenon.basics.state.*;
import com.sphenon.basics.state.classes.*;

import java.io.IOException;

public class JSONSerialiserState {
    protected JSONSerialiser serialiser;

    public JSONSerialiserState(CallContext context, JSONSerialiser serialiser) {
        this.serialiser = serialiser;
        this.serialisation_state = this.serialiser.getSerialisationState(context);
    }

    public StateCondition[] prepareStateConditions(CallContext context, StateCondition[] prepared_conditions, String... conditions) {
        if (prepared_conditions != null) {
            return prepared_conditions;
        }
        if (conditions == null) {
            return null;
        }
        prepared_conditions = new StateCondition[conditions.length];
        int i=0;
        for (String condition: conditions) {
            prepared_conditions[i++] = new StateComplexCondition(context, condition);
        }
        return prepared_conditions;
    }

    protected State serialisation_state;

    public State getSerialisationState(CallContext context) {
        return this.serialisation_state;
    }

    protected StateComplex local_state;

    public StateComplex getLocalState(CallContext context) {
        if (this.local_state != null) {
            return this.local_state;
        } else {
            if (this.serialisation_state != null) {
                this.local_state = ((StateComplex) serialisation_state).clone(context);
            } else {
                this.local_state = new StateComplex(context, null, null, null, null, true);
            }
            this.serialiser.pushSerialisationState(context, this.local_state);
            return this.local_state;
        }
    }

    public void cleanupLocalState(CallContext context) {
        if (this.local_state != null) {
            this.serialiser.popSerialisationState(context);
            this.local_state = null;
        }
    }
}
