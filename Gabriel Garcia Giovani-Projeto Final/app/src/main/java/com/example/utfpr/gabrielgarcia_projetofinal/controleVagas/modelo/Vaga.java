// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.modelo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

// Entidade que recebe a chave estrangeira de Cargo
// Coluna origem (id) coluna destino (cargoId)
@Entity(tableName = "vagas", foreignKeys = @ForeignKey(entity = Cargo.class, parentColumns = "id",
                                                                        childColumns  = "cargoId"))

public class Vaga {

    // Semelhante ao que foi feito inicialmente em Cargo
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String empresa;
    private String cidade;
    private int salario;
//    private String empresa;
////    private String cargo;
//    private String cidade;
//    private int salario;

    // Pra essa coluna, tenho um índice gerado para ela para facilitar a consulta
    @ColumnInfo(index = true)
    private int cargoId;

    public Vaga(String empresa){
        setEmpresa(empresa);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getEmpresa() {
        return empresa;
    }
    public String getCidade() {
        return cidade;
    }
    public int getSalario() {
        return salario;
    }

    public void setEmpresa(@NonNull String empresa) {
        this.empresa = empresa;
    }
    public void setCidade(@NonNull String cidade) {
        this.cidade = cidade;
    }
    public void setSalario(int salario) {
        this.salario = salario;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    @Override
    public String toString(){
        return getEmpresa();
    }
}
