// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo.Vaga;

@Dao
public interface VagaDao {

    @Insert
    long insert(Vaga vaga);

    @Delete
    void delete(Vaga vaga);

    @Update
    void update(Vaga vaga);

    // Procurando vaga por id
    @Query("SELECT * FROM vagas WHERE id = :id")
    Vaga queryForId(long id);

    // Retorna todas as vagas
    @Query("SELECT * FROM vagas ORDER BY empresa ASC")
    List<Vaga> queryAll();

    // Para determinar se existe uma vaga relacionada ao cargo
    @Query("SELECT * FROM vagas WHERE cargoId = :id ORDER BY empresa ASC")
    List<Vaga> queryForCargoId(long id);
}