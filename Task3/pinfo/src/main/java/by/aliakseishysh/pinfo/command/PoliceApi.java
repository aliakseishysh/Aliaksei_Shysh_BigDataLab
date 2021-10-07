package by.aliakseishysh.pinfo.command;

public enum PoliceApi {
    ALL_CRIME("https://data.police.uk/api/crimes-street/all-crime");

    private final String api;

    PoliceApi(String api) {
        this.api = api;
    }

    public String getApi() {
        return api;
    }
}
