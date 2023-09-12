package com.example.pokemon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Inventario extends AppCompatActivity {
    String urlImage;
    String nombre;
    String ataque1;
    String ataque2;
    int vida;
    TextView pokeballs;
    int contPokeballs;

    ImageView medalla2;
    ImageView pokebolagorda;
    ImageView mochila;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<ListElement> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        medalla2 = findViewById(R.id.medalla2);
        pokebolagorda = findViewById(R.id.pokebolagorda);
        mochila = findViewById(R.id.mochila);
        pokeballs = findViewById(R.id.txt_Pokeballs);
        elements = new ArrayList<>();
        cambiarColor();
        cargarFoto();
        establecerPokeballs();
    }

    private void cambiarColor() {
        medalla2.setColorFilter(Color.BLACK);
    }

    public void init() {
        ListAdapter listAdapter = new ListAdapter(elements, this);
        RecyclerView recyclerView = findViewById(R.id.list_inventario);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    private void cargarFoto() {
        db.collection("inventario").orderBy("ps_poke" , Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nombre = String.valueOf(document.getData().get("nombre"));
                                vida = Integer.parseInt(String.valueOf(document.getData().get("ps_poke")));
                                verFoto(nombre, vida);
                            }
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }

    private void establecerPokeballs() {
        db.collection("inventario").whereEqualTo("nombre", "Pokeballs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                contPokeballs = Integer.parseInt(String.valueOf(document.getData().get("ps_poke")));
                            }
                            pokeballs.setText(String.valueOf(contPokeballs));
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }

    private void verFoto(String nombre, int vida) {
        db.collection("pokemon").whereEqualTo("nombre_poke", nombre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                urlImage = String.valueOf(document.getData().get("img"));
                                ataque1 = String.valueOf(document.getData().get("ataque1"));
                                ataque2 = String.valueOf(document.getData().get("ataque2"));
                                elements.add(new ListElement(urlImage, nombre, ataque1, ataque2, vida));
                            }
                            init();
                        } else {
                            String error = ("Error getting documents.");
                            System.out.println(error);
                        }
                    }
                });
    }
}