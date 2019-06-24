package emad.athena.Model;

public class ArabicOCR {
    String status;
    String[] Text;

    public ArabicOCR() {
    }

    public ArabicOCR(String status, String[] text) {
        this.status = status;
        Text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getText() {
        return Text;
    }

    public void setText(String[] text) {
        Text = text;
    }
}