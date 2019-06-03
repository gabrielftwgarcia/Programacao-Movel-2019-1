// Aluno: Gabriel Garcia Giovani    R.A: 1770748

package com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.example.utfpr.gabrielgarcia_projetofinal.controleVagas.R;

public class UtilsGUI {

    public static void atencaoErro(Context contexto, int idTexto){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setTitle(R.string.atencao);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(idTexto);

        builder.setNeutralButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // JOptionPane que exibe mensagem de confirmação
    public static void confirmaAcao(Context contexto, String mensagem, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setTitle(R.string.confirmacao);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.confirmar, listener);
        builder.setNegativeButton(R.string.cancel, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String validaCampoTexto(Context contexto, EditText editText, int idMensagemErro){
        String texto = editText.getText().toString();

        if (UtilsString.stringVazia(texto)){
            UtilsGUI.atencaoErro(contexto, idMensagemErro);
            editText.setText(null);
            editText.requestFocus();
            return null;
        }else{
            // Desconsiderando espaços em branco
            return texto.trim();
        }
    }
}
