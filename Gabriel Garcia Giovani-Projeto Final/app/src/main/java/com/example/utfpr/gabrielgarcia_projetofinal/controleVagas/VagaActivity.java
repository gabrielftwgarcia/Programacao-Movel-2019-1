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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Vaga;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Cargo;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia.VagasDatabase;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils.UtilsGUI;

public class VagaActivity extends AppCompatActivity {

    // Para simplificar o entendimento
    public static final String MODO = "MODO";
    public static final String ID = "ID";
    public static final int NOVO = 1;
    public static final int ALTERAR = 2;

    private EditText editTextEmpresa;
    private EditText editTextCidade;
    private EditText editTextSalario;

    private Spinner spinnerCargo;
    private List<Cargo> listaCargos;

    private int modo;
    private Vaga vaga;

    // Activity referente à nova vaga
    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, VagaActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    // Activity relacionada à alteração de vaga
    public static void alterar(Activity activity, int requestCode, Vaga vaga){

        Intent intent = new Intent(activity, VagaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, vaga.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaga);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextEmpresa = findViewById(R.id.editTextEmpresa);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextSalario = findViewById(R.id.editTextSalario);

        spinnerCargo = findViewById(R.id.spinnerCargo);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, NOVO);

        // Carregando cargos presentes no banco
        carregaCargos();

        // Se o usuário estiver alterando
        if (modo == ALTERAR){

            setTitle(R.string.alterar_vaga);

            // De forma análoga ao que foi feito em VagasDatabase.java
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    VagasDatabase database = VagasDatabase.getDatabase(VagaActivity.this);

                    vaga = database.vagaDao().queryForId(id);

                    VagaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextEmpresa.setText(vaga.getEmpresa());
                            editTextCidade.setText(vaga.getCidade());
                            editTextSalario.setText(String.valueOf(vaga.getSalario()));

                            int posicao = posicaoCargo(vaga.getCargoId());
                            spinnerCargo.setSelection(posicao);
                        }
                    });
                }
            });

        }
        // Se estiver adicionando
        else{
            setTitle(R.string.nova_vaga);
            vaga = new Vaga("");
        }
    }

    // Retornando a posição do cargo
    private int posicaoCargo(int cargoId){
        for (int pos = 0; pos < listaCargos.size(); pos++){
            Cargo c = listaCargos.get(pos);

            if (c.getId() == cargoId){
                return pos;
            }
        }
        return -1;
    }

    private void carregaCargos(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VagasDatabase database = VagasDatabase.getDatabase(VagaActivity.this);

                listaCargos = database.cargoDao().queryAll();

                VagaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Cargo> spinnerAdapter =
                                new ArrayAdapter<>(VagaActivity.this,
                                                   android.R.layout.simple_list_item_1,
                                                   listaCargos);

                        spinnerCargo.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    // Salvando a vaga em si
    private void salvar(){

        String empresa  = UtilsGUI.validaCampoTexto(this, editTextEmpresa, R.string.empresa_vazia);
        String cidade  = UtilsGUI.validaCampoTexto(this, editTextCidade, R.string.cidade_vazia);

        if (empresa == null || cidade == null){
            return;
        }

        String txtSalario  = UtilsGUI.validaCampoTexto(this, editTextSalario, R.string.salario_vazia);

        if (txtSalario == null){
            return;
        }

        int salario = Integer.parseInt(txtSalario);

        vaga.setEmpresa(empresa);
        vaga.setCidade(cidade);
        vaga.setSalario(salario);

        Cargo cargo = (Cargo) spinnerCargo.getSelectedItem();
        if (cargo != null){
            vaga.setCargoId(cargo.getId());
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VagasDatabase database = VagasDatabase.getDatabase(VagaActivity.this);

                if (modo == NOVO) {
                    database.vagaDao().insert(vaga);
//                    Toast.makeText(this, getString(R.string.remocaoSucesso) , Toast.LENGTH_LONG).show();
                } else {

                    database.vagaDao().update(vaga);
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
