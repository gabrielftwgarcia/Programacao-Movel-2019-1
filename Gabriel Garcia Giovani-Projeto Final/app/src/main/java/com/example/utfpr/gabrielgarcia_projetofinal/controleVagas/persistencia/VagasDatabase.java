// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;


import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.R;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Vaga;
import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Cargo;

// Criando o database em si, referenciando a versão para não dar erro (pasta onde é exportada o esquema)
@Database(entities = {Vaga.class, Cargo.class}, version = 1)
public abstract class VagasDatabase extends RoomDatabase {

    // Métodos para manipular vaga e cargo
    public abstract VagaDao vagaDao();

    public abstract CargoDao cargoDao();

    private static VagasDatabase instance;

    public static VagasDatabase getDatabase(final Context context) {

        // Fazendo o tratamento de Thread da maneira esperada
        if (instance == null) {

            synchronized (VagasDatabase.class) {
                if (instance == null) {
                   RoomDatabase.Builder builder =  Room.databaseBuilder(context, VagasDatabase.class,"vagas.db");

                   // Caso o usuário queira fazer coisas além do que é feito inicialmente pela primeira vez
                   builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaCargosIniciais(context);
                                }
                            });
                        }
                   });

                   // Efetivando a operação e fazendo o create dos cargos iniciais
                   instance = (VagasDatabase) builder.build();
                }
            }
        }

        return instance;
    }

    private static void carregaCargosIniciais(final Context context){

        // Inserindo cargos no objeto string e inserindo inicialmente
        String[] descricoes = context.getResources().getStringArray(R.array.cargos_iniciais);

        for (String descricao : descricoes) {

            Cargo cargo = new Cargo(descricao);

            instance.cargoDao().insert(cargo);
        }
    }
}
