package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import plum.widget.MessageDialog;

public class Warning extends AppCompatActivity implements View.OnClickListener, MessageDialog.OnClickMessageDialogListener {
    public static Compteur leCompteurEnQuestion;
    ImageView imageView;
    Bitmap bitmap = null;
    byte img[];
    Bitmap bmp = null;

    private static final int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        Button valider = findViewById(R.id.valider);
        Button annuler = findViewById(R.id.annuler);

        valider.setOnClickListener(this);
        annuler.setOnClickListener(this);

        ImageView image = findViewById(R.id.img);
        image.setOnClickListener(this);

        EditText texte = findViewById(R.id.probleme);

        byte[] img = CompteurSQLLite.getLeCompteur().getLImageProbleme(leCompteurEnQuestion);
        String probleme = CompteurSQLLite.getLeCompteur().getLeProbleme(leCompteurEnQuestion);

        if(img !=null){
            image.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));
        }
        if(!probleme.equals("")){
            texte.setText(probleme);
        }
        LinearLayout linearLayout = findViewById(R.id.pageTotale);
        linearLayout.setOnClickListener(this);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pageTotale:
                hideKeyboard(v);
                break;
            case R.id.valider:
                CompteurSQLLite compteurSQLLite = CompteurSQLLite.getLeCompteur();
                EditText leTexte = findViewById(R.id.probleme);
                ImageView image = findViewById(R.id.img);
                byte[] byteArray = null;
                if(bmp != null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                }
                compteurSQLLite.setProbleme(leCompteurEnQuestion, leTexte.getText().toString(), byteArray);
                finish();
                break;

            case R.id.annuler:
                EditText leTexte1 = findViewById(R.id.probleme);
                if(bmp!=null || !leTexte1.getText().toString().equals(CompteurSQLLite.getLeCompteur().getLeProbleme(leCompteurEnQuestion))){
                    MessageDialog.show(this, "Voulez vous vraiment annuler ?", "OUI","NON", this);
                    return;
                }
                finish();
                break;
            case R.id.img:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

                    List<String> permissions = new ArrayList<String>();
                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                    if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }

                    if (!permissions.isEmpty()) {
                        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                                REQUEST_PERMISSION);
                    }
                }
                Intent imageSelection = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(imageSelection,0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            System.out.println(data);
            Bundle selectedImage = data.getExtras();
            bitmap = (Bitmap) selectedImage.get("data");
            try {
                //bitmap = Bitmap.createScaledBitmap(bitmap,150,150,true);

                ImageView bg = findViewById(R.id.img);
                bg.setImageBitmap(bitmap);
                bmp = bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onClickMessageDialog(MessageDialog messageDialog, char c) {
        switch (c) {
            case 'G':
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case 'D':
                bmp = null;
                break;
        }
    }
}