package fr.gds.relevecompteur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CompteurAdapter extends ArrayAdapter {

    public CompteurAdapter(@NonNull Context context, ArrayList<Compteur> compteurs) {
        super(context,0, compteurs);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Compteur compteur = (Compteur) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_liste_item_compteur, parent, false);
        }

        // Lookup view for data population
        TextView txtNom = (TextView) convertView.findViewById(R.id.nom);
        TextView txtRue = (TextView) convertView.findViewById(R.id.rue);
        TextView txtVille = (TextView) convertView.findViewById(R.id.ville);
        TextView indexAncien = (TextView) convertView.findViewById(R.id.indexAncien);
        TextView indexNouveau = (TextView) convertView.findViewById(R.id.indexNouveau);
        TextView codePostal = (TextView) convertView.findViewById(R.id.codepostal);
        TextView nomReleveur = (TextView) convertView.findViewById(R.id.nomReleveur);

        // Populate the data into the template view using the data object
        txtNom.setText(compteur.nom);
        txtRue.setText(compteur.rue);
        txtVille.setText(compteur.ville);
        indexAncien.setText("Ancien : "+compteur.indexAncien);
        indexNouveau.setText("Nouveau : "+compteur.indexNouveau);
        codePostal.setText(""+compteur.codePostal);
        nomReleveur.setText("Relevé par : "+compteur.nomReleveur);


        if(indexNouveau.getText().equals("Nouveau : 0")){
            ImageView img = convertView.findViewById(R.id.validate);
            img.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_outline_pending_24, null));
        }else{
            ImageView img = convertView.findViewById(R.id.validate);
            img.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24, null));
        }




        // Return the completed view to render on screen


        return convertView;
    }
}
