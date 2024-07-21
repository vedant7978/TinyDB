package Model;

public class ColumnDetail {
    private final String type;
    private final String constraints;
    private final String reference;
    private final String relation;

    public ColumnDetail(String type, String constraints, String reference, String relation) {
        this.type = type;
        this.constraints = constraints;
        this.reference = reference;
        this.relation = relation;
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

    public String getRelation() {
        return relation;
    }
}