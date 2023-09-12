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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ruta extends AppCompatActivity {
    Intent abrirMochila;
    Intent ciudad;
    ArrayList<String> pokemones;
    Intent combate;
    String pokemon;
    String choosePokemon;
    Bundle bundle;
    private int salidaIniX;
    private int salidaIniY;
    boolean hiloEmpezado;
    boolean cooldown;
    boolean hasCodigoPokeball = false;
    Display display;
    int anchoTotal;
    int altoTotal;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MediaPlayer audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        anchoTotal = display.getWidth();
        altoTotal = display.getHeight();
        abrirMochila = new Intent(this, Inventario.class);
        ciudad = new Intent(this, Ciudad.class);
        combate = new Intent(this, Combate.class);
        try {
            bundle = getIntent().getExtras();
            salidaIniX = bundle.getInt("coorX");
            salidaIniY = bundle.getInt("coorY");
        } catch (Exception e) {
            salidaIniX = (int) (0.53 * anchoTotal);
            salidaIniY = (int) (0.1 * altoTotal);

        }
        cooldown = true;
        hiloEmpezado = false;
        setContentView(new VistaRuta(this));
        play();
    }

    private void play() {
        if (audio == null) {
            audio = MediaPlayer.create(this, R.raw.ruta);
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
        private Drawable ruta;
        private Drawable inventario;
        private Drawable pokeball;
        ThreadJuego threadJuego;
        ThreadRandom threadRandom;
        private boolean aPulsadoUp = false;
        private boolean aPulsadoDo = false;
        private boolean aPulsadoLe = false;
        private boolean aPulsadoRi = false;
        private int VelocidadEntrenador = 1;
        boolean hasEncontradoPokemon = false;

        private int xPosicionMochila = (int) (0.85 * anchoTotal);
        private int yPosicionMochila = (int) (0.035 * altoTotal);

        private int xPosicionPokeball = (int) (0.15 * anchoTotal);
        private int yPosicionPokeball = (int) (0.5 * altoTotal);

        //private int xPosicionPersonaje = (int) (0.53 * anchoTotal);
        //private int yPosicionPersonaje = (int) (0.1 * altoTotal);

        private int xPosicionPersonaje = salidaIniX;
        private int yPosicionPersonaje = salidaIniY;


        private int xPosicionflechaizq = (int) (0.81 * anchoTotal);
        private int yPosicionflechaizq = (int) (0.78 * altoTotal);

        private int xPosicionflechaarr = (int) (0.86 * anchoTotal);
        private int yPosicionflechaarr = (int) (0.67 * altoTotal);

        private int xPosicionflechader = (int) (0.91 * anchoTotal);
        private int yPosicionflechader = (int) (0.78 * altoTotal);

        private int xPosicionflechaaba = (int) (0.86 * anchoTotal);
        private int yPosicionflechaaba = (int) (0.8 * altoTotal);

        String idPokeball;
        int numPokeballs;


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
            entrenador = getResources().getDrawable(R.drawable.entrenador, getContext().getTheme());
            flechaizq = getResources().getDrawable(R.drawable.izq, getContext().getTheme());
            flechader = getResources().getDrawable(R.drawable.der, getContext().getTheme());
            flechaarr = getResources().getDrawable(R.drawable.arriba, getContext().getTheme());
            flechaaba = getResources().getDrawable(R.drawable.abajo, getContext().getTheme());
            inventario = getResources().getDrawable(R.drawable.mochila, getContext().getTheme());
            ruta = getResources().getDrawable(R.drawable.ruta_pokemon, getContext().getTheme());
            pokeball = getResources().getDrawable(R.drawable.pokebola, getContext().getTheme());
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

        private void actualizarUp() {
            if (!aPulsadoDo && !aPulsadoRi && !aPulsadoLe && aPulsadoUp) {
                yPosicionPersonaje = yPosicionPersonaje - VelocidadEntrenador;
            }
        }

        private void actualizarDown() {
            if (aPulsadoDo && !aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                yPosicionPersonaje = yPosicionPersonaje + VelocidadEntrenador;
            }

        }

        private void actualizarLeft() {
            if (!aPulsadoDo && !aPulsadoRi && aPulsadoLe && !aPulsadoUp) {
                xPosicionPersonaje = xPosicionPersonaje - VelocidadEntrenador;
            }
        }

        private void actualizarRight() {
            if (!aPulsadoDo && aPulsadoRi && !aPulsadoLe && !aPulsadoUp) {
                xPosicionPersonaje = xPosicionPersonaje + VelocidadEntrenador;
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


        private void encontrarPokemon() {
            if (xPosicionPersonaje >= (int) (0.3 * anchoTotal) && xPosicionPersonaje <= (int) (0.85 * anchoTotal) && yPosicionPersonaje >= (int) (0.3 * altoTotal) && yPosicionPersonaje <= (int) (0.38 * altoTotal)) {
                if (!hiloEmpezado) {
                    hiloEmpezado = true;
                    threadRandom = new ThreadRandom();
                    threadRandom.start();
                }
            } else {
                cooldown = false;
                hiloEmpezado = false;
                hasEncontradoPokemon = false;
            }
        }


        private void recibirPokemon() {
            pokemones = new ArrayList<>();
            db.collection("pokemon")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String almacenarPokemon = String.valueOf(document.getData().get("nombre_poke"));
                                    pokemones.add(almacenarPokemon);
                                }
                                Collections.shuffle(pokemones);
                                pokemon = pokemones.get(0);
                                eligePokemon();
                            } else {
                                String error = ("Error getting documents.");
                                System.out.println(error);
                            }
                        }
                    });
        }

        private void eligePokemon() {
            db.collection("inventario").whereEqualTo("tipo", "Pokemon")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    choosePokemon = String.valueOf(document.getData().get("nombre"));
                                    System.out.println(choosePokemon);
                                    combatePokemon();
                                }
                            } else {
                                String error = ("Error getting documents.");
                                System.out.println(error);
                            }
                        }
                    });
        }


        private void combatePokemon() {
            if (hasEncontradoPokemon && !cooldown) {
                audio.pause();
                cooldown = true;
                aPulsadoDo = false;
                aPulsadoUp = false;
                aPulsadoRi = false;
                aPulsadoLe = false;
                combate.putExtra("tuPokemon", choosePokemon);
                combate.putExtra("pokemonRival", pokemon);
                combate.putExtra("coorX", xPosicionPersonaje);
                combate.putExtra("coorY", yPosicionPersonaje);
                startActivity(combate);
                overridePendingTransition(R.anim.blink, R.anim.fade_in);
            }
        }

        private void pintarTodo(Canvas canvas) {
            setBackground(ruta);
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

        private void cogerPokeball() {
            if (!hasCodigoPokeball) {
                if (xPosicionPokeball - (int) (0.1 * anchoTotal) < xPosicionPersonaje && xPosicionPersonaje < xPosicionPokeball + (int) (0.1 * anchoTotal)) {
                    if (yPosicionPokeball - (int) (0.1 * altoTotal) < yPosicionPersonaje && yPosicionPersonaje < yPosicionPokeball + (int) (0.1 * altoTotal)) {
                        hasCodigoPokeball = true;
                        sacarId();
                    }
                } else {
                    hasCodigoPokeball = false;
                }
            }
        }

        private void sacarId() {
            db.collection("inventario").whereEqualTo("nombre", "Pokeballs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idPokeball = document.getId();
                                    numPokeballs = Integer.parseInt(String.valueOf(document.getData().get("ps_poke")));
                                    addPoke();
                                }
                            } else {
                                System.out.println("EN EL ELSE ERROR DE BBDD");
                            }
                        }
                    });

        }

        private void addPoke() {
            Map<String, Object> updateV = new HashMap<>();
            numPokeballs++;
            updateV.put("ps_poke", numPokeballs);
            db.collection("inventario").document(idPokeball).update(updateV);
            System.out.println("Añadida");
        }


        private void pokeBolasIni(Canvas canvas) {
            if (!hasCodigoPokeball) {
                pokeball.setBounds(xPosicionPokeball, yPosicionPokeball,
                        xPosicionPokeball + pokeball.getIntrinsicWidth(),
                        yPosicionPokeball + pokeball.getIntrinsicHeight());
                pokeball.draw(canvas);
            }
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            pintarTodo(canvas);
            pokeBolasIni(canvas);
        }

        private void actualizar() {
            actualizarUp();
            actualizarDown();
            actualizarLeft();
            actualizarRight();
            encontrarPokemon();
            cogerPokeball();
            salidaRuta();
        }

        private void salidaRuta() {
            if (xPosicionPersonaje <= (int) (0.58 * anchoTotal) && xPosicionPersonaje >= (int) (0.48 * anchoTotal) && yPosicionPersonaje <= (int) (0.04 * altoTotal) && yPosicionPersonaje > (int) (0.02 * altoTotal)) {
                audio.pause();
                salidaIniX = (int) (0.235 * anchoTotal);
                salidaIniY = (int) (0.8 * altoTotal);
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

        class ThreadRandom extends Thread {
            int tiempo = (int) (Math.random() * 10000 + 5000);

            public void run() {
                while (!hasEncontradoPokemon) {
                    try {
                        sleep(tiempo);
                        System.out.println("Has encontrado un pokemon");
                        hasEncontradoPokemon = true;
                        recibirPokemon();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}