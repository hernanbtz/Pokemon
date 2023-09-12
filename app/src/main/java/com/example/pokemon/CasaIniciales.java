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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CasaIniciales extends AppCompatActivity {
    Bundle bundle;
    Intent ciudad;
    Intent abrirMochila;
    boolean tienesUnInicial;
    boolean puedesCogerInicial;
    private int salidaIniX;
    private int salidaIniY;
    Display display;
    int anchoTotal;
    int altoTotal;
    MediaPlayer audio;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        anchoTotal = display.getWidth();
        altoTotal = display.getHeight();
        salidaIniX = (int) (0.16 * anchoTotal);
        salidaIniY = (int) (0.66 * altoTotal);
        ciudad = new Intent(this, Ciudad.class);
        abrirMochila = new Intent(this, Inventario.class);
        try {
            bundle = getIntent().getExtras();
            tienesUnInicial = bundle.getBoolean("inicial");
        } catch (Exception e) {
            tienesUnInicial = false;
        }
        setContentView(new VistaCasaIniciales(this));
        puedesCogerInicial = false;
        play();
    }

    private void play() {
        if (audio == null) {
            audio = MediaPlayer.create(this, R.raw.casa);
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


    public class VistaCasaIniciales extends View {
        private Drawable entrenador;
        private Drawable flechaizq;
        private Drawable flechaarr;
        private Drawable flechader;
        private Drawable flechaaba;
        private Drawable casaIni;
        private Drawable inventario;
        private Drawable pokeballini1;
        private Drawable pokeballini2;
        private Drawable pokeballini3;
        ThreadJuego threadJuego;
        private boolean aPulsadoUp = false;
        private boolean aPulsadoDo = false;
        private boolean aPulsadoLe = false;
        private boolean aPulsadoRi = false;
        private int VelocidadEntrenador = 2;

        private int xPosicionMochila = (int) (0.85 * anchoTotal);
        private int yPosicionMochila = (int) (0.035 * altoTotal);

        String pokemonini1;
        String pokemonini2;
        String pokemonini3;


        private int xPosicionPersonaje = (int) (0.565 * anchoTotal);
        private int yPosicionPersonaje = (int) (0.7 * altoTotal);


        private int xPosicionflechaizq = (int) (0.81 * anchoTotal);
        private int yPosicionflechaizq = (int) (0.78 * altoTotal);

        private int xPosicionflechaarr = (int) (0.86 * anchoTotal);
        private int yPosicionflechaarr = (int) (0.67 * altoTotal);

        private int xPosicionflechader = (int) (0.91 * anchoTotal);
        private int yPosicionflechader = (int) (0.78 * altoTotal);

        private int xPosicionflechaaba = (int) (0.86 * anchoTotal);
        private int yPosicionflechaaba = (int) (0.8 * altoTotal);

        private int xPosicionPokeballini1 = (int) (0.045 * anchoTotal);
        private int yPosicionPokeballini1 = (int) (0.24 * altoTotal);

        private int xPosicionPokeballini2 = (int) (0.6 * anchoTotal);

        private int yPosicionPokeballini2 = (int) (0.24 * altoTotal);

        private int xPosicionPokeballini3 = (int) (0.3 * anchoTotal);

        private int yPosicionPokeballini3 = (int) (0.7 * altoTotal);


        public VistaCasaIniciales(Context context) {
            super(context);
            init(null, 0);
        }

        public VistaCasaIniciales(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(attrs, 0);
        }

        public VistaCasaIniciales(Context context, AttributeSet attrs, int defStyle) {
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
            casaIni = getResources().getDrawable(R.drawable.casa_iniciales, getContext().getTheme());
            pokeballini1 = getResources().getDrawable(R.drawable.pokebola, getContext().getTheme());
            pokeballini2 = getResources().getDrawable(R.drawable.pokebola, getContext().getTheme());
            pokeballini3 = getResources().getDrawable(R.drawable.pokebola, getContext().getTheme());
            almacenarPokemon();
            threadJuego = new ThreadJuego();
            threadJuego.start();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                movimiento(event);
                recogerPokeBolas(event);
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

        private void actualizarUp() {
            if (!aPulsadoDo && !aPulsadoRi && !aPulsadoLe && aPulsadoUp) {
                if (xPosicionPersonaje >= (int) (0.44 * anchoTotal) && xPosicionPersonaje <= (int) (0.83 * anchoTotal) && yPosicionPersonaje <= (int) (0.04 * altoTotal)) {
                    aPulsadoUp = false;
                } else if (xPosicionPersonaje >= (int) (-0.026 * anchoTotal) && xPosicionPersonaje <= (int) (0.3 * anchoTotal) && yPosicionPersonaje <= (int) (0.03 * altoTotal)) {
                    aPulsadoUp = false;
                } else if (xPosicionPersonaje > (int) (0.29 * anchoTotal) && xPosicionPersonaje < (int) (0.51 * anchoTotal) && yPosicionPersonaje <= (int) (0.37 * altoTotal) && yPosicionPersonaje >= (int) (0.36 * altoTotal)) {
                    aPulsadoUp = false;
                } else {
                    yPosicionPersonaje = yPosicionPersonaje - VelocidadEntrenador;
                }
            }
        }

        private void actualizarDown() {
            if (aPulsadoDo && !aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                if (xPosicionPersonaje >= (int) (0.63 * anchoTotal) && xPosicionPersonaje <= (0.7 * anchoTotal) && yPosicionPersonaje >= (0.71 * altoTotal)) {
                    aPulsadoDo = false;
                } else if (xPosicionPersonaje >= (int) (0.775 * anchoTotal) && xPosicionPersonaje <= (int) (0.84 * anchoTotal) && yPosicionPersonaje >= (int) (0.14 * altoTotal)) {
                    aPulsadoDo = false;
                } else if (xPosicionPersonaje >= (int) (0.44 * anchoTotal) && xPosicionPersonaje <= (int) (0.50 * anchoTotal) && yPosicionPersonaje >= (int) (0.15 * altoTotal) && yPosicionPersonaje <= (int) (0.16 * altoTotal)) {
                    aPulsadoDo = false;
                } else if (xPosicionPersonaje >= (int) (-0.1 * anchoTotal) && xPosicionPersonaje <= (int) (0.50 * anchoTotal) && yPosicionPersonaje >= (int) (0.7 * altoTotal)) {
                    aPulsadoDo = false;
                } else if (xPosicionPersonaje >= (int) (0.3 * anchoTotal) && xPosicionPersonaje <= (int) (0.45 * anchoTotal) && yPosicionPersonaje >= (int) (0.56 * altoTotal)) {
                    aPulsadoDo = false;
                } else {
                    yPosicionPersonaje = yPosicionPersonaje + VelocidadEntrenador;
                }
            }

        }

        private void actualizarLeft() {
            if (!aPulsadoDo && !aPulsadoRi && aPulsadoLe && !aPulsadoUp) {
                if (yPosicionPersonaje >= (int) (0.02 * altoTotal) && yPosicionPersonaje <= (0.15 * altoTotal) && xPosicionPersonaje <= (0.45 * anchoTotal) && xPosicionPersonaje >= (0.43 * anchoTotal)) {
                    aPulsadoLe = false;
                } else if (xPosicionPersonaje <= (int) (-0.025 * anchoTotal) && yPosicionPersonaje >= (int) (0.02 * altoTotal) && yPosicionPersonaje <= (int) (0.8 * altoTotal)) {
                    aPulsadoLe = false;
                } else if (xPosicionPersonaje <= (int) (0.46 * anchoTotal) && xPosicionPersonaje >= (int) (0.45 * anchoTotal) && yPosicionPersonaje >= (int) (0.57 * altoTotal) && yPosicionPersonaje <= (int) (0.8 * altoTotal)) {
                    aPulsadoLe = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje - VelocidadEntrenador;
                }
            }
        }

        private void actualizarRight() {
            if (!aPulsadoDo && aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                if (xPosicionPersonaje >= (int) (0.67 * anchoTotal) && yPosicionPersonaje > (int) (0.14 * altoTotal) && yPosicionPersonaje <= (int) (altoTotal)) {
                    aPulsadoRi = false;
                } else if (yPosicionPersonaje >= (int) (0.02 * altoTotal) && yPosicionPersonaje <= (int) (0.14 * altoTotal) && xPosicionPersonaje >= (int) (0.83 * anchoTotal)) {
                    aPulsadoRi = false;
                } else if (yPosicionPersonaje >= (int) (0.02 * altoTotal) && yPosicionPersonaje <= (int) (0.36 * altoTotal) && xPosicionPersonaje >= (int) (0.29 * anchoTotal) && xPosicionPersonaje <= (int) (0.3 * anchoTotal)) {
                    aPulsadoRi = false;
                } else if (yPosicionPersonaje >= (int) (0.57 * altoTotal) && yPosicionPersonaje <= (int) (0.7 * altoTotal) && xPosicionPersonaje >= (int) (0.29 * anchoTotal) && xPosicionPersonaje <= (int) (0.3 * anchoTotal)) {
                    aPulsadoRi = false;
                } else {
                    xPosicionPersonaje = xPosicionPersonaje + VelocidadEntrenador;
                }
            }
        }

        ArrayList<String> iniciales;

        private void almacenarPokemon() {
            if (!tienesUnInicial) {
                iniciales = new ArrayList<>();
                db.collection("pokemon").whereEqualTo("nombre_poke", "Charmander")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        pokemonini1 = String.valueOf(document.getData().get("nombre_poke"));
                                        iniciales.add(pokemonini1);
                                    }
                                } else {
                                    String error = ("Error getting documents.");
                                    System.out.println(error);
                                }
                            }
                        });
                db.collection("pokemon").whereEqualTo("nombre_poke", "Bulbasaur")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        pokemonini2 = String.valueOf(document.getData().get("nombre_poke"));
                                        iniciales.add(pokemonini2);
                                    }
                                } else {
                                    String error = ("Error getting documents.");
                                    System.out.println(error);
                                }
                            }
                        });
                db.collection("pokemon").whereEqualTo("nombre_poke", "Squirtle")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        pokemonini3 = String.valueOf(document.getData().get("nombre_poke"));
                                        iniciales.add(pokemonini3);
                                    }
                                } else {
                                    String error = ("Error getting documents.");
                                    System.out.println(error);
                                }
                            }
                        });
                Collections.shuffle(iniciales);
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


        Map<String, Object> addPokeIni;

        private void recogerPokeBolas(MotionEvent event) {
            if (!tienesUnInicial && puedesCogerInicial) {
                float xRaton = event.getX();
                float yRaton = event.getY();
                if (xRaton > xPosicionPokeballini1 && xRaton < xPosicionPokeballini1 + pokeballini1.getIntrinsicWidth() && yRaton > yPosicionPokeballini1 && yRaton < yPosicionPokeballini1 + pokeballini1.getIntrinsicHeight()) {
                    tienesUnInicial = true;
                }
                if (xRaton > xPosicionPokeballini2 && xRaton < xPosicionPokeballini2 + pokeballini2.getIntrinsicWidth() && yRaton > yPosicionPokeballini2 && yRaton < yPosicionPokeballini2 + pokeballini2.getIntrinsicHeight()) {

                    tienesUnInicial = true;
                }
                if (xRaton > xPosicionPokeballini3 && xRaton < xPosicionPokeballini3 + pokeballini3.getIntrinsicWidth() && yRaton > yPosicionPokeballini3 && yRaton < yPosicionPokeballini3 + pokeballini3.getIntrinsicHeight()) {
                    tienesUnInicial = true;
                }
                if (tienesUnInicial) {
                    System.out.println("Tu pokemon es " + iniciales.get(0));
                    String nombrePokeIni = iniciales.get(0);
                    addPokeIni = new HashMap<>();
                    addPokeIni.put("nombre", nombrePokeIni);
                    addPokeIni.put("tipo", "Pokemon");
                    addPokeIni.put("ps_poke", "100");
                    db.collection("inventario")
                            .add(addPokeIni)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Error");
                                }
                            });
                }
            }
        }

        private void pintarTodo(Canvas canvas) {
            setBackground(casaIni);
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
            pokeBolasIni(canvas);
            pintarTodo(canvas);
        }

        private void actualizar() {
            actualizarUp();
            actualizarDown();
            actualizarLeft();
            actualizarRight();
            cogerIniciales();
            salidacasaIniciales();
        }


        private void cogerIniciales() {
            if (!tienesUnInicial) {
                if (xPosicionPokeballini1 - (int) (0.1 * anchoTotal) < xPosicionPersonaje && xPosicionPersonaje < xPosicionPokeballini1 + (int) (0.1 * anchoTotal)) {
                    if (yPosicionPokeballini1 - (int) (0.1 * altoTotal) < yPosicionPersonaje && yPosicionPersonaje < yPosicionPokeballini1 + (int) (0.1 * altoTotal)) {

                        puedesCogerInicial = true;
                    }

                } else if (xPosicionPokeballini2 - (int) (0.1 * anchoTotal) < xPosicionPersonaje && xPosicionPersonaje < xPosicionPokeballini2 + (int) (0.1 * anchoTotal)) {
                    if (yPosicionPokeballini2 - (int) (0.1 * altoTotal) < yPosicionPersonaje && yPosicionPersonaje < yPosicionPokeballini2 + (int) (0.1 * altoTotal)) {

                        puedesCogerInicial = true;
                    }
                } else if (xPosicionPokeballini3 - (int) (0.1 * anchoTotal) < xPosicionPersonaje && xPosicionPersonaje < xPosicionPokeballini3 + (int) (0.1 * anchoTotal)) {
                    if (yPosicionPokeballini3 - (int) (0.1 * altoTotal) < yPosicionPersonaje && yPosicionPersonaje < yPosicionPokeballini3 + (int) (0.1 * altoTotal)) {

                        puedesCogerInicial = true;
                    }
                } else {

                    puedesCogerInicial = false;
                }
            }
        }

        private void salidacasaIniciales() {
            if (xPosicionPersonaje <= (int) (0.6 * anchoTotal) && xPosicionPersonaje >= (int) (0.52 * anchoTotal) && yPosicionPersonaje >= (int) (0.78 * altoTotal) && yPosicionPersonaje <= (int) (0.79 * altoTotal)) {
                audio.pause();
                ciudad.putExtra("coorX", salidaIniX);
                ciudad.putExtra("coorY", salidaIniY);
                ciudad.putExtra("inicial", tienesUnInicial);
                startActivity(ciudad);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

        private void pokeBolasIni(Canvas canvas) {
            if (!tienesUnInicial) {
                pokeballini1.setBounds(xPosicionPokeballini1, yPosicionPokeballini1,
                        xPosicionPokeballini1 + pokeballini1.getIntrinsicWidth(),
                        yPosicionPokeballini1 + pokeballini1.getIntrinsicHeight());
                pokeballini1.draw(canvas);

                pokeballini2.setBounds(xPosicionPokeballini2, yPosicionPokeballini2,
                        xPosicionPokeballini2 + pokeballini2.getIntrinsicWidth(),
                        yPosicionPokeballini2 + pokeballini2.getIntrinsicHeight());
                pokeballini2.draw(canvas);

                pokeballini3.setBounds(xPosicionPokeballini3, yPosicionPokeballini3,
                        xPosicionPokeballini3 + pokeballini3.getIntrinsicWidth(),
                        yPosicionPokeballini3 + pokeballini3.getIntrinsicHeight());
                pokeballini3.draw(canvas);
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