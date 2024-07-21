package Model;

/**
 * Represents the details of a column in a database table.
 * <p>
 * This class encapsulates the type, constraints, and references associated with a column.
 * </p>
 */
public class ColumnDetail {
    private final String type;
    private final String constraints;
    private final String reference;
    /**
     * Constructs a {@link ColumnDetail} object with the specified type, constraints, and reference.
     * @param type The data type of the column.
     * @param constraints The constraints applied to the column (e.g., NOT NULL, UNIQUE).
     * @param reference The reference to another column or table, if applicable (e.g., FOREIGN KEY reference).
     */
    public ColumnDetail(String type, String constraints, String reference) {
        this.type = type;
        this.constraints = constraints;
        this.reference = reference;
    }
    /**
     * Gets the data type of the column.
     * @return The column type.
     */
    public String getType() {
        return type;
    }
    /**
     * Gets the constraints applied to the column.
     * @return The column constraints.
     */
    public String getConstraints() {
        return constraints;
    }
    /**
     * Gets the reference associated with the column.
     * @return The column reference.
     */
    public String getReference() {
        return reference;
    }
    /**
     * Returns a string representation of the {@link ColumnDetail} object.
     * <p>
     * The string includes the type, constraints, and reference, if available.
     * </p>
     * @return A string representation of the column details.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null) sb.append("TYPE=").append(type).append(" ");
        if (constraints != null) sb.append("CONSTRAINT=").append(constraints).append(" ");
        if (reference != null) sb.append("REFERENCE=").append(reference).append(" ");
        return sb.toString().trim();
    }
}
