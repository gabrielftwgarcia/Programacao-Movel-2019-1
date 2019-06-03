// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Cargo;

// Anotação que representa um DAO
@Dao
public interface CargoDao {

    @Insert
    long insert(Cargo cargo);

    @Delete
    void delete(Cargo cargo);

    @Update
    void update(Cargo cargo);

    // Usando o mesmo nome escolhido no modelo
    // Retorna só um cargo
    @Query("SELECT * FROM cargos WHERE id = :id")
    Cargo queryForId(long id);

    // Retorna todos os cargos
    @Query("SELECT * FROM cargos ORDER BY descricao ASC")
    List<Cargo> queryAll();

    // Para saber se o cargo já está sendo utilizado
    @Query("SELECT * FROM cargos WHERE descricao = :descricao ORDER BY descricao ASC")
    List<Cargo> queryForDescricao(String descricao);

    // Contanto quantos cargos tem no banco (se não tiver nenhum cargo não tem porque tentar cadas-
    // trar uma vaga
    @Query("SELECT count(*) FROM cargos")
    int total();
}
