package Model;

public class ColumnDetail {
    private final String type;
    private final String constraints;
    private final String reference;

    public ColumnDetail(String type, String constraints, String reference) {
        this.type = type;
        this.constraints = constraints;
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public String getConstraints() {
        return constraints;
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null) sb.append("TYPE=").append(type).append(" ");
        if (constraints != null) sb.append("CONSTRAINT=").append(constraints).append(" ");
        if (reference != null) sb.append("REFERENCE=").append(reference).append(" ");
        return sb.toString().trim();
    }
}
