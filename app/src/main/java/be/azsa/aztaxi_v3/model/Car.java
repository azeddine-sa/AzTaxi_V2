package be.azsa.aztaxi_v3.model;

public class Car {

    private long idCar;
    private String brand, model, licensePlate;

    //Consctucteur Vide
    public Car(){ }

    //Constructeur avec paramètres
    public Car(String brand, String model, String licensePlate) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    public long getIdCar() { return idCar; }
    public void setIdCar(int idCar) { this.idCar = idCar; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }


    @Override
    public String toString() {
        return "Car{" +
                "idCar=" + idCar +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }
}
