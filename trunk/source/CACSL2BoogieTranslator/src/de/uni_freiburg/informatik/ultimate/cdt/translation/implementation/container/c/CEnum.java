/**
 * Describes an enum given in C.
 */
package de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c;

import java.util.Arrays;

import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPrimitive.PRIMITIVE;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

/**
 * @author Markus Lindenmann
 * @author nutz
 * @date 18.09.2012
 */
public class CEnum extends CType {
    /**
     * Field names.
     */
    private final String[] fNames;
    /**
     * Field values.
     */
    private final IntegerLiteral[] fValues;
    /**
     * The _boogie_ identifier of this enum set.
     */
    private final String identifier;

    /**
     * Constructor.
     * 
     * @param fNames
     *            field names.
     * @param fValues
     *            field values.
     * @param cDeclSpec
     *            the C declaration used.
     * @param id
     *            this enums identifier.
     */
    public CEnum(String id, String[] fNames,
            IntegerLiteral[] fValues) {
        super(false, false, false, false); //FIXME: integrate those flags
        assert fNames.length == fValues.length;
        this.identifier = id;
        this.fNames = fNames;
        this.fValues = fValues;
    }

    /**
     * Get the number of fields in this enum.
     * 
     * @return the number of fields.
     */
    public int getFieldCount() {
        return fNames.length;
    }

    /**
     * Returns the field value.
     * 
     * @param id
     *            the fields id.
     * @return the fields value.
     */
    public IntegerLiteral getFieldValue(String id) {
        int idx = Arrays.asList(fNames).indexOf(id);
        if (idx < 0) {
            throw new IllegalArgumentException("Field '" + id
                    + "' not in struct!");
        }
        return fValues[idx];
    }

    /**
     * Returns the set of fields in this enum.
     * 
     * @return the set of fields in this enum.
     */
    public String[] getFieldIds() {
        return fNames.clone();
    }

    /**
     * Getter for this enums identifier.
     * 
     * @return this enums identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
//        StringBuilder id = new StringBuilder("ENUM#");
//        for (int i = 0; i < getFieldCount(); i++) {
//            id.append("?");
//            id.append(fNames[i]);
//        }
//        id.append("#");
//        return id.toString();
    	return identifier;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CType)) {
            return false;
        }
        CType oType = ((CType)o).getUnderlyingType();
        if (!(oType instanceof CEnum)) {
            return false;
        }
        
        CEnum oEnum = (CEnum)oType;
        if (!(identifier.equals(oEnum.identifier))) {
            return false;
        }
        if (fNames.length != oEnum.fNames.length) {
            return false;
        }
        for (int i = fNames.length - 1; i >= 0; --i) {
            if (!(fNames[i].equals(oEnum.fNames[i]))) {
                return false;
            }
        }
        return true;
    }

	@Override
	public boolean isCompatibleWith(CType o) {
		if (o instanceof CPrimitive &&
				((CPrimitive) o).getType() == PRIMITIVE.VOID)
			return true;
		
		CType oType = ((CType)o).getUnderlyingType();
        if (!(oType instanceof CEnum)) 
            return false;
        
        CEnum oEnum = (CEnum)oType;
        if (!(identifier.equals(oEnum.identifier))) {
            return false;
        }
        if (fNames.length != oEnum.fNames.length) {
            return false;
        }
        for (int i = fNames.length - 1; i >= 0; --i) {
            if (!(fNames[i].equals(oEnum.fNames[i]))) {
                return false;
            }
        }
        return true;
	}
	
	@Override
	public int hashCode() {
		return HashUtils.hashJenkins(31, fNames, fValues, identifier);
	}
}
