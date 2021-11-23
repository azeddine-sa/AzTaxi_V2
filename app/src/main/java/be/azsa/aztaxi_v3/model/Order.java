package be.azsa.aztaxi_v3.model;

import java.util.Date;

public class Order {
    private long idOrder;
    private String departure, arrival;
    private Date datetime;
    private String infos;
    private User user;

    //Constructeur vide
    public Order(){}

    //Constructeur avec paramètres
    public Order(String departure, String arrival, Date datetime, User user) {
        this.departure = departure;
        this.arrival = arrival;
        this.datetime = datetime;
        this.user = user;
    }
    public Order(String departure, String arrival, Date datetime, String infos, User user) {
        this.departure = departure;
        this.arrival = arrival;
        this.datetime = datetime;
        this.infos = infos;
        this.user = user;
    }

    //Getters&Setters
    public long getIdOrder() {
        return idOrder;
    }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }
    public Date getDatetime() { return datetime; }
    public void setDatetime(Date datetime) { this.datetime = datetime; }
    public String getInfos() { return infos; }
    public void setInfos(String infos) { this.infos = infos; }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    //ToString

    @Override
    public String toString() {
        return "{" +
                "idOrder=" + idOrder +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", datetime=" + datetime +
                ", infos='" + infos + '\'' +
                ", user=" + user +
                '}';
    }
}

