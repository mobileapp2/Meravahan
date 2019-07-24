package in.rto.collections.models;

import java.util.ArrayList;

public class CariqNotificationModel {

    private String pageNum;
    private String noOfRecords;
    private String totalPages;
    private ArrayList<RowsBean> rows;

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getNoOfRecords() {
        return noOfRecords;
    }

    public void setNoOfRecords(String noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public ArrayList<RowsBean> getRows() {
        return rows;
    }

    public void setRows(ArrayList<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean {
        private String id;
        private String message;
        private String logtime;
        private String itsPid;
        private String itsType;
        private String itsValue;
        private String isSeen;
        private String vehicleId;
        private String latitude;
        private String longitude;
        private String address;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLogtime() {
            return logtime;
        }

        public void setLogtime(String logtime) {
            this.logtime = logtime;
        }

        public String getItsPid() {
            return itsPid;
        }

        public void setItsPid(String itsPid) {
            this.itsPid = itsPid;
        }

        public String getItsType() {
            return itsType;
        }

        public void setItsType(String itsType) {
            this.itsType = itsType;
        }

        public String getItsValue() {
            return itsValue;
        }

        public void setItsValue(String itsValue) {
            this.itsValue = itsValue;
        }

        public String isIsSeen() {
            return isSeen;
        }

        public void setIsSeen(String isSeen) {
            this.isSeen = isSeen;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
        }

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
