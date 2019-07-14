package in.rto.collections.models;

public class TripListModel {


    /**
     * tripDate : 2019-07-11
     * kmCovered : 14.609274942737475
     * avgSpeed : 19.562623797122896
     */

    private String tripDate;
    private String kmCovered;
    private String avgSpeed;

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getKmCovered() {
        return kmCovered;
    }

    public void setKmCovered(String kmCovered) {
        this.kmCovered = kmCovered;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}
