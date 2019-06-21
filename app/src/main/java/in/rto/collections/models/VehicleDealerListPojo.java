package in.rto.collections.models;

import java.io.Serializable;
import java.util.ArrayList;

public class VehicleDealerListPojo implements Serializable {

    private String id;

    private String state_name;
    private String state_id;

    private String vehicle_no;

    private String vehicle_owner;

    private String type_id;
    private String type_name;
    private String client_id;
    private String client_name;
    private String rto_agent_id;
    private String rto_agent_name;
    private String engine_no;
    private String chassis_no;
    private String insurance_policy_no;

    private String insurance_renewal_date;

    private String purchase_date;

    private String tem_reg_no;

    private String hypothecated_to;

    private String remark;

    private String description;

    private String importR;
    private String isimport;

    private String updated_by;

    private String created_by;

    private String created_at;

    private String updated_at;
    private String isshowto_rto;
    private String isshowto_customer;
    private String createrName;
    public boolean isChecked;
    private String bank_name;
    private String branch_name;
    private String borrower_name;
    private String loan_amount;
    private String loan_account_number;
    private String installment_amount;
    private String date_to_section;
    private String installment_start_date;
    private String installment_end_date;
    private String frequency_id;
    private String frequency;
    private String vehicle_image;
    private String vehicle_image_url;
    private ArrayList<ServiceDatesListPojo> service_date;
    private ArrayList<OtherDatesListPojo> other_date;

    public ArrayList<WheelDatesListPojo> getWheel_date() {
        return wheel_date;
    }

    public void setWheel_date(ArrayList<WheelDatesListPojo> wheel_date) {
        this.wheel_date = wheel_date;
    }

    private ArrayList<WheelDatesListPojo> wheel_date;

    private ArrayList<DocumentListPojo> document;

    public void setIsshowto_rto(String isshowto_rto) {
        this.isshowto_rto = isshowto_rto;
    }

    public String getIsshowto_rto() {
        return isshowto_rto;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getIsimport() {
        return isimport;
    }

    public void setIsimport(String isimport) {
        this.isimport = isimport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImportR() {
        return importR;
    }

    public void setImportR(String importR) {
        this.importR = importR;
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

    public String getRto_agent_id() {
        return rto_agent_id;
    }

    public void setRto_agent_id(String rto_agent_id) {
        this.rto_agent_id = rto_agent_id;
    }

    public String getRto_agent_name() {
        return rto_agent_name;
    }

    public void setRto_agent_name(String rto_agent_name) {
        this.rto_agent_name = rto_agent_name;
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

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getInsurance_renewal_date() {
        return insurance_renewal_date;
    }

    public void setInsurance_renewal_date(String insurance_renewal_date) {
        this.insurance_renewal_date = insurance_renewal_date;
    }

    public String getTem_reg_no() {
        return tem_reg_no;
    }

    public void setTem_reg_no(String tem_reg_no) {
        this.tem_reg_no = tem_reg_no;
    }

    public String getHypothecated_to() {
        return hypothecated_to;
    }

    public void setHypothecated_to(String hypothecated_to) {
        this.hypothecated_to = hypothecated_to;
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


    public String getInstallment_start_date() {
        return installment_start_date;
    }

    public void setInstallment_start_date(String installment_start_date) {
        this.installment_start_date = installment_start_date;
    }

    public String getInstallment_end_date() {
        return installment_end_date;
    }

    public void setInstallment_end_date(String installment_end_date) {
        this.installment_end_date = installment_end_date;
    }

    public String getFrequency_id() {
        return frequency_id;
    }

    public void setFrequency_id(String frequency_id) {
        this.frequency_id = frequency_id;
    }


    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBorrower_name() {
        return borrower_name;
    }

    public void setBorrower_name(String borrower_name) {
        this.borrower_name = borrower_name;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(String loan_amount) {
        this.loan_amount = loan_amount;
    }

    public String getLoan_account_number() {
        return loan_account_number;
    }

    public void setLoan_account_number(String loan_account_number) {
        this.loan_account_number = loan_account_number;
    }


    public String getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(String installment_amount) {
        this.installment_amount = installment_amount;
    }


    public String getDate_to_section() {
        return date_to_section;
    }

    public void setDate_to_section(String date_to_section) {
        this.date_to_section = date_to_section;
    }


    public ArrayList<ServiceDatesListPojo> getService_date() {
        return service_date;
    }

    public void setService_date(ArrayList<ServiceDatesListPojo> service_date) {
        this.service_date = service_date;
    }

    public ArrayList<OtherDatesListPojo> getOther_date() {
        return other_date;
    }

    public void setOther_date(ArrayList<OtherDatesListPojo> other_date) {
        this.other_date = other_date;
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

    public ArrayList<DocumentListPojo> getDocument() {
        return document;
    }

    public void setDocument(ArrayList<DocumentListPojo> document) {
        this.document = document;
    }

    public static class ServiceDatesListPojo implements Serializable {
        private String service_date;
        private String text;
        private String service_date_id;

        public String getService_date() {
            return service_date;
        }

        public void setService_date_id(String service_date_id) {
            this.service_date_id = service_date_id;
        }

        public String getService_date_id() {
            return service_date_id;
        }

        public void setService_date(String service_date) {
            this.service_date = service_date;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class WheelDatesListPojo implements Serializable {
        private String wheel_alignment_date;
        private String text;
        private String wheel_date_id;

        public String getWheel_alignment_date() {
            return wheel_alignment_date;
        }

        public void setWheel_alignment_date(String wheel_alignment_date) {
            this.wheel_alignment_date = wheel_alignment_date;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getWheel_date_id() {
            return wheel_date_id;
        }

        public void setWheel_date_id(String wheel_date_id) {
            this.wheel_date_id = wheel_date_id;
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


    public static class DocumentListPojo implements Serializable {
        private String document;
        private String document_name;
        private String document_id;
        private String original_name;

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public String getDocument_name() {
            return document_name;
        }

        public void setDocument_name(String document_name) {
            this.document_name = document_name;
        }

        public String getOriginal_name() {
            return original_name;
        }

        public void setOriginal_name(String original_name) {
            this.original_name = original_name;
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


}
