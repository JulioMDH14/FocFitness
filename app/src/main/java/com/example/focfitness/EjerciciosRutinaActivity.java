package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;

public class EjerciciosRutinaActivity extends AppCompatActivity {

    private static final Map<String, List<String[]>> EJERCICIOS = new HashMap<>();

    static {
        EJERCICIOS.put("Tren Superior", Arrays.asList(
                new String[]{"Press banca", "Pecho con barra en banco plano"},
                new String[]{"Press militar", "Hombros con barra de pie o sentado"},
                new String[]{"Dominadas", "Espalda y bíceps con peso corporal"},
                new String[]{"Remo con barra", "Espalda media e inferior"},
                new String[]{"Fondos en paralelas", "Pecho y tríceps con peso corporal"},
                new String[]{"Curl bíceps", "Bíceps con mancuernas o barra"},
                new String[]{"Extensiones tríceps", "Tríceps en polea o con mancuerna"},
                new String[]{"Aperturas", "Pecho con mancuernas en banco"}
        ));
        EJERCICIOS.put("Tren Inferior", Arrays.asList(
                new String[]{"Sentadillas", "Cuádriceps, glúteos e isquios"},
                new String[]{"Peso muerto", "Isquios, glúteos y espalda baja"},
                new String[]{"Prensa de piernas", "Cuádriceps y glúteos en máquina"},
                new String[]{"Zancadas", "Cuádriceps y glúteos con mancuernas"},
                new String[]{"Curl femoral", "Isquiotibiales en máquina"},
                new String[]{"Extensiones de cuádriceps", "Cuádriceps en máquina"},
                new String[]{"Elevación de talones", "Gemelos de pie o sentado"}
        ));
        EJERCICIOS.put("Pecho", Arrays.asList(
                new String[]{"Press banca plano", "Pecho completo con barra"},
                new String[]{"Press banca inclinado", "Pecho superior con barra"},
                new String[]{"Press banca declinado", "Pecho inferior con barra"},
                new String[]{"Aperturas en polea", "Pecho con cables cruzados"},
                new String[]{"Fondos en paralelas", "Pecho inferior y tríceps"},
                new String[]{"Pullover", "Pecho y espalda con mancuerna"},
                new String[]{"Press con mancuernas", "Pecho con mayor rango de movimiento"}
        ));
        EJERCICIOS.put("Espalda", Arrays.asList(
                new String[]{"Dominadas", "Dorsal ancho y bíceps"},
                new String[]{"Remo con barra", "Espalda media con barra"},
                new String[]{"Remo en polea baja", "Espalda media en máquina"},
                new String[]{"Jalones al pecho", "Dorsal ancho en polea alta"},
                new String[]{"Peso muerto", "Espalda completa e isquios"},
                new String[]{"Remo con mancuerna", "Espalda unilateral"},
                new String[]{"Hiperextensiones", "Espalda baja en banco romano"}
        ));
        EJERCICIOS.put("Cardio", Arrays.asList(
                new String[]{"Cinta de correr", "Cardio de baja o alta intensidad"},
                new String[]{"Bicicleta estática", "Cardio de bajo impacto"},
                new String[]{"Elíptica", "Cardio de cuerpo completo"},
                new String[]{"Remo", "Cardio y fuerza de espalda"},
                new String[]{"Saltar a la comba", "Cardio de alta intensidad"},
                new String[]{"Burpees", "Cardio y fuerza funcional"},
                new String[]{"Mountain climbers", "Cardio y core combinados"},
                new String[]{"Jumping jacks", "Calentamiento y cardio ligero"}
        ));
        EJERCICIOS.put("Core", Arrays.asList(
                new String[]{"Plancha", "Core completo isométrico"},
                new String[]{"Crunch abdominal", "Abdominales superiores"},
                new String[]{"Elevación de piernas", "Abdominales inferiores"},
                new String[]{"Russian twist", "Oblicuos con o sin peso"},
                new String[]{"Bird dog", "Estabilidad lumbar y core"},
                new String[]{"Dead bug", "Core profundo y coordinación"},
                new String[]{"Rueda abdominal", "Core completo avanzado"},
                new String[]{"Plancha lateral", "Oblicuos isométrico"}
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios_rutina);

        String categoria = getIntent().getStringExtra("categoria");
        TextView tvCategoria = findViewById(R.id.tvCategoria);
        ListView listaEjerciciosRutina = findViewById(R.id.listaEjerciciosRutina);

        tvCategoria.setText(categoria);

        List<String[]> ejercicios = EJERCICIOS.getOrDefault(categoria, new ArrayList<>());

        listaEjerciciosRutina.setAdapter(new ArrayAdapter<String[]>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, ejercicios) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] ejercicio = ejercicios.get(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(ejercicio[0]);
                ((TextView) view.findViewById(android.R.id.text2)).setText(ejercicio[1]);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(
                        android.graphics.Color.parseColor("#111827"));
                ((TextView) view.findViewById(android.R.id.text2)).setTextColor(
                        android.graphics.Color.parseColor("#6B7280"));
                return view;
            }
        });
    }
}
