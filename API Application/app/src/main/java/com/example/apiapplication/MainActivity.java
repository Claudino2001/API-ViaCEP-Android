/*
 Consulta CEP
 @autor: Gabriel Claudino
 @date: 04/05/2023
 */

package com.example.apiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {
    public TextView textView;
    public EditText textCEP;
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        textCEP = findViewById(R.id.textCEP);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTask task = new MyTask();
                //String urlApi = "https://blockchain.info/ticker";
                String cep = textCEP.getText().toString();
                if(!CEP_Valido(cep)){
                    textCEP.setError("CEP inválido");
                    //Toast.makeText(MainActivity.this, "CEP inválido", Toast.LENGTH_SHORT).show();
                }else{
                    cep = textCEP.getText().toString();
                    String urlApi = "https://viacep.com.br/ws/"+ cep +"/json/";
                    task.execute(urlApi);
                    textCEP.setText("");
                }
            }
        });
    }

    boolean CEP_Valido(String cep){
        int erro = 0;
        boolean CEPValido = false;

        for(int i = 0; i < cep.length(); i++){
            if((Character.isLetter(cep.charAt(i))) || (Character.isSpaceChar(cep.charAt(i)))){
                erro = erro + 1;
            }
                System.out.println(erro);
        }

        System.out.println(cep.isEmpty() + " " + cep.length() + " " + erro);

        if((!cep.isEmpty()) && (cep.length() == 8) && (erro == 0)){

            CEPValido = true;
        }

        return CEPValido;
    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...Strings){

            String stringUrl = Strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            StringBuffer buffer= null;

            try{
                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                inputStream = conexao.getInputStream();

                inputStreamReader = new InputStreamReader(inputStream);

                reader = new BufferedReader(inputStreamReader);

                buffer = new StringBuffer();

                String linha = "";

                while((linha=reader.readLine()) != null){
                    buffer.append(linha);
                }

            }catch (MalformedURLException e){
                throw new RuntimeException(e);
            }catch (IOException e){
                throw new RuntimeException(e);
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String resultado){
            super.onPostExecute(resultado);

            String cep = null;
            String logradouro = null;
            String complemento = null;
            String bairro = null;
            String localidade = null;
            String uf = null;
            String ibge = null;
            String gia = null;
            String ddd = null;
            String siafi = null;

            try{
                JSONObject jsonObject = new JSONObject((resultado));
                cep = jsonObject.getString("cep");
                logradouro = jsonObject.getString("logradouro");
                complemento = jsonObject.getString("complemento");
                bairro = jsonObject.getString("bairro");
                uf = jsonObject.getString("uf");
                ibge = jsonObject.getString("ibge");
                gia = jsonObject.getString("gia");
                ddd = jsonObject.getString("ddd");
                siafi = jsonObject.getString("siafi");
                textView.setText("CEP: " + cep + "\n" +
                                "Logradouro: " + logradouro + "\n" +
                                "Complemento: " + complemento + "\n" +
                                "Bairro: " + bairro +"\n" +
                                "UF: " + uf +"\n" +
                                "IBGE: " + ibge + "\n" +
                                "GIA: " + gia + "\n" +
                                "DDD: " + ddd + "\n" +
                                "SIAFI: " + siafi + "\n");

            }catch (JSONException e){
                System.out.println(""+ new RuntimeException());
                Toast.makeText(MainActivity.this, "CEP inexistente", Toast.LENGTH_SHORT).show();
                textView.setText("");
                //throw new RuntimeException();
            }
        }
    }
}