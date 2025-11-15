package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class SeleccionarUbicacionMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng seleccion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_ubicacion_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        Button btnConfirmar = findViewById(R.id.buttonConfirmar);
        btnConfirmar.setOnClickListener(v -> {
            if (seleccion != null) {
                Intent result = new Intent();
                result.putExtra("lat", seleccion.latitude);
                result.putExtra("lng", seleccion.longitude);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Centro inicial (puedes cambiar a la última ubicación del usuario)
        LatLng inicial = new LatLng(15.85, -87.94); // Puerto Cortés aprox
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicial, 14f));

        // Tocar largo para seleccionar punto
        mMap.setOnMapLongClickListener(latLng -> {
            seleccion = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
        });
    }
}