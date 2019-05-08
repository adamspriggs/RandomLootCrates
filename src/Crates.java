import java.util.ArrayList;
import Crates.*;

public class Crates {
    private ArrayList<Crate> cratesList;

    public Crates() {
        cratesList = new ArrayList<Crate>();
    }

    public ArrayList<Crate> getCratesList() {
        return cratesList;
    }

    public Crate addCrate(Crate c) {
        cratesList.add(c);
        return c;
    }

    public Crate getCrate(String name) {
        for (Crate c : cratesList) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
