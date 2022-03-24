package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import plum.widget.ComboDialog;

public class Connexion extends AppCompatActivity implements ComboDialog.OnClickComboDialogListener, View.OnClickListener {

    public static Releveur leReleveurConnecte;
    public static Releveur releveurFraichementInscrit = null;

    private static boolean isHide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        CompteurSQLLite leCompteur = CompteurSQLLite.getLeCompteur();

        List<Releveur> lesReleveurs = leCompteur.getListeReleveurs();
        CharSequence[] stringReleveurs = new String[lesReleveurs.size()];
        CharSequence[] stringValues = new String[lesReleveurs.size()];
        for(int i=0;i<lesReleveurs.size();i++){
            Releveur releveur = lesReleveurs.get(i);
            stringReleveurs[i] = releveur.nomReleveur;
            stringValues[i] = ""+i;
        }

        TextView identifiant = (TextView)findViewById( R.id.identifiant );

        ComboDialog comboReleveurs = new ComboDialog( "Choisir un identifiant", stringReleveurs, stringValues, identifiant, this );
        comboReleveurs.setOnClickComboDialogListener(this);

        Button connexion = findViewById(R.id.connexion);
        connexion.setOnClickListener(this);

        Button retour = findViewById(R.id.retour);
        retour.setOnClickListener(this);

        LinearLayout linearLayout = findViewById(R.id.pageTotale);
        linearLayout.setOnClickListener(this);

        if(releveurFraichementInscrit!=null){
            identifiant.setText(releveurFraichementInscrit.nomReleveur);
        }
    }


    @Override
    public void onClickComboDialog(ComboDialog comboDialog) {

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
            case R.id.connexion:
                TextView identifiant = findViewById(R.id.identifiant);
                if(identifiant.getText().toString().equals("")){
                    Toast toast = Toast.makeText(this,"Veuillez choisir un identifiant",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                TextView password = findViewById(R.id.password);
                if(password.getText().toString().equals("")){
                    Toast toast = Toast.makeText(this,"Veuillez entrer un mot de passe",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                ArrayList<Releveur> listeReleveurs = CompteurSQLLite.getLeCompteur().getListeReleveurs();
                Releveur releveur = null;
                for (Releveur releveur1:listeReleveurs){
                    if(releveur1.nomReleveur.equals(identifiant.getText().toString())){
                        releveur=releveur1;
                    }
                }
                if(!releveur.motDePasse.equals(password.getText().toString())){
                    Toast toast = Toast.makeText(this,"Mot de passe incorrect",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                leReleveurConnecte = releveur;
                Intent intent = new Intent(this,ControleurListeCompteur.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.retour:
                Intent home = new Intent(this,MainActivity.class);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }


}
