// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

// Neste caso, a tabela Cargo fornece a chave para a Vaga
@Entity(tableName = "cargos", indices = @Index(value = {"descricao"}, unique = true))
public class Cargo{

    // Chave primária generada automaticamente
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Campo obrigatório
    @NonNull
    private String descricao;

    public Cargo(String descricao){
        setDescricao(descricao);
    }

    // Fazendo gets e sets já que os atributos são privados
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Descrição não pode ser nula
    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString(){
        return getDescricao();
    }
}
