package org.json;

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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class JSONObjectComparator implements Comparator {
    public JSONObjectComparator( String property, PropertyType propertyType, SortDirection sortDirection){
        this.property = property;
        this.propertyType = propertyType;
        this.sortDirection = sortDirection;
    }
    
    public int compare(Object o1, Object o2) {
        int rc = 0;
        if (o1 instanceof JSONObject && o2 instanceof JSONObject) {
            JSONObject jo1 = (JSONObject)o1;
            JSONObject jo2 = (JSONObject)o2;
            if (this.propertyType == PropertyType.string) {
                try {
                    rc = jo1.getString(this.property).compareTo(jo2.getString(property));
                } catch ( Exception ex) {
                }
                return rc*this.getSortFactor();
            }
            if (this.propertyType == PropertyType.integer) {
                try {
                    Integer i1 = new Integer(jo1.getInt(property));
                    Integer i2 = new Integer(jo2.getInt(property));
                    rc = i1.compareTo(i2)*this.getSortFactor();
                } catch ( Exception ex) {
                }
                return rc;
            }
            if (this.propertyType == PropertyType.date) {
                try {
                    Date d1 = this.sdf.parse(jo1.getString(property));
                    Date d2 = this.sdf.parse(jo2.getString(property));
                    rc = d1.compareTo(d2)*this.getSortFactor();
                } catch ( Exception ex) {
                }
                return rc;
            }
        }
        return 0;
    }
    
    private int 
    getSortFactor(){
        if( this.sortDirection == SortDirection.DESC){
            return -1;
        }
        return 1;
    }
    
    private String property = null;
    private SortDirection sortDirection = SortDirection.DESC;
    private PropertyType propertyType = PropertyType.string;
    private SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy");

    public enum SortDirection{
        ASC,
        DESC
    }
    public enum PropertyType{
        integer,
        string,
        date
    }
}
