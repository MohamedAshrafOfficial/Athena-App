package emad.athena.Model;

public class App {
    String AppName;
    String packageName;

    public App( String appName, String packageName) {
        AppName = appName;
        this.packageName = packageName;
    }


    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}

