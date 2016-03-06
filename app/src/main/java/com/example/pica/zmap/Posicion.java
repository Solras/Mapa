package com.example.pica.zmap;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Posicion {
    private double latitud, longitud;
    private GregorianCalendar fecha;

    public Posicion(double latitud, double longitud, GregorianCalendar fecha) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
    }

    public Posicion() {
        this(0, 0, null);
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Posicion{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                ", hora=" + fecha.get(Calendar.HOUR_OF_DAY) + ":" + fecha.get(Calendar.MINUTE) + ":" + fecha.get(Calendar.SECOND) +
                '}';
    }
}
