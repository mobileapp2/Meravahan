package in.rto.collections.models;

import java.util.ArrayList;

public class MyCarListModel {

    private String type;
    private String message;
    private ArrayList<ResultBean> result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ResultBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String id;
        private String mfg_year;
        private String purchase_date;
        private String kms_covered;
        private String fuel_type;
        private String registration_number;
        private String variant;
        private String model;
        private String vin;
        private String device_id;
        private String make;
        private String vehicle_details_id;
        private String user_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMfg_year() {
            return mfg_year;
        }

        public void setMfg_year(String mfg_year) {
            this.mfg_year = mfg_year;
        }

        public String getPurchase_date() {
            return purchase_date;
        }

        public void setPurchase_date(String purchase_date) {
            this.purchase_date = purchase_date;
        }

        public String getKms_covered() {
            return kms_covered;
        }

        public void setKms_covered(String kms_covered) {
            this.kms_covered = kms_covered;
        }

        public String getFuel_type() {
            return fuel_type;
        }

        public void setFuel_type(String fuel_type) {
            this.fuel_type = fuel_type;
        }

        public String getRegistration_number() {
            return registration_number;
        }

        public void setRegistration_number(String registration_number) {
            this.registration_number = registration_number;
        }

        public String getVariant() {
            return variant;
        }

        public void setVariant(String variant) {
            this.variant = variant;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getVehicle_details_id() {
            return vehicle_details_id;
        }

        public void setVehicle_details_id(String vehicle_details_id) {
            this.vehicle_details_id = vehicle_details_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
