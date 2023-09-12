package com.example.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.IOException;

public class Ciudad extends AppCompatActivity {
    Intent iniciales;
    Intent abrirMochila;
    Intent centroPokemon;
    Intent ruta;
    Bundle bundle;
    private int salidaIniX;
    private int salidaIniY;
    private boolean tienesUnInicial;
    Display display;
    int anchoTotal;
    int altoTotal;
    MediaPlayer audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        anchoTotal = display.getWidth();
        altoTotal = display.getHeight();
        try {
            bundle = getIntent().getExtras();
            salidaIniX = bundle.getInt("coorX");
            salidaIniY = bundle.getInt("coorY");
            tienesUnInicial = bundle.getBoolean("inicial");
        } catch (Exception e) {
            salidaIniX = (int) (0.235 * anchoTotal);
            salidaIniY = (int) (0.8 * altoTotal);
            tienesUnInicial = false;
        }
        setContentView(new VistaCiudad(this));
        iniciales = new Intent(this, CasaIniciales.class);
        centroPokemon = new Intent(this, CentroPokemon.class);
        abrirMochila = new Intent(this, Inventario.class);
        ruta = new Intent(this, Ruta.class);
        play();
    }

    private void play() {
        if (audio == null) {
            audio = MediaPlayer.create(this, R.raw.ciudad);
            audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }

        audio.start();
        audio.setVolume(0.01f, 0.01f);
    }

    private void stopPlayer() {
        if (audio != null) {
            audio.release();
            audio = null;
        }
    }

    protected void onResume() {
        super.onResume();
        play();
    }

    protected void onStop() {
        super.onStop();
        stopPlayer();
    }


    public class VistaCiudad extends View {
        //imagenes
        private Drawable entrenador;
        private Drawable flechaizq;
        private Drawable flechaarr;
        private Drawable flechader;
        private Drawable flechaaba;
        private Drawable fondo;
        private Drawable inventario;


        //Datos de posicionamiento de las imagenes
        private int xPosicionMochila = (int) (0.85 * anchoTotal);
        private int yPosicionMochila = (int) (0.035 * altoTotal);


        private int xPosicionPersonaje = (int) salidaIniX;
        private int yPosicionPersonaje = (int) salidaIniY;


        private int xPosicionflechaizq = (int) (0.81 * anchoTotal);
        private int yPosicionflechaizq = (int) (0.78 * altoTotal);

        private int xPosicionflechaarr = (int) (0.86 * anchoTotal);
        private int yPosicionflechaarr = (int) (0.67 * altoTotal);

        private int xPosicionflechader = (int) (0.91 * anchoTotal);
        private int yPosicionflechader = (int) (0.78 * altoTotal);

        private int xPosicionflechaaba = (int) (0.86 * anchoTotal);
        private int yPosicionflechaaba = (int) (0.8 * altoTotal);


        private ThreadJuego threadJuego;
        private boolean aPulsadoUp = false;
        private boolean aPulsadoDo = false;
        private boolean aPulsadoLe = false;
        private boolean aPulsadoRi = false;
        private int VelocidadEntrenador = 1;

        public VistaCiudad(Context context) {
            super(context);
            init(null, 0);
        }

        public VistaCiudad(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(attrs, 0);
        }

        public VistaCiudad(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(attrs, defStyle);
        }

        private void init(AttributeSet attrs, int defStyle) {
            //Creamos los objetos del juego
            entrenador = getResources().getDrawable(R.drawable.entrenador, getContext().getTheme());
            flechaizq = getResources().getDrawable(R.drawable.izq, getContext().getTheme());
            flechader = getResources().getDrawable(R.drawable.der, getContext().getTheme());
            flechaarr = getResources().getDrawable(R.drawable.arriba, getContext().getTheme());
            flechaaba = getResources().getDrawable(R.drawable.abajo, getContext().getTheme());
            fondo = getResources().getDrawable(R.drawable.fondo, getContext().getTheme());
            inventario = getResources().getDrawable(R.drawable.mochila, getContext().getTheme());
            threadJuego = new ThreadJuego();
            threadJuego.start();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            pintarTodo(canvas);
        }


        private void actualizarUp() {
            if (!aPulsadoDo && !aPulsadoRi && !aPulsadoLe && aPulsadoUp) {
                if (xPosicionPersonaje >= (int) (0.13 * anchoTotal) && xPosicionPersonaje <= (int) (0.145 * anchoTotal) && yPosicionPersonaje <= (int) (0.64 * altoTotal) && yPosicionPersonaje >= (int) (0.63 * altoTotal)) {
                    aPulsadoUp = false;
                } else if (xPosicionPersonaje > (int) (0.18 * anchoTotal) && xPosicionPersonaje < (int) (0.29 * anchoTotal) && yPosicionPersonaje <= (0.64 * altoTotal) && yPosicionPersonaje >= (int) (0.63 * altoTotal)) {
                    aPulsadoUp = false;
                } else if (xPosicionPersonaje >= (int) (0.34 * anchoTotal) && xPosicionPersonaje <= (int) (0.36 * anchoTotal) && yPosicionPersonaje == (int) (0.53 * altoTotal)) {
                    aPulsadoUp = false;
                } else if (xPosicionPersonaje > (int) (0.145 * anchoTotal) && xPosicionPersonaje <= (int) (0.18 * anchoTotal) && yPosicionPersonaje == (int) (0.63 * altoTotal)) {
                    aPulsadoUp = false;
                } else {
                    yPosicionPersonaje = yPosicionPersonaje - VelocidadEntrenador;
                }
            }
        }

        private void actualizarDown() {
            if (aPulsadoDo && !aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                if (xPosicionPersonaje > (int) (0.27 * anchoTotal) && xPosicionPersonaje < (0.8 * anchoTotal) && yPosicionPersonaje >= (0.67 * altoTotal)) {
                    aPulsadoDo = false;
                } else if (xPosicionPersonaje >= (int) (0.13 * anchoTotal) && xPosicionPersonaje < (0.20 * anchoTotal) && yPosicionPersonaje >= (0.67 * altoTotal)) {
                    aPulsadoDo = false;
                } else {
                    yPosicionPersonaje = yPosicionPersonaje + VelocidadEntrenador;
                }
            }

        }

        private void actualizarLeft() {
            if (!aPulsadoDo && !aPulsadoRi && aPulsadoLe && !aPulsadoUp) {
                if (xPosicionPersonaje <= (int) (0.21 * anchoTotal) && yPosicionPersonaje > (int) (0.69 * altoTotal) && yPosicionPersonaje < (int) (altoTotal)) {
                    aPulsadoLe = false;
                } else if (yPosicionPersonaje < (int) (0.68 * altoTotal) && yPosicionPersonaje >= (int) (0.64 * altoTotal) && xPosicionPersonaje <= (int) (0.13 * anchoTotal)) {
                    aPulsadoLe = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje - VelocidadEntrenador;
                }
            }
        }

        private void actualizarRight() {
            if (!aPulsadoDo && aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                if (xPosicionPersonaje >= (int) (0.27 * anchoTotal) && yPosicionPersonaje > (int) (0.69 * altoTotal) && yPosicionPersonaje < (int) (altoTotal)) {
                    aPulsadoRi = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje + VelocidadEntrenador;
                }
            }
        }


        private void centroPokemon() {
            if (xPosicionPersonaje >= (int) (0.34 * anchoTotal) && xPosicionPersonaje <= (int) (0.36 * anchoTotal) && yPosicionPersonaje < (int) (0.56 * altoTotal) && yPosicionPersonaje > (int) (0.53 * altoTotal)) {
                audio.pause();
                startActivity(centroPokemon);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }


        private void casaIniciales() {
            if (xPosicionPersonaje > (int) (0.145 * anchoTotal) && xPosicionPersonaje <= (int) (0.18 * anchoTotal) && yPosicionPersonaje < (int) (0.64 * altoTotal) && yPosicionPersonaje > (int) (0.63 * altoTotal)) {
                audio.pause();
                iniciales.putExtra("inicial", tienesUnInicial);
                startActivity(iniciales);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

        private void rutaPokemon() {
            if (xPosicionPersonaje <= 540 && xPosicionPersonaje >= 462 && yPosicionPersonaje >= 960 && yPosicionPersonaje < 970) {
                audio.pause();
                startActivity(ruta);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }


        private void actualizar() {
            actualizarUp();
            actualizarDown();
            actualizarLeft();
            actualizarRight();
            centroPokemon();
            casaIniciales();
            rutaPokemon();
            //System.out.println("x: " + String.valueOf(xPosicionPersonaje) + " y: " + String.valueOf(yPosicionPersonaje));
        }


        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                movimiento(event);
                abrirInventario(event);
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                aPulsadoUp = false;
                aPulsadoLe = false;
                aPulsadoRi = false;
                aPulsadoDo = false;
            }
            return true;
        }

        private void abrirInventario(MotionEvent event) {
            float xRaton = event.getX();
            float yRaton = event.getY();
            if (xRaton > xPosicionMochila && xRaton < xPosicionMochila + inventario.getIntrinsicWidth() && yRaton > yPosicionMochila && yRaton < yPosicionMochila + inventario.getIntrinsicHeight()) {
                startActivity(abrirMochila);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

        private void movimiento(MotionEvent event) {
            float xRaton = event.getX();
            float yRaton = event.getY();
            if (xRaton > xPosicionflechaaba && xRaton < xPosicionflechaaba + flechaaba.getIntrinsicWidth() && yRaton > yPosicionflechaaba && yRaton < yPosicionflechaaba + flechaaba.getIntrinsicHeight()) {
                aPulsadoDo = true;

            }
            if (xRaton > xPosicionflechaarr && xRaton < xPosicionflechaarr + flechaarr.getIntrinsicWidth() && yRaton > yPosicionflechaarr && yRaton < yPosicionflechaarr + flechaarr.getIntrinsicHeight()) {
                aPulsadoUp = true;

            }
            if (xRaton > xPosicionflechader && xRaton < xPosicionflechader + flechader.getIntrinsicWidth() && yRaton > yPosicionflechader && yRaton < yPosicionflechader + flechader.getIntrinsicHeight()) {
                aPulsadoRi = true;

            }
            if (xRaton > xPosicionflechaizq && xRaton < xPosicionflechaizq + flechaizq.getIntrinsicWidth() && yRaton > yPosicionflechaizq && yRaton < yPosicionflechaizq + flechaizq.getIntrinsicHeight()) {
                aPulsadoLe = true;


            }

        }

        private void pintarTodo(Canvas canvas) {
            setBackground(fondo);
            entrenador.setBounds(xPosicionPersonaje, yPosicionPersonaje,
                    xPosicionPersonaje + entrenador.getIntrinsicWidth(),
                    yPosicionPersonaje + entrenador.getIntrinsicHeight());
            entrenador.draw(canvas);
            flechaaba.setBounds(xPosicionflechaaba, yPosicionflechaaba,
                    xPosicionflechaaba + flechaaba.getIntrinsicWidth(),
                    yPosicionflechaaba + flechaaba.getIntrinsicHeight());
            flechaaba.draw(canvas);
            flechaarr.setBounds(xPosicionflechaarr, yPosicionflechaarr,
                    xPosicionflechaarr + flechaarr.getIntrinsicWidth(),
                    yPosicionflechaarr + flechaarr.getIntrinsicHeight());
            flechaarr.draw(canvas);
            flechaizq.setBounds(xPosicionflechaizq, yPosicionflechaizq,
                    xPosicionflechaizq + flechaizq.getIntrinsicWidth(),
                    yPosicionflechaizq + flechaizq.getIntrinsicHeight());
            flechaizq.draw(canvas);
            flechader.setBounds(xPosicionflechader, yPosicionflechader,
                    xPosicionflechader + flechader.getIntrinsicWidth(),
                    yPosicionflechader + flechader.getIntrinsicHeight());
            flechader.draw(canvas);
            inventario.setBounds(xPosicionMochila, yPosicionMochila,
                    xPosicionMochila + inventario.getIntrinsicWidth(),
                    yPosicionMochila + inventario.getIntrinsicHeight());
            inventario.draw(canvas);
        }

        class ThreadJuego extends Thread {
            final int tiempo = 9;
            private boolean exit = true;
            private int numVUeltas = 0;

            public void setExit(boolean exit) {
                this.exit = exit;
            }

            public void run() {
                while (exit) {
                    numVUeltas++;
                    //Actualizar datos
                    actualizar();
                    //dibujarlos
                    postInvalidate();//llama al draw
                    try {
                        sleep(tiempo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}