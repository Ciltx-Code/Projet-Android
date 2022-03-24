package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSION = 1;
    FrameLayout layout;
    private Button enregistrer;
    private Button retour;
    private ImageButton deconnexion;

    private Bitmap bitmap;
    byte img[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bitmap=null;
        enregistrer = findViewById(R.id.enregistrer);
        retour = findViewById(R.id.retour);
        deconnexion = findViewById(R.id.logout);
        layout = findViewById(R.id.img);
        enregistrer.setOnClickListener(this);
        retour.setOnClickListener(this);
        deconnexion.setOnClickListener(this);
        layout.setOnClickListener(this);

        LinearLayout linearLayout = findViewById(R.id.pageTotale);
        linearLayout.setOnClickListener(this);


        EditText identifiant = findViewById(R.id.identifiant);
        EditText mdp = findViewById(R.id.password);
        Releveur releveur = Connexion.leReleveurConnecte;
        CircleImageView bg = findViewById(R.id.bg);
        if(releveur.image!=null){
            bg.setImageBitmap(releveur.image);
        }

        identifiant.setText(releveur.nomReleveur);
        mdp.setText(releveur.motDePasse);
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
            case R.id.enregistrer:
                EditText identifiant = findViewById(R.id.identifiant);
                EditText mdp = findViewById(R.id.password);
                Releveur r = new Releveur();
                r.id = Connexion.leReleveurConnecte.id;
                r.nomReleveur = identifiant.getText().toString();
                r.motDePasse = mdp.getText().toString();
                if(bitmap!=null){
                    r.image = bitmap;
                }else{
                    r.image = Connexion.leReleveurConnecte.image;
                }
                CompteurSQLLite compteurSQLLite = CompteurSQLLite.getLeCompteur();
                compteurSQLLite.updateReleveur(r);
                Toast toast = Toast.makeText(this,"Modification réussie !",Toast.LENGTH_SHORT);
                toast.show();
                Connexion.leReleveurConnecte = r;
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;
            case R.id.retour:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.logout:
                Intent intent2 = new Intent(this,Connexion.class);
                startActivity(intent2);
                Connexion.leReleveurConnecte = null;
                ControleurListeCompteur.getLobjet().finish();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                Intent imageSelection = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageSelection,0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {

                bitmap = Register.getCorrectlyOrientedImage(this,selectedImage);
                //bitmap = Bitmap.createScaledBitmap(bitmap,150,150,true);

                ImageView bg = findViewById(R.id.bg);
                bg.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}