package in.rto.collections.models;

import java.io.Serializable;
import java.util.ArrayList;

public class BankerDetailsPojo implements Serializable {

    private String id;
    private String bank_id;
    private String bank_name;
    private String branch_id;
    private String branch_name;
    private String vehicle_no;
    private String borrower_name;
    private String client_id;
    private String client_name;
    private String vehicle_dealer_id;
    private String vehicle_dealer_name;
    private String loan_amount;
    private String loan_account_number;
    private String 	installment_amount;
    private String date_to_section;
    private String installment_start_date;
    private String 	installment_end_date;
    private String frequency_id;
    private String frequency;
    private String remark;
    private String 	vehicle_number;
    private String date_of_purchase;
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

    private ArrayList<BankerDetailsPojo.OtherDatesListPojo> other_date;
    private ArrayList<BankerDetailsPojo.DocumentListPojo> document;

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


    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }
    public  String getBank_id(){return bank_id;}
    public  String getBank_name(){return bank_name;}
    public void setBank_name(String bank_name){ this.bank_name = bank_name; }


    public String getVehicle_no() {
        return vehicle_no;
    }


    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getBorrower_name() {
        return borrower_name;
    }

    public void setBorrower_name(String borrower_name) {
        this.borrower_name = borrower_name;
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

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
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


    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getDate_of_purchase() {
        return date_of_purchase;
    }

    public void setDate_of_purchase(String date_of_purchase) {
        this.date_of_purchase = date_of_purchase;
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

    public ArrayList<BankerDetailsPojo.OtherDatesListPojo> getOther_date() {
        return other_date;
    }

    public void setOther_date(ArrayList<BankerDetailsPojo.OtherDatesListPojo> other_date) {
        this.other_date = other_date;
    }

    public ArrayList<BankerDetailsPojo.DocumentListPojo> getDocument() {
        return document;
    }

    public void setDocument(ArrayList<BankerDetailsPojo.DocumentListPojo> document) {
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


