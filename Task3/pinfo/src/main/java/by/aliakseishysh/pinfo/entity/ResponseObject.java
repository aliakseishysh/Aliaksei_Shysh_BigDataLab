package by.aliakseishysh.pinfo.entity;

public class ResponseObject {

    private String category;
    private String persistentId;
    private String month;
    private Location location;
    private String context;
    private long id;
    private String locationType;
    private String locationSubtype;
    private OutcomeStatus outcomeStatus;

    private ResponseObject() {
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
        final StringBuilder sb = new StringBuilder("ResponseObject{");
        sb.append("category='").append(category).append('\'');
        sb.append(", persistentId='").append(persistentId).append('\'');
        sb.append(", month='").append(month).append('\'');
        sb.append(", location=").append(location);
        sb.append(", context='").append(context).append('\'');
        sb.append(", id=").append(id);
        sb.append(", locationType='").append(locationType).append('\'');
        sb.append(", locationSubtype='").append(locationSubtype).append('\'');
        sb.append(", outcomeStatus=").append(outcomeStatus);
        sb.append('}');
        return sb.toString();
    }

    public class Location {
        private String latitude;
        private Street street;
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
            final StringBuilder sb = new StringBuilder("Location{");
            sb.append("latitude='").append(latitude).append('\'');
            sb.append(", street=").append(street);
            sb.append(", longtitude='").append(longtitude).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public class Street {
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
                final StringBuilder sb = new StringBuilder("Street{");
                sb.append("id=").append(id);
                sb.append(", name='").append(name).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
    }

    public class OutcomeStatus {
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
            final StringBuilder sb = new StringBuilder("OutcomeStatus{");
            sb.append("category='").append(category).append('\'');
            sb.append(", date='").append(date).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static Builder newBuilder() {
        return new ResponseObject().new Builder();
    }

    public class Builder {
        private Builder() {
        }
        public Builder setCategory(String category) {
            ResponseObject.this.category = category;
            return this;
        }
        public Builder setPersistentId(String persistentId) {
            ResponseObject.this.persistentId = persistentId;
            return this;
        }
        public Builder setMonth(String month) {
            ResponseObject.this.month = month;
            return this;
        }
        public Builder setLocationLatitude(String latitude) {
            ResponseObject.this.location.latitude = latitude;
            return this;
        }
        public Builder setLocationStreetId(long id) {
            ResponseObject.this.location.street.id = id;
            return this;
        }
        public Builder setLocationStreetName(String name) {
            ResponseObject.this.location.street.name = name;
            return this;
        }
        public Builder setLocationLongtitude(String longtitude) {
            ResponseObject.this.location.longtitude = longtitude;
            return this;
        }
        public Builder setContext(String context) {
            ResponseObject.this.context = context;
            return this;
        }
        public Builder setId(long id) {
            ResponseObject.this.id = id;
            return this;
        }
        public Builder setLocationType(String locationType) {
            ResponseObject.this.locationType = locationType;
            return this;
        }
        public Builder setLocationSubtype(String locationSubtype) {
            ResponseObject.this.locationSubtype = locationSubtype;
            return this;
        }
        public Builder setOutcomeStatusCategory(String category) {
            ResponseObject.this.outcomeStatus.category = category;
            return this;
        }
        public Builder setOutcomeStatusDate(String date) {
            ResponseObject.this.outcomeStatus.date = date;
            return this;
        }
        public ResponseObject build() {
            return ResponseObject.this;
        }

    }

}
