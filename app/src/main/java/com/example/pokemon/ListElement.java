package com.example.pokemon;

public class ListElement {
    public String imagen;
    public String nombre;
    public String ataque1;
    public String ataque2;
    public int vida;

    public ListElement(String imagen, String nombre, String ataque1, String ataque2, int vida) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.ataque1 = ataque1;
        this.ataque2 = ataque2;
        this.vida = vida;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAtaque1() {
        return ataque1;
    }

    public void setAtaque1(String ataque1) {
        this.ataque1 = ataque1;
    }

    public String getAtaque2() {
        return ataque2;
    }

    public void setAtaque2(String ataque2) {
        this.ataque2 = ataque2;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }
}
