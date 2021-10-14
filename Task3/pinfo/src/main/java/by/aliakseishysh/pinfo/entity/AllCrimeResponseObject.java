package by.aliakseishysh.pinfo.entity;

/**
 * Represents json response object from all-crime api
 */
public class AllCrimeResponseObject {

    private String category;
    private String persistentId;
    private String month;
    private final Location location;
    private String context;
    private long id;
    private String locationType;
    private String locationSubtype;
    private final OutcomeStatus outcomeStatus;

    private AllCrimeResponseObject() {
        this.location = new Location();
        this.outcomeStatus = new OutcomeStatus();
    }

    public String getCategory() {
        return category;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public String getMonth() {
        return month;
    }

    public Location getLocation() {
        return location;
    }

    public String getContext() {
        return context;
    }

    public long getId() {
        return id;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getLocationSubtype() {
        return locationSubtype;
    }

    public OutcomeStatus getOutcomeStatus() {
        return outcomeStatus;
    }

    @Override
    public String toString() {
        return "ResponseObject{" + "category='" + category + '\'' +
                ", persistentId='" + persistentId + '\'' +
                ", month='" + month + '\'' +
                ", location=" + location +
                ", context='" + context + '\'' +
                ", id=" + id +
                ", locationType='" + locationType + '\'' +
                ", locationSubtype='" + locationSubtype + '\'' +
                ", outcomeStatus=" + outcomeStatus +
                '}';
    }

    public static class Location {
        private String latitude;
        private final Street street;
        private String longtitude;

        private Location() {
            this.street = new Street();
        }

        public String getLatitude() {
            return latitude;
        }

        public Street getStreet() {
            return street;
        }

        public String getLongtitude() {
            return longtitude;
        }

        @Override
        public String toString() {
            return "Location{" + "latitude='" + latitude + '\'' +
                    ", street=" + street +
                    ", longtitude='" + longtitude + '\'' +
                    '}';
        }

        public static class Street {
            private long id;
            private String name;

            public long getId() {
                return id;
            }

            public String getName() {
                return name;
            }


            @Override
            public String toString() {
                return "Street{" + "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }

    public static class OutcomeStatus {
        private String category;
        private String date;

        public String getCategory() {
            return category;
        }

        public String getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "OutcomeStatus{" + "category='" + category + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    public static Builder newBuilder() {
        return new AllCrimeResponseObject().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setCategory(String category) {
            AllCrimeResponseObject.this.category = category;
            return this;
        }

        public Builder setPersistentId(String persistentId) {
            AllCrimeResponseObject.this.persistentId = persistentId;
            return this;
        }

        public Builder setMonth(String month) {
            AllCrimeResponseObject.this.month = month;
            return this;
        }

        public Builder setLocationLatitude(String latitude) {
            AllCrimeResponseObject.this.location.latitude = latitude;
            return this;
        }

        public Builder setLocationStreetId(long id) {
            AllCrimeResponseObject.this.location.street.id = id;
            return this;
        }

        public Builder setLocationStreetName(String name) {
            AllCrimeResponseObject.this.location.street.name = name;
            return this;
        }

        public Builder setLocationLongtitude(String longtitude) {
            AllCrimeResponseObject.this.location.longtitude = longtitude;
            return this;
        }

        public Builder setContext(String context) {
            AllCrimeResponseObject.this.context = context;
            return this;
        }

        public Builder setId(long id) {
            AllCrimeResponseObject.this.id = id;
            return this;
        }

        public Builder setLocationType(String locationType) {
            AllCrimeResponseObject.this.locationType = locationType;
            return this;
        }

        public Builder setLocationSubtype(String locationSubtype) {
            AllCrimeResponseObject.this.locationSubtype = locationSubtype;
            return this;
        }

        public Builder setOutcomeStatusCategory(String category) {
            AllCrimeResponseObject.this.outcomeStatus.category = category;
            return this;
        }

        public Builder setOutcomeStatusDate(String date) {
            AllCrimeResponseObject.this.outcomeStatus.date = date;
            return this;
        }

        public AllCrimeResponseObject build() {
            return AllCrimeResponseObject.this;
        }

    }

}
