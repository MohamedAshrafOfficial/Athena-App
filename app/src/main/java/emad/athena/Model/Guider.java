package emad.athena.Model;

import java.io.Serializable;

public class Guider implements Serializable {
    String action;
    String explain;

    public Guider() {
    }

    public Guider(String action, String explain) {
        this.action = action;
        this.explain = explain;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
