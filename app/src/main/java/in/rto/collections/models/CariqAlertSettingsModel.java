package in.rto.collections.models;

import java.util.ArrayList;

public class CariqAlertSettingsModel {

    private ArrayList<TypesBean> types;

    public ArrayList<TypesBean> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<TypesBean> types) {
        this.types = types;
    }

    public static class TypesBean {
        /**
         * type : Speed
         * isOn : true
         * isConfigurable : true
         */

        private String type;
        private boolean isOn;
        private boolean isConfigurable;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isIsOn() {
            return isOn;
        }

        public void setIsOn(boolean isOn) {
            this.isOn = isOn;
        }

        public boolean isIsConfigurable() {
            return isConfigurable;
        }

        public void setIsConfigurable(boolean isConfigurable) {
            this.isConfigurable = isConfigurable;
        }
    }
}
