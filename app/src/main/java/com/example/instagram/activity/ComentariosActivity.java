package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Comentario;
import com.example.instagram.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private String idPostagem;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializar componentes
        editComentario = findViewById(R.id.editComentario);

        //Configuraçoes iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentarios");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        //Recupera id da postagem
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            idPostagem = bundle.getString("idPostagem");
        }
    }

    public void salvarComentario(View view){

        String textoComentario = editComentario.getText().toString();
        if (textoComentario != null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNome());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);
            if (comentario.salvar()){
                Toast.makeText(this, "Comentario salco com sucesso", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Insira um comentario antes de salvar", Toast.LENGTH_SHORT).show();
        }

        //Limpa comentario digitado
        editComentario.setText("");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}