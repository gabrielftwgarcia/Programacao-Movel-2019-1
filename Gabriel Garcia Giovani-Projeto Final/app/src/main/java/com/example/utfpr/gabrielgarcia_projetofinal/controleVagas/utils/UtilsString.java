// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils;

// Classe usada apenas para tratar strings vazias
public class UtilsString {
    public static boolean stringVazia(String texto){
        return texto == null || texto.trim().length() == 0;
    }
}
