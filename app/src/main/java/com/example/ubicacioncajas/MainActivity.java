package com.example.ubicacioncajas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etBulto, etModulo, etColumna, etFila;
    Button btnIngresar, btnBuscar, btnModificar, btnEliminar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBulto = findViewById(R.id.etBulto);
        etColumna= findViewById(R.id.etColumna);
        etModulo= findViewById(R.id.etModulo);
        etFila= findViewById(R.id.etFila);
        btnIngresar= findViewById(R.id.btnIngresar);
        btnBuscar=findViewById(R.id.btnBuscar);
        btnModificar=findViewById(R.id.btnModificar);
        btnEliminar=findViewById(R.id.btnEliminar);

        //MUCHA ANTENCION, EN EL MANIFES TUVE QUE AGREGAR ""android:usesCleartextTraffic="true">"" PERO AL FINAL
        //DE APLAICATION.  SIN ESTO NO FUNCIONA LA CONEXION A PHP.
        //ESTO SE USA PARTICULARMENTE PARA SERVIDORES QUE NO USAN CIFRADO HTTPS

        btnIngresar.setOnClickListener(v -> ejecutarServicio("http://192.168.100.29/AndroidCajas/insertarCajas.php"));
        btnBuscar.setOnClickListener(v -> buscarCajas("http://192.168.100.29/AndroidCajas/mostrarCajas.php?idcaja="+etBulto.getText()+""));
        btnModificar.setOnClickListener(v -> ejecutarServicio("http://192.168.100.29/AndroidCajas/modificarCajas.php"));
        btnEliminar.setOnClickListener(v -> eliminaCajas("http://192.168.100.29/AndroidCajas/eliminarCajas.php"));
    }

    private void ejecutarServicio(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> Toast.makeText(getApplicationContext(), "bien", Toast.LENGTH_LONG).show(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("idcaja",etBulto.getText().toString());
                parametros.put("modulo",etModulo.getText().toString());
                parametros.put("columna",etColumna.getText().toString());
                parametros.put("fila",etFila.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void buscarCajas(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, response -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    etModulo.setText(jsonObject.getString("modulo"));
                    etColumna.setText(jsonObject.getString("columna"));
                    etFila.setText(jsonObject.getString("fila"));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> Toast.makeText(getApplicationContext(), "Error con la conexion", Toast.LENGTH_SHORT).show());
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void limpiar(){
        etBulto.setText("");
        etModulo.setText("");
        etFila.setText("");
        etColumna.setText("");
    }

    private void eliminaCajas(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            Toast.makeText(getApplicationContext(), "Caja Fue Eliminada", Toast.LENGTH_LONG).show();
            limpiar();
        }, error -> Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show()){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("idcaja",etBulto.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}