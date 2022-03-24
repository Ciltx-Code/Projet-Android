package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plum.widget.MessageDialog;

public class ControleurFicheCompteur extends AppCompatActivity implements View.OnClickListener, MessageDialog.OnClickMessageDialogListener{
    private Compteur compteur=null;
    private EditText nouvelIndex;
    private boolean test = true;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if (!nouvelIndex.getText().toString().equals("")){
                MessageDialog.show(this, "Voulez vous vraiment annuler ?", "OUI","NON", this);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fiche_compteur);

        int idCompteur=getIntent().getIntExtra("compteurId", -1);

        ArrayList<Compteur> compteurs = CompteurSQLLite.getLeCompteur().getListeCompteur();
        for(Compteur compteur1:compteurs){
            if(compteur1.id == idCompteur){
                compteur = compteur1;
            }
        }
        TextView nom = findViewById(R.id.nomCompteur);
        nom.setText(compteur.nom);
        TextView ville = findViewById(R.id.ville);
        ville.setText(compteur.ville);
        TextView codepostal = findViewById(R.id.codepostal);
        codepostal.setText(compteur.codePostal);
        TextView rue = findViewById(R.id.rue);
        rue.setText(compteur.rue);
        TextView ancienIndex = findViewById(R.id.indexAncien);
        ancienIndex.setText(""+compteur.indexAncien);
        nouvelIndex = findViewById(R.id.indexNouveau);
        if(nouvelIndex.getText().toString().equals("0")){
            nouvelIndex.setText(""+compteur.indexNouveau);
        }

        ImageView warning = findViewById(R.id.warning);
        warning.setOnClickListener(this);

        Button valider = findViewById(R.id.valider);
        valider.setOnClickListener(this);

        Button annuler = findViewById(R.id.annuler);
        annuler.setOnClickListener(this);

        LinearLayout linearLayout = findViewById(R.id.pageTotale);
        linearLayout.setOnClickListener(this);

        LinearLayout call = findViewById(R.id.call);
        call.setOnClickListener(this);

        TextView telNum = findViewById(R.id.telNum);
        telNum.setText(compteur.numTelephone);

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,ControleurListeCompteur.class);
        EditText nouvelIndex = findViewById(R.id.indexNouveau);
        TextView ancienIndex = findViewById(R.id.indexAncien);
        switch (v.getId()){
            case R.id.warning:
                Warning.leCompteurEnQuestion = compteur;
                Intent intent2 = new Intent(this,Warning.class);
                startActivity(intent2);
                break;
            case R.id.pageTotale:
                hideKeyboard(v);
                break;
            case R.id.call:
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + compteur.numTelephone));
                startActivity(intent1);

                break;
            case R.id.valider:
                if(nouvelIndex.getText().toString().equals("0")||nouvelIndex.getText().toString().equals("")){
                    Toast.makeText(this,"Veuillez entrer une valeur",Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern p = Pattern.compile("[0-9]+");
                Matcher matcher = p.matcher(nouvelIndex.getText().toString());
                matcher.find();
                if(!matcher.matches()){
                    Toast.makeText(this,"Veuillez entrer une valeur valide",Toast.LENGTH_SHORT).show();
                    return;
                }

                int valeur = Integer.parseInt(nouvelIndex.getText().toString());

                int ancienReleve = Integer.parseInt(ancienIndex.getText().toString());
                int nouveauReleve = Integer.parseInt(nouvelIndex.getText().toString());


                if(nouveauReleve<ancienReleve){
                    Toast.makeText(this,"Veuillez entrer une valeur supérieure à la précédente",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(nouveauReleve>ancienReleve+800){
                    Toast.makeText(this,"Attention, le nouveau relevé est supérieur à l'ancien relevé de plus de 800",Toast.LENGTH_LONG).show();
                }
                compteur.indexNouveau = valeur;
                compteur.nomReleveur = Connexion.leReleveurConnecte.nomReleveur;

                CompteurSQLLite.getLeCompteur().setComteur(compteur);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.annuler:

                if(!nouvelIndex.getText().toString().equals("")){
                    MessageDialog.show(this, "Voulez vous vraiment annuler ?", "OUI","NON", this);
                    return;
                }
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
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

                break;
        }
    }
}