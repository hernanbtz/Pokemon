package com.example.pokemon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Combate extends AppCompatActivity {
    Button ataque1;
    Button ataque2;
    Button huir;
    Button atrapar;
    String ataque1s;
    String ataque2s;
    String ataque1sRival;
    String ataque2sRival;
    ArrayList<String> ataquesR;
    String ataque;
    String ataqueRival;
    ImageView tuPokemon;
    String tuPokemons;
    ImageView pokemonRival;
    String pokemonRivals;
    ImageView pokeball;
    Bundle bundle;
    ProgressBar barra1;
    ProgressBar barra2;
    int damage;
    int barra2progress;
    int barra1progress;
    int salidaX;
    int salidaY;
    int probabilidad;
    int capturar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ThreadRandom threadRandom;
    ThreadRandom2 threadRandom2;
    TextView area;
    Intent huida;
    boolean hiloEmpezado = false;
    Map<String, Object> addPoke;
    Animation animshake;
    Animation moveleft;
    Animation moveright;
    Animation fade_out;

    boolean hasGanado = true;
    String idPoke;


    String idPokeball;
    int numPokeballs;

    MediaPlayer audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combate);
        bundle = getIntent().getExtras();
        ataque1 = findViewById(R.id.btn_ataque1);
        ataque2 = findViewById(R.id.btn_ataque2);
        huir = findViewById(R.id.btn_huir);
        atrapar = findViewById(R.id.btn_atrapar);
        tuPokemon = findViewById(R.id.image_tuPokemon);
        pokemonRival = findViewById(R.id.image_pokemonRival);
        pokeball = findViewById(R.id.image_pokeball);
        pokeball.setVisibility(View.INVISIBLE);
        animshake = AnimationUtils.loadAnimation(this, R.anim.shake);
        animshake.reset();
        moveleft = AnimationUtils.loadAnimation(this, R.anim.move_left);
        moveleft.reset();
        moveright = AnimationUtils.loadAnimation(this, R.anim.move_right);
        moveright.reset();
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fade_out.reset();
        tuPokemons = bundle.getString("tuPokemon");
        pokemonRivals = bundle.getString("pokemonRival");
        salidaX = bundle.getInt("coorX");
        salidaY = bundle.getInt("coorY");
        huida = new Intent(this, Ruta.class);
        ponerAtaques();
        ataquesRivales();
        verFoto();
        ponerVida();
        play();
        barra1 = findViewById(R.id.ProgressBar01);
        barra2 = findViewById(R.id.progressBar02);
        area = findViewById(R.id.area_txt);
        area.setText("¿Que te gustaria hacer?");
        ataque1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ataque = ataque1s;
                atacar();
            }
        });
        ataque2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ataque = ataque2s;
                atacar();
            }
        });
        huir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                huida();
            }
        });
        atrapar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atraparPokemon();
            }
        });

    }

    private void play() {
        if (audio == null) {
            audio = MediaPlayer.create(this, R.raw.combate);
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

    private void ataquesRivales() {
        ataquesR = new ArrayList<>();
        db.collection("pokemon").whereEqualTo("nombre_poke", pokemonRivals)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ataque1sRival = String.valueOf(document.getData().get("ataque1"));
                                ataquesR.add(ataque1sRival);
                                ataque2sRival = String.valueOf(document.getData().get("ataque2"));
                                ataquesR.add(ataque2sRival);
                            }
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }

    private void ataqueRival() {
        Collections.shuffle(ataquesR);
        ataqueRival = ataquesR.get(0);
    }

    private void atraparPokemon() {
        sacarIdPoke();
        if (numPokeballs > 0) {
            addPoke();
            barra1progress = barra1.getProgress();
            barra2progress = barra2.getProgress();
            if (barra1progress > 0) {
                if (barra2progress <= 0) {
                    probabilidad = 100;
                }
                if (barra2progress <= 20 && barra2progress > 0) {
                    probabilidad = 80;
                }
                if (barra2progress <= 50 && barra2progress > 20) {
                    probabilidad = 60;
                }
                if (barra2progress <= 80 && barra2progress > 50) {
                    probabilidad = 40;
                }
                if (barra2progress <= 100 && barra2progress > 80) {
                    probabilidad = 20;
                }
                capturar = (int) (Math.random() * 100 + probabilidad);
                if (capturar > 50) {
                    aniadirPokemon();
                    atrapar.setVisibility(View.INVISIBLE);
                    huir.setVisibility(View.INVISIBLE);
                    ataque1.setVisibility(View.INVISIBLE);
                    ataque2.setVisibility(View.INVISIBLE);
                    barra2.setVisibility(View.INVISIBLE);
                    pokeball.setVisibility(View.VISIBLE);
                    //pokemonRival.setVisibility(View.INVISIBLE);
                    area.setText("Has atrapado a " + pokemonRivals);
                    threadRandom2 = new ThreadRandom2();
                    threadRandom2.start();
                } else {
                    area.setText("No has conseguido capturar a " + pokemonRivals);
                }
            }
        }
    }

    private void aniadirPokemon() {
        addPoke = new HashMap<>();
        addPoke.put("nombre", pokemonRivals);
        addPoke.put("tipo", "Pokemon");
        addPoke.put("ps_poke", barra2progress);
        db.collection("inventario")
                .add(addPoke)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        area.setText(pokemonRivals + " fue añadido al inventario");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error");
                    }
                });
    }

    private void finalizarCombate() {
        barra2progress = barra2.getProgress();
        barra1progress = barra1.getProgress();
        if (barra2progress <= 0) {
            hiloEmpezado = false;
            area.setText("El combate ha finalizado, HAS GANADO");
            ataque1.setVisibility(View.INVISIBLE);
            ataque2.setVisibility(View.INVISIBLE);
            hasGanado = true;
        } else if (barra1progress <= 0) {
            hiloEmpezado = false;
            ataque1.setVisibility(View.INVISIBLE);
            ataque2.setVisibility(View.INVISIBLE);
            area.setText("El combate ha finalizado, HAS PERDIDO");
            hasGanado = false;
        }
    }


    private void huida() {
        audio.pause();
        huida.putExtra("coorX", salidaX);
        huida.putExtra("coorY", salidaY);
        startActivity(huida);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void atacar() {
        hiloEmpezado = true;
        threadRandom = new ThreadRandom();
        threadRandom.start();
    }

    private void ponerAtaques() {
        db.collection("pokemon").whereEqualTo("nombre_poke", tuPokemons)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ataque1s = String.valueOf(document.getData().get("ataque1"));
                                ataque1.setText(ataque1s);
                                ataque2s = String.valueOf(document.getData().get("ataque2"));
                                ataque2.setText(ataque2s);
                            }
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }

    private void sacarIdPoke() {
        db.collection("inventario").whereEqualTo("nombre", "Pokeballs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idPokeball = document.getId();
                                numPokeballs = Integer.parseInt(String.valueOf(document.getData().get("ps_poke")));
                            }
                        } else {
                            System.out.println("EN EL ELSE ERROR DE BBDD");
                        }
                    }
                });

    }

    private void addPoke() {
        Map<String, Object> updateV = new HashMap<>();
        numPokeballs--;
        updateV.put("ps_poke", numPokeballs);
        db.collection("inventario").document(idPokeball).update(updateV);
        System.out.println("Restada");
    }

    private void sacarId() {
        db.collection("inventario").whereEqualTo("nombre", tuPokemons)
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

    private void ponerVida() {
        db.collection("inventario").whereEqualTo("nombre", tuPokemons)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                barra1.setProgress(Integer.parseInt(String.valueOf(document.getData().get("ps_poke"))));
                            }
                        } else {
                            System.out.println("EN EL ELSE ERROR DE BBDD");
                        }
                    }
                });
    }

    private void actualizarVida() {
        Map<String, Object> updateV = new HashMap<>();
        updateV.put("ps_poke", barra1progress);
        db.collection("inventario").document(idPoke)
                .update(updateV);
    }

    private void verFoto() {
        db.collection("pokemon").whereEqualTo("nombre_poke", tuPokemons)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = String.valueOf(document.getData().get("img"));
                                System.out.println(url);
                                Picasso.get().load(url).into(tuPokemon);
                            }
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
        db.collection("pokemon").whereEqualTo("nombre_poke", pokemonRivals)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = String.valueOf(document.getData().get("img"));
                                Picasso.get().load(url).into(pokemonRival);
                            }
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }


    class ThreadRandom extends Thread {
        int tiempo = 3000;

        public void run() {
            if (hiloEmpezado) {
                try {
                    area.setText("Has utilizado " + ataque);
                    tuPokemon.startAnimation(moveleft);
                    sleep(tiempo);
                    barra2progress = barra2.getProgress();
                    damage = (int) (Math.random() * 59 + 1);
                    barra2progress = barra2progress - damage;
                    barra2.setProgress(barra2progress);
                    area.setText("Has inflingido " + damage + " de daño");
                    finalizarCombate();
                    if (hiloEmpezado) {
                        sleep(tiempo);
                        ataqueRival();
                        area.setText(pokemonRivals + " ha utilizado " + ataqueRival);
                        pokemonRival.startAnimation(moveright);
                        sleep(tiempo);
                        barra1progress = barra1.getProgress();
                        damage = (int) (Math.random() * 59 + 1);
                        barra1progress = barra1progress - damage;
                        barra1.setProgress(barra1progress);
                        area.setText("Te han inflingido " + damage + " de daño");
                        sacarId();
                        finalizarCombate();
                        sleep(tiempo);
                        if (!hasGanado) {
                            huida();
                        } else {
                            area.setText("¿Que te gustaria hacer?");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class ThreadRandom2 extends Thread {
        public void run() {
            pokeball.startAnimation(animshake);
            try {
                sleep(2900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pokemonRival.startAnimation(fade_out);
            pokeball.setVisibility(View.INVISIBLE);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pokemonRival.setVisibility(View.INVISIBLE);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            huida();
        }

    }
}