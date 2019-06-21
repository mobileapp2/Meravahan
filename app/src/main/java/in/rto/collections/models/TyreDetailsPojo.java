package in.rto.collections.models;

import java.io.Serializable;
import java.util.ArrayList;

public class TyreDetailsPojo implements Serializable {

    private String id;
    private String state_name;
    private String state_id;
    private String vehicle_no;
    private String type_id;
    private String type_name;
    private String client_id;
    private String client_name;
    private String remark;
    private String description;
    private String tyre_no;
    private String tyre_replacement_date;
    private String tyre_remounding_date;
    private String purchase_date;
    private String updated_by;
    private String created_by;
    private String created_at;
    private String updated_at;

    private ArrayList<TyreDetailsPojo.OtherDatesListPojo> other_date;
    private ArrayList<TyreDetailsPojo.DocumentListPojo> document;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getStateId() {
        return state_id;
    }

    public String getStateName() {
        return state_name;
    }

    public void setStateName(String state_name) {
        this.state_name = state_name;
    }

    public void type_id(String type_id) {
        this.type_id = type_id;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTyre_no() {
        return tyre_no;
    }

    public void setTyre_no(String tyre_no) {
        this.tyre_no = tyre_no;
    }

    public String getTyre_replacement_date() {
        return tyre_replacement_date;
    }

    public void setTyre_replacement_date(String tyre_replacement_date) {
        this.tyre_replacement_date = tyre_replacement_date;
    }

    public String getTyre_remounding_date() {
        return tyre_remounding_date;
    }

    public void setTyre_remounding_date(String tyre_remounding_date) {
        this.tyre_remounding_date = tyre_remounding_date;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }


    public ArrayList<TyreDetailsPojo.OtherDatesListPojo> getOther_date() {
        return other_date;
    }

    public void setOther_date(ArrayList<TyreDetailsPojo.OtherDatesListPojo> other_date) {
        this.other_date = other_date;
    }

    public ArrayList<TyreDetailsPojo.DocumentListPojo> getDocument() {
        return document;
    }

    public void setDocument(ArrayList<TyreDetailsPojo.DocumentListPojo> document) {
        this.document = document;
    }


    public static class DocumentListPojo implements Serializable {
        private String document;
        private String document_id;

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public String getDocument_id() {
            return document_id;
        }

        public void setDocument_id(String document_id) {
            this.document_id = document_id;
        }

        @Override
        public String toString() {
            return "ClassPojo [document = " + document + "]";
        }
    }

    public static class OtherDatesListPojo implements Serializable {
        private String other_date;
        private String other_date_id;
        private String text;

        public String getOther_date() {
            return other_date;
        }

        public void setOther_date(String other_date) {
            this.other_date = other_date;
        }

        public String getOther_date_id() {
            return other_date_id;
        }

        public void setOther_date_id(String other_date_id) {
            this.other_date_id = other_date_id;
        }


        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}

