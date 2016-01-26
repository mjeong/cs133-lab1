package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }
    
    private TDItem[] td;
    private int ts;

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
    	Iterator<TDItem> it = new Iterator<TDItem>() {
    		
    		private int currentIndex = 0;
    		
    		@Override
    		public boolean hasNext() {
    			return currentIndex < td.length;
    		}
    		
    		@Override
    		public TDItem next() {
    			return td[currentIndex++];
    		}
    		
    		@Override
    		public void remove() {
    			throw new UnsupportedOperationException();
    		}
    		
    	};
        return it;
    }

    private static final long serialVersionUID = 1L;
    
    

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        td = new TDItem[typeAr.length];
        Type myType;
        for(int i = 0; i < typeAr.length; i++) {
        	myType = typeAr[i];
        	td[i] = new TDItem(myType, fieldAr[i]);
        	ts += myType.getLen();
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        td = new TDItem[typeAr.length];
        Type myType;
        for(int i = 0; i < typeAr.length; i++) {
        	myType = typeAr[i];
        	td[i] = new TDItem(myType, "");
        	ts += myType.getLen();
        }
    }
   

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return td.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
    	if (i > numFields()) {
    		throw new NoSuchElementException();
    	}
        return td[i].fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if (i > numFields()) {
        	throw new NoSuchElementException();
        }
        return td[i].fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
    	for (int i = 0; i < td.length; i++) {
    		if (name.equals(td[i].fieldName)) {
    			return i;
    		}
    	}
        throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        return ts;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
    	int leftLength = td1.numFields();
    	int rightLength = td2.numFields();
    	int numFields = leftLength + rightLength;
        Type[] tdrType = new Type[numFields];
        String[] tdrName = new String[numFields];
        int i = 0;
        for(i = 0; i < leftLength; i++) {
        	tdrType[i] = td1.getFieldType(i);
        	tdrName[i] = td1.getFieldName(i);
        }
        for(int j = 0; j < rightLength; j++, i++) {
        	tdrType[i] = td2.getFieldType(j);
        	tdrName[i] = td2.getFieldName(j);
        }
        return new TupleDesc(tdrType, tdrName);
    }

	/**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass())) {
        	return false;
        }
        TupleDesc tdO = ((TupleDesc) o);
        if (tdO.getSize() != this.getSize()) {
    		return false;
    	}
        else {
        	for(int i = 0; i < this.numFields(); i++) {
        		if(!this.getFieldType(i).equals(tdO.getFieldType(i))) {
        			return false;
        		}
        	}
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldName[0](fieldType[0]), ..., fieldName[M](fieldType[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        StringBuilder tdBuilder = new StringBuilder();
        for(TDItem tdi : td) {
        	tdBuilder.append(tdi.toString());
        }
        return tdBuilder.toString();
    }
}
