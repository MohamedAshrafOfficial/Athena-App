package emad.athena.Model;

public class Recent {
    String firebaseid;
    String question;
    String response;
    String date;
    Boolean expanded;

    public Recent() {
    }

    public Recent(String question, String response, String date, Boolean expanded) {
        this.question = question;
        this.response = response;
        this.date = date;
        this.expanded = expanded;
    }

    public Recent(String firebaseid, String question, String response, String date, Boolean expanded) {
        this.firebaseid = firebaseid;
        this.question = question;
        this.response = response;
        this.date = date;
        this.expanded = expanded;
    }

    public String getFirebaseid() {
        return firebaseid;
    }

    public void setFirebaseid(String firebaseid) {
        this.firebaseid = firebaseid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }
}

