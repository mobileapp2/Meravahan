package in.rto.collections.models;

import java.io.Serializable;

public class ClientMainListPojo implements Serializable{

    private String created_by;

    private String whats_app_no;

    private String alias;

    //private String middle_name;

    private String sync_status;

    private String client_code;

    private String id;

    private String name;

    private String updated_at;

    private String email;

    private String dob;

   private String remark;

    private String created_at;

    private String updated_by;

    private String anniversary_date;

    private String mobile;

    private String client_code_id;

    private String language_id;

    private String language;

    public boolean isChecked;


    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getWhats_app_no() {
        return whats_app_no;
    }

    public void setWhats_app_no(String whats_app_no) {
        this.whats_app_no = whats_app_no;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getClient_code() {
        return client_code;
    }

    public void setClient_code(String client_code) {
        this.client_code = client_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String first_name) {
        this.name = first_name;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getAnniversary_date() {
        return anniversary_date;
    }

    public void setAnniversary_date(String anniversary_date) {
        this.anniversary_date = anniversary_date;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getClient_code_id() {
        return client_code_id;
    }

    public void setClient_code_id(String client_code_id) {
        this.client_code_id = client_code_id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
