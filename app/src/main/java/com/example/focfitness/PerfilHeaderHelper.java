package com.example.focfitness;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PerfilHeaderHelper {

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    public static void cargarFotoPerfil(Activity activity) {
        ImageView imgHeaderPerfil = activity.findViewById(R.id.imgHeaderPerfil);
        if (imgHeaderPerfil == null) {
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            imgHeaderPerfil.setImageResource(R.drawable.ic_user);
            return;
        }

        FirebaseDatabase.getInstance(DB_URL).getReference("usuarios").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String imagenBase64 = obtenerTexto(snapshot, "imagenBase64");
                        String imagen = obtenerTexto(snapshot, "imagen");

                        if (!imagenBase64.isEmpty()) {
                            try {
                                byte[] bytesImagen = Base64.decode(imagenBase64, Base64.NO_WRAP);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.length);
                                imgHeaderPerfil.setImageBitmap(bitmap);
                                return;
                            } catch (IllegalArgumentException e) {
                                imgHeaderPerfil.setImageResource(R.drawable.ic_user);
                                return;
                            }
                        }

                        if (!imagen.isEmpty()) {
                            Picasso.get()
                                    .load(imagen)
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .into(imgHeaderPerfil);
                        } else {
                            imgHeaderPerfil.setImageResource(R.drawable.ic_user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        imgHeaderPerfil.setImageResource(R.drawable.ic_user);
                    }
                });
    }

    private static String obtenerTexto(DataSnapshot snapshot, String campo) {
        String valor = snapshot.child(campo).getValue(String.class);
        return valor != null ? valor.trim() : "";
    }
}
