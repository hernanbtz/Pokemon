package com.example.pokemon;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class CentroPokemon extends AppCompatActivity {
    Intent abrirMochila;
    Intent ciudad;
    Display display;
    int anchoTotal;
    int altoTotal;
    private int salidaIniX;
    private int salidaIniY;
    Animation fade_in;
    Animation fade_out;

    boolean hasCurado;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MediaPlayer audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        anchoTotal = display.getWidth();
        altoTotal = display.getHeight();
        salidaIniX = (int) (0.35 * anchoTotal);
        salidaIniY = (int) (0.6 * altoTotal);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_in.reset();
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fade_out.reset();
        hasCurado = false;
        abrirMochila = new Intent(this, Inventario.class);
        ciudad = new Intent(this, Ciudad.class);
        setContentView(new VistaRuta(this));
        play();
    }

    private void play() {
        if (audio == null) {
            audio = MediaPlayer.create(this, R.raw.centropoke);
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

    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
    protected void onResume() {
        super.onResume();
        play();
    }

    public class VistaRuta extends View {
        private Drawable entrenador;
        private Drawable flechaizq;
        private Drawable flechaarr;
        private Drawable flechader;
        private Drawable flechaaba;
        private Drawable centro;
        private Drawable inventario;
        ThreadJuego threadJuego;
        private boolean aPulsadoUp = false;
        private boolean aPulsadoDo = false;
        private boolean aPulsadoLe = false;
        private boolean aPulsadoRi = false;


        private int VelocidadEntrenador = 2;

        private int xPosicionMochila = (int) (0.85 * anchoTotal);
        private int yPosicionMochila = (int) (0.035 * altoTotal);
        private int xPosicionPersonaje = (int) (0.425 * anchoTotal);
        private int yPosicionPersonaje = (int) (0.69 * altoTotal);

        private int xPosicionflechaizq = (int) (0.81 * anchoTotal);
        private int yPosicionflechaizq = (int) (0.78 * altoTotal);

        private int xPosicionflechaarr = (int) (0.86 * anchoTotal);
        private int yPosicionflechaarr = (int) (0.67 * altoTotal);

        private int xPosicionflechader = (int) (0.91 * anchoTotal);
        private int yPosicionflechader = (int) (0.78 * altoTotal);

        private int xPosicionflechaaba = (int) (0.86 * anchoTotal);
        private int yPosicionflechaaba = (int) (0.8 * altoTotal);

        String idPoke;


        public VistaRuta(Context context) {
            super(context);
            init(null, 0);
        }

        public VistaRuta(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(attrs, 0);
        }

        public VistaRuta(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(attrs, defStyle);
        }

        private void init(AttributeSet attrs, int defStyle) {
            //Creamos los objetos del juego
            entrenador = getResources().getDrawable(R.drawable.entrenador_grande, getContext().getTheme());
            flechaizq = getResources().getDrawable(R.drawable.izq, getContext().getTheme());
            flechader = getResources().getDrawable(R.drawable.der, getContext().getTheme());
            flechaarr = getResources().getDrawable(R.drawable.arriba, getContext().getTheme());
            flechaaba = getResources().getDrawable(R.drawable.abajo, getContext().getTheme());
            inventario = getResources().getDrawable(R.drawable.mochila, getContext().getTheme());
            centro = getResources().getDrawable(R.drawable.fondo2, getContext().getTheme());
            threadJuego = new ThreadJuego();
            threadJuego.start();

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

        private void curar() {
            if (xPosicionPersonaje <= 0.445 * anchoTotal && xPosicionPersonaje >= 0.405 * anchoTotal && yPosicionPersonaje <= 0.3 * altoTotal && yPosicionPersonaje >= 0.2 * altoTotal) {
                aPulsadoUp = false;
                if (!hasCurado) {
                    startAnimation(fade_in);
                    startAnimation(fade_out);
                    hasCurado = true;
                    sacarId();
                }
            }
        }

        private void sacarId() {
            db.collection("inventario").whereEqualTo("tipo", "Pokemon")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idPoke = document.getId();
                                    actualizarVida();
                                }
                            } else {
                                System.out.println("EN EL ELSE ERROR DE BBDD");
                            }
                        }
                    });

        }

        private void actualizarVida() {
            Map<String, Object> updateV = new HashMap<>();
            updateV.put("ps_poke", 100);
            db.collection("inventario").document(idPoke).update(updateV);
        }

        private void actualizarUp() {
            if (!aPulsadoDo && !aPulsadoRi && !aPulsadoLe && aPulsadoUp) {
                if (yPosicionPersonaje <= (int) (0.3 * altoTotal) && xPosicionPersonaje >= (int) (0.1 * anchoTotal) && xPosicionPersonaje <= (int) (0.62 * anchoTotal)) {
                    aPulsadoUp = false;
                } else {
                    yPosicionPersonaje = yPosicionPersonaje - VelocidadEntrenador;
                }
            }
        }

        private void actualizarDown() {
            if (aPulsadoDo && !aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                yPosicionPersonaje = yPosicionPersonaje + VelocidadEntrenador;
            }

        }

        private void actualizarLeft() {
            if (!aPulsadoDo && !aPulsadoRi && aPulsadoLe && !aPulsadoUp) {
                if (yPosicionPersonaje >= (int) (0.3 * altoTotal) && xPosicionPersonaje <= (int) (0.1 * anchoTotal)) {
                    aPulsadoLe = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje - VelocidadEntrenador;

                }
            }
        }

        private void actualizarRight() {
            if (!aPulsadoDo && aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                if (yPosicionPersonaje >= (int) (0.3 * altoTotal) && xPosicionPersonaje >= (int) (0.62 * anchoTotal)) {
                    aPulsadoRi = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje + VelocidadEntrenador;
                }
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
            setBackground(centro);
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

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            pintarTodo(canvas);
        }

        private void actualizar() {
            actualizarUp();
            actualizarDown();
            actualizarLeft();
            actualizarRight();
            salidaCentroPokemon();
            curar();
            //System.out.println("x: " + String.valueOf(xPosicionPersonaje) + " y: " + String.valueOf(yPosicionPersonaje));
        }

        private void salidaCentroPokemon() {
            if (yPosicionPersonaje >= (int) (0.75 * altoTotal) && xPosicionPersonaje >= (int) (0.35 * anchoTotal) && xPosicionPersonaje <= (int) (0.5 * anchoTotal) && yPosicionPersonaje <= (int) (0.8 * altoTotal)) {
                audio.pause();
                ciudad.putExtra("coorX", salidaIniX);
                ciudad.putExtra("coorY", salidaIniY);
                startActivity(ciudad);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
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