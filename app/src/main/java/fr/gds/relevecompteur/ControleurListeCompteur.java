package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ControleurListeCompteur extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private ArrayList<Compteur> compteurs;
    private CompteurAdapter compteurAdapter;
    private Compteur compteur;
    private static ControleurListeCompteur objet;

    public static ControleurListeCompteur getLobjet(){
        return objet;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Spinner leSpinner = findViewById(R.id.spinner);
        updateListe(leSpinner);
        Releveur releveur = Connexion.leReleveurConnecte;
        TextView bienvenue = findViewById(R.id.bienvenue);
        bienvenue.setText("Bienvenue\n"+releveur.nomReleveur);

        CircleImageView logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        if(releveur.image!=null){
            logout.setImageBitmap(releveur.image);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_liste_compteur);
        Releveur releveur = Connexion.leReleveurConnecte;
        TextView bienvenue = findViewById(R.id.bienvenue);
        bienvenue.setText("Bienvenue\n"+releveur.nomReleveur);

        CircleImageView logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        if(releveur.image!=null){
            logout.setImageBitmap(releveur.image);
        }

        compteurs = CompteurSQLLite.getLeCompteur().getListeCompteur();
        compteurAdapter = new CompteurAdapter(this,compteurs);
        ListView lv = findViewById(R.id.listeCompteurs);
        lv.setAdapter(compteurAdapter);
        lv.setOnItemClickListener(this);

        Spinner leSpinner = findViewById(R.id.spinner);
        leSpinner.setOnItemSelectedListener(this);
        objet=this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                Intent intent = new Intent(this,Profile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        compteur = compteurs.get(position);
        Intent intent = new Intent(this,ControleurFicheCompteur.class);
        intent.putExtra("compteurId",compteur.id);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner leSpinner = findViewById(R.id.spinner);
        updateListe(leSpinner);
    }

    public void updateListe(Spinner leSpinner){
        switch (leSpinner.getItemAtPosition(leSpinner.getSelectedItemPosition()).toString()){
            case "Tous les relevés":
                compteurs = CompteurSQLLite.getLeCompteur().getListeCompteur();
                break;
            case "Relevés faits":
                compteurs = CompteurSQLLite.getLeCompteur().getListeCompteurWhereReleveFait();
                break;
            case "Relevés à faire":
                compteurs = CompteurSQLLite.getLeCompteur().getListeCompteurWhereRelevePasFait();
                break;
        }

        compteurAdapter.clear();
        for(Compteur c:compteurs){
            compteurAdapter.add(c);
        }
        compteurAdapter.notifyDataSetChanged();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}