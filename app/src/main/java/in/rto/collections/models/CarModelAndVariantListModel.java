package in.rto.collections.models;

public class CarModelAndVariantListModel {

    private String carModel;
    private String itsYear;
    private String fuelType;
    private String variant;
    private String id;
    private String frontTyrePressure;
    private String rearTyrePressure;
    private String estimatePrice;
    private String milage;
    private String gearCount;
    private String tankCapacity;
    private String fuelVoltageRange;
    private String vehicleType;

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getItsYear() {
        return itsYear;
    }

    public void setItsYear(String itsYear) {
        this.itsYear = itsYear;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrontTyrePressure() {
        return frontTyrePressure;
    }

    public void setFrontTyrePressure(String frontTyrePressure) {
        this.frontTyrePressure = frontTyrePressure;
    }

    public String getRearTyrePressure() {
        return rearTyrePressure;
    }

    public void setRearTyrePressure(String rearTyrePressure) {
        this.rearTyrePressure = rearTyrePressure;
    }

    public String getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(String estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public String getMilage() {
        return milage;
    }

    public void setMilage(String milage) {
        this.milage = milage;
    }

    public String getGearCount() {
        return gearCount;
    }

    public void setGearCount(String gearCount) {
        this.gearCount = gearCount;
    }

    public String getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(String tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public String getFuelVoltageRange() {
        return fuelVoltageRange;
    }

    public void setFuelVoltageRange(String fuelVoltageRange) {
        this.fuelVoltageRange = fuelVoltageRange;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj instanceof CarModelAndVariantListModel) {
            CarModelAndVariantListModel temp = (CarModelAndVariantListModel) obj;
            if (this.carModel.equals(temp.carModel))
                return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.carModel.hashCode());
    }
}
