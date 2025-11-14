package com.example.asados_steaks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private ImageView imageFoto;
    private EditText editNombre, editTelefono, editDescripcion;
    private TextView textUbicacion;
    private Button buttonTomarFoto, buttonSeleccionarFoto, buttonUbicacion, buttonGuardar;

    private FusedLocationProviderClient fusedLocationClient;
    private double lat = 0.0, lng = 0.0;

    // Uri listo para subir a Firebase (desde cámara o galería)
    private Uri photoUri;
    // Archivo temporal de cámara
    private File photoFile;

    // Códigos de permiso
    private static final int REQ_CAMERA = 100;
    private static final int REQ_GALLERY = 101;
    private static final int REQ_MAP = 102;

    // Launchers modernos (evitan onActivityResult deprecado)
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> mapLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        imageFoto = findViewById(R.id.imageFoto);
        editNombre = findViewById(R.id.editNombre);
        editTelefono = findViewById(R.id.editTelefono);
        editDescripcion = findViewById(R.id.editDescripcion);
        textUbicacion = findViewById(R.id.textUbicacion);
        buttonTomarFoto = findViewById(R.id.buttonTomarFoto);
        buttonSeleccionarFoto = findViewById(R.id.buttonSeleccionarFoto);
        buttonUbicacion = findViewById(R.id.buttonUbicacion);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        configurarLaunchers();
        solicitarPermisosSiFaltan();

        buttonTomarFoto.setOnClickListener(v -> abrirCamaraReal());
        buttonSeleccionarFoto.setOnClickListener(v -> abrirGaleria());
        buttonUbicacion.setOnClickListener(v -> abrirMapaParaSeleccionarUbicacion());
        buttonGuardar.setOnClickListener(v -> guardarPerfil());

        cargarPerfil();
    }

    private void configurarLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // La cámara guardó la imagen en photoFile mediante EXTRA_OUTPUT
                        if (photoFile != null) {
                            imageFoto.setImageURI(Uri.fromFile(photoFile));
                            // Preparamos photoUri para subir
                            photoUri = FileProvider.getUriForFile(
                                    this, getPackageName() + ".fileprovider", photoFile);
                            Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No se encontró el archivo de la foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        photoUri = result.getData().getData();
                        imageFoto.setImageURI(photoUri);
                        Toast.makeText(this, "Imagen seleccionada de galería", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        lat = result.getData().getDoubleExtra("lat", 0.0);
                        lng = result.getData().getDoubleExtra("lng", 0.0);
                        textUbicacion.setText("Ubicación: " + lat + ", " + lng);
                        Toast.makeText(this, "Ubicación seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void solicitarPermisosSiFaltan() {
        // Cámara
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        }

        // Galería (Android 13+ usa READ_MEDIA_IMAGES)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQ_GALLERY);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_GALLERY);
            }
        }

        // Ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_MAP);
        }
    }

    private void cargarPerfil() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                if (!s.exists()) return;

                String nombre = s.child("nombre").getValue(String.class);
                String telefono = s.child("telefono").getValue(String.class);
                String descripcion = s.child("descripcion").getValue(String.class);
                Double latDb = s.child("ubicacion").child("lat").getValue(Double.class);
                Double lngDb = s.child("ubicacion").child("lng").getValue(Double.class);
                String fotoUrl = s.child("fotoUrl").getValue(String.class);

                if (nombre != null) editNombre.setText(nombre);
                if (telefono != null) editTelefono.setText(telefono);
                if (descripcion != null) editDescripcion.setText(descripcion);
                if (latDb != null && lngDb != null) {
                    lat = latDb;
                    lng = lngDb;
                    textUbicacion.setText("Ubicación: " + lat + ", " + lng);
                }
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(PerfilUsuarioActivity.this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.ic_person)
                            .into(imageFoto);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PerfilUsuarioActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cámara real con FileProvider
    private void abrirCamaraReal() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No hay app de cámara disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            photoFile = crearArchivoImagen();
        } catch (IOException e) {
            Toast.makeText(this, "No se pudo crear archivo de imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile != null) {
            Uri outputUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            // Guardamos para subir luego
            photoUri = outputUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(intent);
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir(); // /Android/data/<pack>/cache
        File imagesDir = new File(storageDir, "camera");
        if (!imagesDir.exists()) imagesDir.mkdirs();
        return File.createTempFile(imageFileName, ".jpg", imagesDir);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void abrirMapaParaSeleccionarUbicacion() {
        Intent intent = new Intent(this, SeleccionarUbicacionMapActivity.class);
        mapLauncher.launch(intent);
    }

    private void guardarPerfil() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(uid);

        ref.child("nombre").setValue(editNombre.getText().toString());
        ref.child("telefono").setValue(editTelefono.getText().toString());
        ref.child("descripcion").setValue(editDescripcion.getText().toString());
        ref.child("ubicacion").child("lat").setValue(lat);
        ref.child("ubicacion").child("lng").setValue(lng);

        if (photoUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("perfil_fotos/" + uid + ".jpg");
            storageRef.putFile(photoUri)
                    .addOnSuccessListener(task -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                ref.child("fotoUrl").setValue(uri.toString());
                                Toast.makeText(this, "Perfil y foto guardados", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "No se pudo subir la foto", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Perfil guardado", Toast.LENGTH_SHORT).show();
        }
    }
}
