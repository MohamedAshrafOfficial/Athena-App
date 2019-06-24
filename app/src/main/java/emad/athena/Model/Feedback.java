package emad.athena.Model;

public class Feedback {
    String message;
    String msgDate;

    public Feedback(String message, String msgDate) {
        this.message = message;
        this.msgDate = msgDate;
    }

    public Feedback() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }
}

