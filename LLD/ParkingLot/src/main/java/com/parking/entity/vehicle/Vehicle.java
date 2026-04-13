package com.parking.entity.vehicle;

public abstract class Vehicle {
    private VehicleSize vehicleSize;
    private String LicenseNo;

    public Vehicle(VehicleSize vehicleSize, String licenseNo) {
        this.vehicleSize = vehicleSize;
        LicenseNo = licenseNo;
    }

    public VehicleSize getVehicleSize() {
        return vehicleSize;
    }

    public String getLicenseNo() {
        return LicenseNo;
    }
}
