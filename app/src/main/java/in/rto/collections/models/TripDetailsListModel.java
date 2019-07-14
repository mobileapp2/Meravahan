package in.rto.collections.models;

import java.io.Serializable;
import java.util.ArrayList;

public class TripDetailsListModel implements Serializable {

    private String itsDate;
    private ItsSpeedDetailBean itsSpeedDetail;
    private ArrayList<ItsLastLocationListBean> itsLastLocationList;

    public String getItsDate() {
        return itsDate;
    }

    public void setItsDate(String itsDate) {
        this.itsDate = itsDate;
    }

    public ItsSpeedDetailBean getItsSpeedDetail() {
        return itsSpeedDetail;
    }

    public void setItsSpeedDetail(ItsSpeedDetailBean itsSpeedDetail) {
        this.itsSpeedDetail = itsSpeedDetail;
    }

    public ArrayList<ItsLastLocationListBean> getItsLastLocationList() {
        return itsLastLocationList;
    }

    public void setItsLastLocationList(ArrayList<ItsLastLocationListBean> itsLastLocationList) {
        this.itsLastLocationList = itsLastLocationList;
    }

    public static class ItsSpeedDetailBean implements Serializable {

        private String avgSpeed;
        private String maxSpeed;
        private String maxRpm;
        private String avgEngineLoad;
        private String avgDailyCommute;
        private String distanceTraveled;

        public String getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(String avgSpeed) {
            this.avgSpeed = avgSpeed;
        }

        public String getMaxSpeed() {
            return maxSpeed;
        }

        public void setMaxSpeed(String maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        public String getMaxRpm() {
            return maxRpm;
        }

        public void setMaxRpm(String maxRpm) {
            this.maxRpm = maxRpm;
        }

        public String getAvgEngineLoad() {
            return avgEngineLoad;
        }

        public void setAvgEngineLoad(String avgEngineLoad) {
            this.avgEngineLoad = avgEngineLoad;
        }

        public String getAvgDailyCommute() {
            return avgDailyCommute;
        }

        public void setAvgDailyCommute(String avgDailyCommute) {
            this.avgDailyCommute = avgDailyCommute;
        }

        public String getDistanceTraveled() {
            return distanceTraveled;
        }

        public void setDistanceTraveled(String distanceTraveled) {
            this.distanceTraveled = distanceTraveled;
        }
    }

    public static class ItsLastLocationListBean implements Serializable {

        private String latitude;
        private String longitude;
        private String itsTimeStamp;
        private String accelerometerX;
        private String accelerometerY;
        private String accelerometerZ;
        private String accelerometerG;
        private String accelerometer;
        private String gpggaRecord;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getItsTimeStamp() {
            return itsTimeStamp;
        }

        public void setItsTimeStamp(String itsTimeStamp) {
            this.itsTimeStamp = itsTimeStamp;
        }

        public String getAccelerometerX() {
            return accelerometerX;
        }

        public void setAccelerometerX(String accelerometerX) {
            this.accelerometerX = accelerometerX;
        }

        public String getAccelerometerY() {
            return accelerometerY;
        }

        public void setAccelerometerY(String accelerometerY) {
            this.accelerometerY = accelerometerY;
        }

        public String getAccelerometerZ() {
            return accelerometerZ;
        }

        public void setAccelerometerZ(String accelerometerZ) {
            this.accelerometerZ = accelerometerZ;
        }

        public String getAccelerometerG() {
            return accelerometerG;
        }

        public void setAccelerometerG(String accelerometerG) {
            this.accelerometerG = accelerometerG;
        }

        public String getAccelerometer() {
            return accelerometer;
        }

        public void setAccelerometer(String accelerometer) {
            this.accelerometer = accelerometer;
        }

        public String getGpggaRecord() {
            return gpggaRecord;
        }

        public void setGpggaRecord(String gpggaRecord) {
            this.gpggaRecord = gpggaRecord;
        }
    }
}
