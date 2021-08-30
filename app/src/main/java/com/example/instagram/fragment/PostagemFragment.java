package com.example.instagram.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagram.R;
import com.example.instagram.activity.FiltroActivity;
import com.example.instagram.helper.Permissao;

import java.io.ByteArrayOutputStream;

public class PostagemFragment extends Fragment {

    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static int requestCode;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    ActivityResultLauncher<Intent> activityResultLauncher;

    public PostagemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //Validar permissoes
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        //Inicializar componentes
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);

        //Adiciona evento de clique no botao da camera
        buttonAbrirCamera.setOnClickListener(v->{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getActivity().getPackageManager()) != null){
                activityResultLauncher.launch(i);
                requestCode = SELECAO_CAMERA;
            }
        });

        //Adiciona evento de clique no botao da galeria
        buttonAbrirGaleria.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getActivity().getPackageManager()) != null){
                activityResultLauncher.launch(i);
                requestCode = SELECAO_GALERIA;
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                         if (result.getResultCode() == Activity.RESULT_OK){

                             Bitmap imagem = null;

                             try{
                                //Valida tipo de seleção de imagem
                                 switch (requestCode){
                                     case SELECAO_CAMERA:
                                         imagem = (Bitmap) result.getData().getExtras().get("data");
                                         break;
                                     case SELECAO_GALERIA:
                                         Uri localImagemSelecionada = result.getData().getData();
                                         imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                                         break;
                                 }

                                 //Valida imagem selecionada
                                 if (imagem != null){

                                     //Converte imagem em byte array
                                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                     imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                     byte[] dadosImagem = baos.toByteArray();

                                     //Envia imagem escolhida para alicação de filtro
                                     Intent i = new Intent(getActivity(), FiltroActivity.class);
                                     i.putExtra("fotoEscolhida", dadosImagem);
                                     startActivity(i);

                                 }


                             }catch (Exception e){
                                 e.printStackTrace();
                             }
                         }
                    }
                });

        return view;
    }

}