package com.example.listec.models;

public class Student {

    private String nombre;
    private Group grupo;
    private String stundent_id;
    private int nasistencias;
    private int nfaltas;
    private int promedioasistencias;

    public Student(String nombre, Group grupo, String stundent_id, int nasistencias, int nfaltas, int promedioasistencias) {
        this.nombre = nombre;
        this.grupo = grupo;
        this.stundent_id = stundent_id;
        this.nasistencias = nasistencias;
        this.nfaltas = nfaltas;
        this.promedioasistencias = promedioasistencias;

    }

    public Student() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    public Group getGrupo() {
        return grupo;
    }

    public void setGrupo(Group grupo) {
        this.grupo = grupo;
    }

    public String getStundent_id() {
        return stundent_id;
    }

    public void setStundent_id(String stundent_id) {
        this.stundent_id = stundent_id;
    }

    public int getNasistencias() {
        return nasistencias;
    }

    public void setNasistencias(int nasistencias) {
        this.nasistencias = nasistencias;
    }

    public int getNfaltas() {
        return nfaltas;
    }

    public void setNfaltas(int nfaltas) {
        this.nfaltas = nfaltas;
    }

    public int getPromedioasistencias() {
        return promedioasistencias;
    }

    public void setPromedioasistencias(int promedioasistencias) {
        this.promedioasistencias = promedioasistencias;
    }

    @Override
    public String toString() {
        return "Students{" +
                "nombre='" + nombre + '\'' +
                ", grupo='" + grupo + '\'' +
                ", stundent_id='" + stundent_id + '\'' +
                ", nasistencias='" + nasistencias + '\'' +
                ", nfaltas='" + nfaltas + '\'' +
                ", promedioasistencias='" + promedioasistencias + '\'' +
                '}';
    }

}
