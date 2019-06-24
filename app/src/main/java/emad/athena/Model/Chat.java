package emad.athena.Model;

import android.net.Uri;

public class Chat {

    private String message;
    private int flag;
    private Uri uri;
    private String profileImage;

    public Chat(String message, int flag, Uri uri, String profileImage) {
        this.message = message;
        this.flag = flag;
        this.uri = uri;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}

