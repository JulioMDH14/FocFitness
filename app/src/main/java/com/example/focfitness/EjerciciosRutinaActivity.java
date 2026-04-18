package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class EjerciciosRutinaActivity extends AppCompatActivity {

    private static final String API_KEY = "58a99b5f02mshb8a84833584f487p1824d3jsn11e5f36eb4b1";
    private static final String API_HOST = "exercisedb.p.rapidapi.com";

    private static final Map<String, List<String[]>> EJERCICIOS = new HashMap<>();

    private static final Map<String, String> DIFICULTAD_ES = new HashMap<String, String>() {{
        put("beginner", "Principiante"); put("intermediate", "Intermedio"); put("advanced", "Avanzado");
    }};

    private static final Map<String, String> MUSCULO_ES = new HashMap<String, String>() {{
        put("pectorals", "Pectorales"); put("biceps", "Bíceps"); put("triceps", "Tríceps");
        put("shoulders", "Hombros"); put("lats", "Dorsal ancho"); put("upper back", "Espalda alta");
        put("lower back", "Espalda baja"); put("quads", "Cuádriceps"); put("hamstrings", "Isquiotibiales");
        put("glutes", "Glúteos"); put("abs", "Abdominales"); put("calves", "Gemelos");
        put("spine", "Columna"); put("serratus anterior", "Serrato"); put("delts", "Deltoides");
        put("upper arms", "Brazos"); put("forearms", "Antebrazos"); put("chest", "Pecho");
        put("back", "Espalda"); put("waist", "Cintura"); put("cardiovascular system", "Cardio");
    }};

    static {
        EJERCICIOS.put("Tren Superior", Arrays.asList(
                new String[]{"Press banca",        "Pecho con barra en banco plano",       "barbell bench press",      "1. Túmbate en el banco con los pies en el suelo.\n2. Agarra la barra un poco más ancha que los hombros.\n3. Baja la barra hasta el pecho controlando el movimiento.\n4. Empuja hacia arriba hasta extender los brazos.\n5. Repite el movimiento de forma controlada."},
                new String[]{"Press militar",      "Hombros con barra de pie o sentado",   "barbell overhead press",   "1. Coloca la barra a la altura de los hombros.\n2. Agarra la barra con las manos a la anchura de los hombros.\n3. Empuja la barra hacia arriba hasta extender los brazos.\n4. Baja lentamente hasta la posición inicial.\n5. Mantén el core tenso durante todo el movimiento."},
                new String[]{"Dominadas",          "Espalda y bíceps con peso corporal",   "pullup",                   "1. Agarra la barra con las palmas hacia fuera.\n2. Cuelga con los brazos totalmente extendidos.\n3. Tira hacia arriba hasta que la barbilla supere la barra.\n4. Baja lentamente a la posición inicial.\n5. Evita balancearte durante el ejercicio."},
                new String[]{"Remo con barra",     "Espalda media e inferior",             "barbell bent over row",    "1. Inclínate hacia adelante con la espalda recta.\n2. Agarra la barra con las manos a la anchura de los hombros.\n3. Tira de la barra hacia el abdomen apretando los omóplatos.\n4. Baja controladamente a la posición inicial.\n5. Mantén la espalda recta en todo momento."},
                new String[]{"Fondos",             "Pecho y tríceps con peso corporal",    "chest dip",                "1. Agarra las paralelas y elévate con los brazos extendidos.\n2. Inclínate ligeramente hacia adelante para trabajar el pecho.\n3. Baja hasta que los codos estén a 90 grados.\n4. Empuja hacia arriba hasta la posición inicial.\n5. Controla el descenso para mayor efectividad."},
                new String[]{"Curl bíceps",        "Bíceps con mancuernas o barra",        "barbell curl",             "1. Sostén la barra con los brazos extendidos.\n2. Mantén los codos pegados al cuerpo.\n3. Sube la barra hasta los hombros contrayendo el bíceps.\n4. Baja lentamente a la posición inicial.\n5. No balancees el cuerpo para subir más peso."},
                new String[]{"Extensiones tríceps","Tríceps en polea o con mancuerna",     "cable pushdown",           "1. Sujeta la cuerda de la polea alta.\n2. Mantén los codos pegados al cuerpo.\n3. Empuja hacia abajo extendiendo completamente los brazos.\n4. Vuelve lentamente a la posición inicial.\n5. No muevas los codos durante el ejercicio."},
                new String[]{"Aperturas",          "Pecho con mancuernas en banco",        "dumbbell flyes",           "1. Túmbate en el banco con una mancuerna en cada mano.\n2. Extiende los brazos sobre el pecho con codos ligeramente flexionados.\n3. Abre los brazos hacia los lados describiendo un arco.\n4. Vuelve a la posición inicial apretando el pecho.\n5. Controla el movimiento en ambas direcciones."}
        ));
        EJERCICIOS.put("Tren Inferior", Arrays.asList(
                new String[]{"Sentadillas",            "Cuádriceps, glúteos e isquios",       "barbell squat",      "1. Coloca la barra en los trapecios y separa los pies a la anchura de los hombros.\n2. Baja flexionando rodillas y caderas como si te sentaras.\n3. Desciende hasta que los muslos estén paralelos al suelo.\n4. Sube empujando con los talones.\n5. Mantén la espalda recta y las rodillas alineadas."},
                new String[]{"Peso muerto",            "Isquios, glúteos y espalda baja",     "barbell deadlift",   "1. Coloca los pies a la anchura de las caderas frente a la barra.\n2. Agáchate y agarra la barra con las manos a la anchura de los hombros.\n3. Sube la barra pegándola al cuerpo extendiendo piernas y cadera.\n4. Al llegar arriba, aprieta glúteos.\n5. Baja controlando el movimiento."},
                new String[]{"Prensa de piernas",      "Cuádriceps y glúteos en máquina",     "leg press",          "1. Siéntate en la máquina con los pies a la anchura de los hombros.\n2. Desbloquea la plataforma y flexiona las rodillas hacia el pecho.\n3. Empuja la plataforma hasta casi extender las piernas.\n4. Vuelve lentamente a la posición inicial.\n5. No bloquees las rodillas al extender."},
                new String[]{"Zancadas",               "Cuádriceps y glúteos con mancuernas", "dumbbell lunge",     "1. De pie con una mancuerna en cada mano.\n2. Da un paso largo hacia adelante con una pierna.\n3. Baja la rodilla trasera casi hasta el suelo.\n4. Vuelve a la posición inicial empujando con el talón delantero.\n5. Alterna piernas en cada repetición."},
                new String[]{"Curl femoral",           "Isquiotibiales en máquina",           "lying leg curl",     "1. Túmbate boca abajo en la máquina.\n2. Coloca los talones bajo el rodillo.\n3. Dobla las rodillas llevando los talones hacia los glúteos.\n4. Baja lentamente a la posición inicial.\n5. Contrae los isquios en la parte alta del movimiento."},
                new String[]{"Extensiones cuádriceps", "Cuádriceps en máquina",               "leg extension",      "1. Siéntate en la máquina con las espinillas bajo el rodillo.\n2. Extiende las piernas levantando el peso hasta quedar rectas.\n3. Aguanta un segundo arriba apretando el cuádriceps.\n4. Baja lentamente controlando el peso.\n5. No uses impulso para subir."},
                new String[]{"Elevación de talones",   "Gemelos de pie o sentado",            "calf raise",         "1. De pie con los pies a la anchura de los hombros.\n2. Sube de puntillas elevando los talones lo máximo posible.\n3. Aguanta un segundo arriba contrayendo los gemelos.\n4. Baja lentamente hasta casi tocar el suelo.\n5. Puedes hacerlo en un escalón para mayor rango."}
        ));
        EJERCICIOS.put("Pecho", Arrays.asList(
                new String[]{"Press banca plano",  "Pecho completo con barra",            "barbell bench press",          "1. Túmbate en el banco con los pies planos en el suelo.\n2. Agarra la barra algo más ancha que los hombros.\n3. Baja la barra hasta el pecho de forma controlada.\n4. Empuja hacia arriba hasta extender los brazos.\n5. Repite manteniendo la espalda apoyada."},
                new String[]{"Press inclinado",    "Pecho superior con barra",            "barbell incline bench press",  "1. Inclina el banco entre 30 y 45 grados.\n2. Agarra la barra a la anchura de los hombros.\n3. Baja la barra hacia la parte alta del pecho.\n4. Empuja hacia arriba y ligeramente hacia atrás.\n5. Controla el descenso en todo momento."},
                new String[]{"Press declinado",    "Pecho inferior con barra",            "barbell decline bench press",  "1. Ajusta el banco en declive y fija los pies.\n2. Agarra la barra a la anchura de los hombros.\n3. Baja la barra hacia la parte baja del pecho.\n4. Empuja hacia arriba extendiendo los brazos.\n5. Ten cuidado al desbloquear la barra."},
                new String[]{"Aperturas en polea", "Pecho con cables cruzados",           "cable crossover",              "1. Coloca las poleas en posición alta.\n2. Agarra un cable con cada mano y da un paso adelante.\n3. Con codos ligeramente flexionados, junta las manos frente al pecho.\n4. Vuelve lentamente a la posición inicial.\n5. Siente el estiramiento del pecho en cada repetición."},
                new String[]{"Fondos",             "Pecho inferior y tríceps",            "chest dip",                    "1. Agarra las paralelas y elévate con brazos extendidos.\n2. Inclínate hacia adelante para enfocarte en el pecho.\n3. Baja hasta que los codos estén a 90 grados.\n4. Empuja hacia arriba a la posición inicial.\n5. Controla especialmente el descenso."},
                new String[]{"Pullover",           "Pecho y espalda con mancuerna",       "dumbbell pullover",            "1. Túmbate en el banco con los hombros apoyados.\n2. Sujeta la mancuerna con ambas manos sobre el pecho.\n3. Baja la mancuerna por encima de la cabeza en arco.\n4. Vuelve a la posición inicial contrayendo el pecho.\n5. Mantén los codos ligeramente flexionados."},
                new String[]{"Press mancuernas",   "Pecho con mayor rango de movimiento", "dumbbell bench press",         "1. Túmbate con una mancuerna en cada mano a la altura del pecho.\n2. Empuja hacia arriba hasta casi extender los brazos.\n3. Baja las mancuernas controlando el movimiento.\n4. Mayor rango que con barra al poder bajar más.\n5. Mantén las muñecas neutras."}
        ));
        EJERCICIOS.put("Espalda", Arrays.asList(
                new String[]{"Dominadas",        "Dorsal ancho y bíceps",        "pullup",              "1. Agarra la barra con palmas hacia fuera.\n2. Cuelga con brazos totalmente extendidos.\n3. Tira hacia arriba hasta que la barbilla supere la barra.\n4. Baja lentamente a la posición inicial.\n5. Evita el balanceo."},
                new String[]{"Remo con barra",   "Espalda media con barra",      "barbell bent over row","1. Inclínate con la espalda recta a 45 grados.\n2. Agarra la barra a la anchura de los hombros.\n3. Tira hacia el abdomen apretando los omóplatos.\n4. Baja controladamente.\n5. No redondees la espalda."},
                new String[]{"Remo en polea",    "Espalda media en máquina",     "cable seated row",    "1. Siéntate frente a la polea con las rodillas ligeramente flexionadas.\n2. Agarra el mango y tira hacia el abdomen.\n3. Aprieta los omóplatos al final del movimiento.\n4. Extiende los brazos volviendo a la posición inicial.\n5. Mantén la espalda recta."},
                new String[]{"Jalones al pecho", "Dorsal ancho en polea alta",   "lat pulldown",        "1. Siéntate con los muslos bajo los soportes.\n2. Agarra la barra ancha con palmas hacia fuera.\n3. Tira hacia abajo llevando la barra al pecho.\n4. Vuelve lentamente a la posición inicial.\n5. No te eches hacia atrás para ayudarte."},
                new String[]{"Peso muerto",      "Espalda completa e isquios",   "barbell deadlift",    "1. Pies a la anchura de caderas frente a la barra.\n2. Agarra la barra y mantén la espalda recta.\n3. Levanta empujando con las piernas y extendiendo la cadera.\n4. Baja controlando el movimiento.\n5. La barra siempre pegada al cuerpo."},
                new String[]{"Remo mancuerna",   "Espalda unilateral",           "dumbbell one arm row","1. Apoya una rodilla y la mano del mismo lado en el banco.\n2. Con la otra mano sujeta la mancuerna con el brazo extendido.\n3. Tira de la mancuerna hacia la cadera.\n4. Baja lentamente.\n5. Mantén la espalda paralela al suelo."},
                new String[]{"Hiperextensiones", "Espalda baja en banco romano", "hyperextension",      "1. Colócate en el banco romano con las caderas en el borde.\n2. Cruza los brazos sobre el pecho.\n3. Baja el tronco hacia abajo manteniendo la espalda recta.\n4. Sube hasta que el cuerpo forme una línea recta.\n5. No hiperextiendas la espalda al subir."}
        ));
        EJERCICIOS.put("Cardio", Arrays.asList(
                new String[]{"Cinta de correr",    "Cardio de baja o alta intensidad",  "run treadmill",    "1. Comienza caminando 2-3 minutos para calentar.\n2. Aumenta la velocidad progresivamente.\n3. Mantén la postura erguida con los brazos balanceando.\n4. Alterna intervalos de intensidad si quieres más rendimiento.\n5. Termina con 2-3 minutos caminando para enfriar."},
                new String[]{"Bicicleta estática", "Cardio de bajo impacto",            "stationary bike",  "1. Ajusta el sillín a la altura de la cadera.\n2. Comienza pedaleando suave para calentar.\n3. Aumenta la resistencia o velocidad progresivamente.\n4. Mantén la espalda recta y el core activo.\n5. Pedalea durante 20-40 minutos según tu objetivo."},
                new String[]{"Burpees",            "Cardio y fuerza funcional",         "burpee",           "1. De pie, desciende al suelo con las manos.\n2. Lanza los pies hacia atrás hasta posición de plancha.\n3. Haz una flexión (opcional).\n4. Vuelve los pies a las manos.\n5. Salta hacia arriba con los brazos en alto."},
                new String[]{"Mountain climbers",  "Cardio y core combinados",          "mountain climber", "1. Comienza en posición de plancha alta.\n2. Lleva una rodilla hacia el pecho rápidamente.\n3. Vuelve y alterna con la otra pierna.\n4. Mantén las caderas bajas y el core activo.\n5. Aumenta la velocidad para más intensidad cardio."},
                new String[]{"Jumping jacks",      "Calentamiento y cardio ligero",     "jumping jacks",    "1. De pie con pies juntos y brazos a los lados.\n2. Salta abriendo piernas a la anchura de los hombros.\n3. Al mismo tiempo, sube los brazos por encima de la cabeza.\n4. Salta volviendo a la posición inicial.\n5. Mantén un ritmo constante."},
                new String[]{"Saltar comba",       "Cardio de alta intensidad",         "jump rope",        "1. Sujeta los mangos de la comba a la altura de las caderas.\n2. Salta con ambos pies juntos al ritmo de la comba.\n3. Mantén los codos cerca del cuerpo.\n4. Aterriza suavemente en la punta de los pies.\n5. Alterna entre salto básico e intervalos rápidos."}
        ));
        EJERCICIOS.put("Core", Arrays.asList(
                new String[]{"Plancha",           "Core completo isométrico",      "plank",          "1. Apoya los antebrazos y las puntas de los pies.\n2. Mantén el cuerpo en línea recta de cabeza a talones.\n3. Aprieta el abdomen y los glúteos.\n4. No dejes caer las caderas ni las subas.\n5. Aguanta el tiempo indicado respirando con normalidad."},
                new String[]{"Crunch",            "Abdominales superiores",        "crunch",         "1. Túmbate boca arriba con rodillas flexionadas.\n2. Coloca las manos detrás de la cabeza sin entrelazar los dedos.\n3. Sube el tronco llevando los hombros hacia las rodillas.\n4. Baja lentamente sin llegar a apoyar del todo.\n5. Exhala al subir, inhala al bajar."},
                new String[]{"Elevación piernas", "Abdominales inferiores",        "lying leg raise","1. Túmbate boca arriba con las piernas extendidas.\n2. Coloca las manos bajo los glúteos para apoyar la zona lumbar.\n3. Sube las piernas juntas hasta los 90 grados.\n4. Baja lentamente sin que toquen el suelo.\n5. Mantén el abdomen contraído en todo momento."},
                new String[]{"Russian twist",     "Oblicuos con o sin peso",       "russian twist",  "1. Siéntate con las rodillas flexionadas y el tronco inclinado 45 grados.\n2. Junta las manos frente al pecho (o sujeta un peso).\n3. Gira el tronco a la derecha tocando el suelo con las manos.\n4. Vuelve al centro y gira al otro lado.\n5. Puedes levantar los pies para más dificultad."},
                new String[]{"Bird dog",          "Estabilidad lumbar y core",     "bird dog",       "1. A cuatro patas con manos bajo los hombros y rodillas bajo las caderas.\n2. Extiende simultáneamente el brazo derecho y la pierna izquierda.\n3. Mantén la posición 2-3 segundos con el core activo.\n4. Vuelve al centro y cambia al lado contrario.\n5. Evita rotar las caderas al extender."},
                new String[]{"Rueda abdominal",   "Core completo avanzado",        "ab roller",      "1. Arrodíllate con la rueda frente a ti.\n2. Agarra los mangos y rueda hacia adelante extendiendo el cuerpo.\n3. Llega lo más lejos que puedas sin que las caderas toquen el suelo.\n4. Contrae el abdomen para volver a la posición inicial.\n5. Es un ejercicio avanzado, empieza con poco rango."},
                new String[]{"Plancha lateral",   "Oblicuos isométrico",           "side plank",     "1. Apoya el antebrazo en el suelo y apila los pies.\n2. Eleva las caderas formando una línea recta.\n3. Extiende el brazo libre hacia el techo.\n4. Aprieta el oblicuo del lado que está arriba.\n5. Mantén el tiempo indicado y cambia de lado."}
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
                String[] e = ejercicios.get(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(e[0]);
                ((TextView) view.findViewById(android.R.id.text2)).setText(e[1]);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(
                        android.graphics.Color.parseColor("#111827"));
                ((TextView) view.findViewById(android.R.id.text2)).setTextColor(
                        android.graphics.Color.parseColor("#6B7280"));
                return view;
            }
        });

        listaEjerciciosRutina.setOnItemClickListener((parent, view, position, id) ->
                mostrarDetalle(ejercicios.get(position)));
    }

    private void mostrarDetalle(String[] ejercicio) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ejercicio_gif, null);

        TextView tvNombre        = dialogView.findViewById(R.id.tvNombreDialog);
        TextView tvCargando      = dialogView.findViewById(R.id.tvCargando);
        LinearLayout seccion     = dialogView.findViewById(R.id.seccionInfo);
        TextView tvDificultad    = dialogView.findViewById(R.id.tvDificultad);
        TextView tvMusculo       = dialogView.findViewById(R.id.tvMusculo);
        TextView tvDescripcion   = dialogView.findViewById(R.id.tvDescripcion);
        TextView tvInstrucciones = dialogView.findViewById(R.id.tvInstrucciones);

        tvNombre.setText(ejercicio[0]);
        tvDescripcion.setText(ejercicio[1]);
        tvInstrucciones.setText(ejercicio[3]);
        tvCargando.setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .create();
        dialog.show();

        new Thread(() -> {
            JSONObject datos = buscarInfo(ejercicio[2]);
            runOnUiThread(() -> {
                tvCargando.setVisibility(View.GONE);
                if (datos != null) {
                    String dif = DIFICULTAD_ES.getOrDefault(datos.optString("difficulty"), "");
                    String mus = MUSCULO_ES.getOrDefault(datos.optString("target"), datos.optString("target"));
                    tvDificultad.setText(dif.isEmpty() ? "—" : dif);
                    tvMusculo.setText(mus.isEmpty() ? "—" : mus);
                }
                seccion.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private JSONObject buscarInfo(String nombreIngles) {
        try {
            String query = nombreIngles.replace(" ", "%20");
            URL url = new URL("https://exercisedb.p.rapidapi.com/exercises/name/" + query + "?limit=1&offset=0");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", API_HOST);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            if (conn.getResponseCode() != 200) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONArray array = new JSONArray(sb.toString());
            if (array.length() > 0) return array.getJSONObject(0);
        } catch (Exception ignored) {
        }
        return null;
    }
}
