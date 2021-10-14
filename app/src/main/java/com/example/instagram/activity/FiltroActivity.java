package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adapter.AdaptarMiniaturas;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private TextInputEditText textDescricaoFiltro;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private ProgressBar progressBar;
    private boolean estaCarregando;

    private RecyclerView recyclerFiltros;
    private AdaptarMiniaturas adaptarMiniaturas;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //Configuraçoes iniciais
        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        //Inicializar componenetes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        progressBar = findViewById(R.id.progressFiltro);

        //Recuperar dados do usuario logado
        recuperarDadosUsuarioLogado();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        //Recupera a imagem escolhida pelo usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0,  dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            // Configura recyclerView de filtros
            adaptarMiniaturas = new AdaptarMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adaptarMiniaturas);

            //Adicionar evento de clique no recyclerView
            recyclerFiltros.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFiltros,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    ThumbnailItem item = listaFiltros.get(position);

                                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                                    Filter filtro = item.filter;
                                    imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));

                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            //Recuperar filtros
            recuperarFiltros();

        }

    }

    private void carregando(boolean estado){

        if (estado){
            estaCarregando = true;
            progressBar.setVisibility(View.VISIBLE);
        } else{
            estaCarregando = false;
            progressBar.setVisibility(View.GONE);
        }

    }

    private void recuperarDadosUsuarioLogado(){

        carregando(true);

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Recupera dados de usuario logado
                        usuarioLogado = snapshot.getValue(Usuario.class);
                        carregando(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void recuperarFiltros(){

        //limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "normal";
        ThumbnailsManager.addThumb(item);

        //Listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro: filtros){
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adaptarMiniaturas.notifyDataSetChanged();

    }

    private void publicarPostagem(){

        if (estaCarregando){
            Toast.makeText(getApplicationContext(), "Carregando dados, aguarde", Toast.LENGTH_SHORT).show();
        } else{

            Postagem postagem = new Postagem();
            postagem.setIdUsuario(idUsuarioLogado);
            postagem.setDescricao(textDescricaoFiltro.getText().toString());

            //Recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            //Salvar imagem no firebase storage
            StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
            StorageReference imagemRef = storageRef
                    .child("imagens")
                    .child("postagens")
                    .child(postagem.getId() + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(e -> Toast.makeText(FiltroActivity.this, "Erro ao salvar imagem, tente novamente", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(v->{
                        //Recuperar local da foto
                        imagemRef.getDownloadUrl().addOnCompleteListener(task -> {
                            Uri url = task.getResult();
                            postagem.setCaminhoFoto(url.toString());

                            //Salvar postagem
                            if (postagem.salvar()){

                                //Atualizar quantidade de postagens
                                int qtdPostagem = usuarioLogado.getPostagens() + 1;
                                usuarioLogado.setPostagens(qtdPostagem);
                                usuarioLogado.atualizarQtdPostagem();

                                Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        });

                        Toast.makeText(FiltroActivity.this, "Sucesso ao fazer postagem", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}