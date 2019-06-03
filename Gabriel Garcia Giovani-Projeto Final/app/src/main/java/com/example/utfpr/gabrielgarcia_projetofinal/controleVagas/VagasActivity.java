// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.R;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Vaga;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia.VagasDatabase;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils.InfoActivity;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils.UtilsGUI;

public class VagasActivity extends AppCompatActivity {

    private static final String ARQUIVO ="br.edu.utfpr.gabrielgarcia.sharedpreferences.PREFERENCIAS_CORES";
    private int opcao = Color.WHITE;
    private static final String COR = "COR";
    private ConstraintLayout layout;

    private static final int REQUEST_NOVA_VAGA    = 1;
    private static final int REQUEST_ALTERAR_VAGA = 2;

    private ListView             listViewVaga;
    private ArrayAdapter<Vaga> listaAdapter;
    private List<Vaga>         lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        layout = findViewById(R.id.layoutPrincipal);

        listViewVaga = findViewById(R.id.listViewItens);

        listViewVaga.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Vaga vaga = (Vaga) parent.getItemAtPosition(position);

                VagaActivity.alterar(VagasActivity.this,
                                       REQUEST_ALTERAR_VAGA,
                                       vaga);
            }
        });

        // Carregando valores estabelecidos graças ao uso do sharedpreferences
        lerCor();

        carregaVagas();

        registerForContextMenu(listViewVaga);
    }

    // Recuperando valores em memória referentes à cor de background
    private void lerCor(){

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        opcao = shared.getInt(COR, opcao);

        layout.setBackgroundColor(opcao);

    }
    private void salvarPreferenciaCor(int novoValor){

        SharedPreferences shared = getSharedPreferences(ARQUIVO,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(COR, novoValor);

        editor.commit();

        opcao = novoValor;

        layout.setBackgroundColor(opcao);

    }
    // Implementando funcionalidade dos itens do menu
    // Carregando o menu de acordo com seu último uso
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item;

        switch(opcao){

            case Color.YELLOW:
                item = menu.findItem(R.id.menuItemAmarelo);
                break;

            case Color.WHITE:
                item = menu.findItem(R.id.menuItemBranco);
                break;

            case Color.RED:
                item = menu.findItem(R.id.menuItemVermelho);
                break;

            default:
                return false;
        }

        item.setChecked(true);
        return true;
    }

    private void carregaVagas(){

        // Carregando dados com o uso da thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VagasDatabase database = VagasDatabase.getDatabase(VagasActivity.this);

                // Armazenando os dados passados pela consulta
                lista = database.vagaDao().queryAll();

                VagasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(VagasActivity.this,
                                                          android.R.layout.simple_list_item_1,
                                                          lista);

                        listViewVaga.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void excluirVaga(final Vaga vaga){

        String mensagem = getString(R.string.deseja_excluir) +" " + vaga.getEmpresa() + "?";

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        VagasDatabase database =
                                                VagasDatabase.getDatabase(VagasActivity.this);

                                        database.vagaDao().delete(vaga);

                                        VagasActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(vaga);
                                            }
                                        });
                                    }
                                });
                                break;

                            // Caso recuse a exclusão
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    // Quando a activity Vagas for chamada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_NOVA_VAGA || requestCode == REQUEST_ALTERAR_VAGA)
             && resultCode == Activity.RESULT_OK){

            carregaVagas();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_pessoas, menu);

        // Habilitando exibição dos ícones do menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
//            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    private void verificaCargos(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VagasDatabase database = VagasDatabase.getDatabase(VagasActivity.this);

                int total = database.cargoDao().total();

                if (total == 0){

                    VagasActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.atencaoErro(VagasActivity.this, R.string.nenhum_cargo);
                        }
                    });

                    return;
                }

                VagaActivity.nova(VagasActivity.this, REQUEST_NOVA_VAGA);
            }
        });
    }

    // Activity relacionada às informações sobre a aplicação
    public void abrirActivity2(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Mantendo o item selecionado feito no ultimo uso da aplicação
        item.setChecked(true);

            if(item.getItemId() == R.id.menuItemNovo){
                verificaCargos();
                return true;
            }

            else if(item.getItemId() == R.id.menuItemCargos){
                CargosActivity.abrir(this);
                return true;
            }

            else if(item.getItemId() == R.id.menuItemInfo) {
                abrirActivity2();
                return true;
            }
            else if(item.getItemId() == R.id.menuItemBranco){
                salvarPreferenciaCor(Color.WHITE);
                return true;
            }
            else if(item.getItemId() == R.id.menuItemAmarelo){
                salvarPreferenciaCor(Color.YELLOW);
                return true;
            }
            else if(item.getItemId() == R.id.menuItemVermelho){
                salvarPreferenciaCor(Color.RED);
                return true;
            }
            else{
                return super.onOptionsItemSelected(item);
            }

    }

    // Menu criado ao pressionar item por certo tempo
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    // Implementando funcionalidades do menu de contexto
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Vaga vaga = (Vaga) listViewVaga.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                VagaActivity.alterar(this, REQUEST_ALTERAR_VAGA, vaga);
                return true;

            case R.id.menuItemApagar:
                excluirVaga(vaga);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
