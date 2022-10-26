package lmp;

public class Advancement {
    String ID;
    String name;
    String criteria;

    public Advancement(String ID, String name, String criteria) {
        this.ID = ID;
        this.name = name;
        this.criteria = criteria;
    }

    public String getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public String getCriteria() {
        return this.criteria;
    }

}
