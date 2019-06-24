package emad.athena.Model;

public class ChatbotResponse {
    String status;
    String sentiment;
    String Reply;

    public ChatbotResponse() {
    }

    public ChatbotResponse(String status, String sentiment, String reply) {
        this.status = status;
        this.sentiment = sentiment;
        Reply = reply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getReply() {
        return Reply;
    }

    public void setReply(String reply) {
        Reply = reply;
    }
}
