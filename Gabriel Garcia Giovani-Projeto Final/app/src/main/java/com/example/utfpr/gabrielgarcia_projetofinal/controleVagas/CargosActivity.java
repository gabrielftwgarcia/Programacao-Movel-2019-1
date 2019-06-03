// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Vaga;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Cargo;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia.VagasDatabase;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils.UtilsGUI;

public class CargosActivity extends AppCompatActivity {

    private static final int REQUEST_NOVO_CARGO = 1;
    private static final int REQUEST_ALTERAR_CARGO = 2;

    private ListView listViewCargos;
    private ArrayAdapter<Cargo> listaAdapter;
    private List<Cargo> lista;

    // Abrindo cargos
    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, CargosActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewCargos = findViewById(R.id.listViewItens);

        listViewCargos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cargo cargo = (Cargo) parent.getItemAtPosition(position);

                CargoActivity.alterar(CargosActivity.this, REQUEST_ALTERAR_CARGO, cargo);
            }
        });

        carregaCargos();

        registerForContextMenu(listViewCargos);

        setTitle(R.string.cargos);
    }

    private void carregaCargos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                VagasDatabase database = VagasDatabase.getDatabase(CargosActivity.this);

                lista = database.cargoDao().queryAll();

                CargosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(CargosActivity.this, android.R.layout.simple_list_item_1, lista);
                        listViewCargos.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void verificaUsoCargo(final Cargo cargo){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VagasDatabase database = VagasDatabase.getDatabase(CargosActivity.this);

                List<Vaga> lista = database.vagaDao().queryForCargoId(cargo.getId());

                if (lista != null && lista.size() > 0){

                    CargosActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.atencaoErro(CargosActivity.this, R.string.cargo_usado);
                        }
                    });

                    return;
                }

                CargosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excluirCargo(cargo);
                    }
                });
            }
        });
    }

    private void excluirCargo(final Cargo cargo){

        String mensagem = getString(R.string.deseja_excluir) + "\n" + cargo.getDescricao();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        VagasDatabase database = VagasDatabase.getDatabase(CargosActivity.this);

                                        database.cargoDao().delete(cargo);

                                        CargosActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(cargo);
                                            }
                                        });
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_CARGO || requestCode == REQUEST_ALTERAR_CARGO)
             && resultCode == Activity.RESULT_OK){

            carregaCargos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_cargos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                CargoActivity.novo(this, REQUEST_NOVO_CARGO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Cargo cargo = (Cargo) listViewCargos.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                CargoActivity.alterar(this, REQUEST_ALTERAR_CARGO, cargo);
                return true;

            case R.id.menuItemApagar:
                verificaUsoCargo(cargo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
