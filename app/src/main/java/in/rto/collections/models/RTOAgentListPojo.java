package in.rto.collections.models;

import java.io.Serializable;
import java.util.ArrayList;

public class RTOAgentListPojo implements Serializable {

    private String id;
    private String state_name;
    private String state_id;
    private String vehicle_no;
    private String vehicle_owner;
    private String type_id;
    private String type_name;
    private String client_id;
    private String client_name;
    private String vehicle_dealer_id;
    private String vehicle_dealer_name;
    private String engine_no;
    private String chassis_no;
    private String insurance_policy_no;
    private String insurance_renewal_date;
    private String tax_paid_up_to;
     private String permit_valid_upto;
     private String state_permit_valid_upto;
    private String national_permit_valid_upto;
     private String puc_renewal_date;
    private String fittness_valid_upto;
    private String remark;
    private String description;
    private String doc_name;
    private String updated_by;
    private String created_by;
    private String created_at;
    private String updated_at;
    private String importR;
    private String isimport;
    private String isshowto_dealer;
    private String isshowto_customer;
    private String createrName;
    private String vehicle_image;
    private String vehicle_image_url;
    public boolean isChecked;

    private ArrayList<RTOAgentListPojo.OtherDatesListPojo> other_date;
    private ArrayList<RTOAgentListPojo.DocumentListPojo> document;

    public String getId() {
        return id;
    }

    public void setIsimport(String isimport) {
        this.isimport = isimport;
    }
    public String getIsimport() {
        return isimport;
    }

    public void setIsshowto_dealer(String isshowto_dealer) {
        this.isshowto_dealer = isshowto_dealer;
    }
    public String getIsshowto_dealer() {
        return isshowto_dealer;
    }
    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public void setIsshowto_customer(String isshowto_customer) {
        this.isshowto_customer = isshowto_customer;
    }
    public String getIsshowto_customer() {
        return isshowto_customer;
    }


    public void setImportR(String importR) {
        this.importR = importR;
    }
    public String getImportR() {
        return importR;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public void setState_id(String state_id) {
        this.state_id = state_id;
    }
    public  String getStateId(){return state_id;}
    public  String getStateName(){return state_name;}
    public void setStateName(String state_name){ this.state_name = state_name; }


    public String getVehicle_no() {
        return vehicle_no;
    }


    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getVehicle_owner() {
        return vehicle_owner;
    }

    public void setVehicle_owner(String vehicle_owner) {
        this.vehicle_owner = vehicle_owner;
    }

    public String getVehicle_dealer_id() {
        return vehicle_dealer_id;
    }

    public void setVehicle_dealer_id(String vehicle_dealer_id) {
        this.vehicle_dealer_id = vehicle_dealer_id;
    }

    public String getVehicle_dealer_name() {
        return vehicle_dealer_name;
    }

    public void setVehicle_dealer_name(String vehicle_dealer_name) {
        this.vehicle_dealer_name = vehicle_dealer_name;
    }

    public String getTax_paid_up_to() {
        return tax_paid_up_to;
    }

    public void setTax_paid_up_to(String tax_paid_up_to) {
        this.tax_paid_up_to = tax_paid_up_to;
    }

    public String getPermit_valid_upto() {
        return permit_valid_upto;
    }

    public void setPermit_valid_upto(String permit_valid_upto) {
        this.permit_valid_upto = permit_valid_upto;
    }

    public String getState_permit_valid_upto() {
        return state_permit_valid_upto;
    }

    public void setState_permit_valid_upto(String state_permit_valid_upto) {
        this.state_permit_valid_upto = state_permit_valid_upto;
    }

    public String getFittness_valid_upto() {
        return fittness_valid_upto;
    }

    public void setFittness_valid_upto(String fittness_valid_upto) {
        this.fittness_valid_upto = fittness_valid_upto;
    }


    public String getPuc_renewal_date() {
        return puc_renewal_date;
    }

    public void setPuc_renewal_date(String puc_renewal_date) {
        this.puc_renewal_date = puc_renewal_date;
    }


    public String getNational_permit_valid_upto() {
        return national_permit_valid_upto;
    }

    public void setNational_permit_valid_upto(String national_permit_valid_upto) {
        this.national_permit_valid_upto = national_permit_valid_upto;
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

    public String getEngine_no() {
        return engine_no;
    }

    public void setEngine_no(String engine_no) {
        this.engine_no = engine_no;
    }

    public String getChassis_no() {
        return chassis_no;
    }

    public void setChassis_no(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    public String getInsurance_policy_no() {
        return insurance_policy_no;
    }

    public void setInsurance_policy_no(String insurance_policy_no) {
        this.insurance_policy_no = insurance_policy_no;
    }


    public String getInsurance_renewal_date() {
        return insurance_renewal_date;
    }

    public void setInsurance_renewal_date(String insurance_renewal_date) {
        this.insurance_renewal_date = insurance_renewal_date;
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

    public String getVehicle_image() {
        return vehicle_image;
    }

    public void setVehicle_image(String vehicle_image) {
        this.vehicle_image = vehicle_image;
    }

    public String getVehicle_image_url() {
        return vehicle_image_url;
    }

    public void setVehicle_image_url(String vehicle_image_url) {
        this.vehicle_image_url = vehicle_image_url;
    }


    public ArrayList<RTOAgentListPojo.OtherDatesListPojo> getOther_date() {
        return other_date;
    }

    public void setOther_date(ArrayList<RTOAgentListPojo.OtherDatesListPojo> other_date) {
        this.other_date = other_date;
    }

    public ArrayList<RTOAgentListPojo.DocumentListPojo> getDocument() {
        return document;
    }

    public void setDocument(ArrayList<RTOAgentListPojo.DocumentListPojo> document) {
        this.document = document;
    }




    public static class DocumentListPojo implements Serializable {
        private String document;
        private String document_id;
        private String name;
        private String doc_name;
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
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDoc_name() {
            return doc_name;
        }

        public void setDoc_name(String doc_name) {
            this.doc_name = doc_name;
        }


        @Override
        public String toString() {
            return "ClassPojo [document = " + document + "]";
        }
    }

    public static class OtherDatesListPojo implements Serializable {
        private String other_date;
        private String other_date_id;
        private  String text;

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

