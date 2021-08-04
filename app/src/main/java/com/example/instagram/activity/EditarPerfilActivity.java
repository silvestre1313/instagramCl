package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.helper.UsuarioFirebase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        //Inicializar componentes
        inicializarComponentes();

        //Recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

    }

    public void inicializarComponentes(){

        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);

    }

}