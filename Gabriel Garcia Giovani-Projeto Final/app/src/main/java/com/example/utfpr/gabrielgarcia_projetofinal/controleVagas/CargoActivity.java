// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.R;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Cargo;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia.VagasDatabase;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils.UtilsGUI;

public class CargoActivity extends AppCompatActivity {

    public static final String MODO = "MODO";
    public static final String ID = "ID";
    public static final int NOVO = 1;
    public static final int ALTERAR = 2;

    private EditText editTexDescricao;

    private int modo;
    private Cargo cargo;

    // Activity para novo cargo
    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, CargoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    // Activity para alterar cargo
    public static void alterar(Activity activity, int requestCode, Cargo cargo){

        Intent intent = new Intent(activity, CargoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, cargo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    // Semelhante ao que ocorre em Vagas
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTexDescricao = findViewById(R.id.editTextDescricao);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
        }else{
            modo = NOVO;
        }

        if (modo == ALTERAR){

            setTitle(R.string.alterar_cargo);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    int id = bundle.getInt(ID);

                    VagasDatabase database = VagasDatabase.getDatabase(CargoActivity.this);

                    cargo = database.cargoDao().queryForId(id);

                    CargoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTexDescricao.setText(cargo.getDescricao());
                        }
                    });
                }
            });

        }else{
            setTitle(R.string.novo_cargo);
            cargo = new Cargo("");
        }
    }

    private void salvar(){
        final String descricao  = UtilsGUI.validaCampoTexto(this, editTexDescricao, R.string.descricao_vazia);
        if (descricao == null){
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                VagasDatabase database = VagasDatabase.getDatabase(CargoActivity.this);

                List<Cargo> lista = database.cargoDao().queryForDescricao(descricao);

                if (modo == NOVO) {

                    if (lista.size() > 0){

                        CargoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilsGUI.atencaoErro(CargoActivity.this, R.string.descricao_usada);
                            }
                        });

                        return;
                    }

                    cargo.setDescricao(descricao);
                    database.cargoDao().insert(cargo);

                } else {
                    if (!descricao.equals(cargo.getDescricao())){

                        if (lista.size() >= 1){

                            CargoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsGUI.atencaoErro(CargoActivity.this, R.string.descricao_usada);
                                }
                            });

                            return;
                        }
                        cargo.setDescricao(descricao);
                        database.cargoDao().update(cargo);
                    }
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
