package by.aliakseishysh.pinfo.command;

public enum PoliceApi {
    ALL_CRIME("https://data.police.uk/api/crimes-street/all-crime");
    // ALL_CRIME("/api/crimes-street/all-crime"); for socket

    private final String api;

    PoliceApi(String api) {
        this.api = api;
    }

    public String getApi() {
        return api;
    }
}
