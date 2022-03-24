package fr.gds.relevecompteur;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompteurSQLLite extends SQLiteOpenHelper{
    private static CompteurSQLLite leCompteur;
    public static CompteurSQLLite getLeCompteur(){
        return leCompteur;
    }
    private static final String telMatheo = "06 48 48 27 92";
    private Boolean leBooleanDeCon = false;


    //--- Base de données

    private static final String DATABASE_NAME = "compteur.db";
    private static final int DATABASE_VERSION = 40;


    //--- Table compteur

    private final static String TABLE_COMPTEUR = "compteur";
    private final static String TABLE_RELEVEUR = "releveur";

    private final static String COLUMN_ID = "id";
    private final static String COLUMN_NOM = "nom";
    private final static String COLUMN_RUE = "rue";
    private final static String COLUMN_CODE_POSTAL = "codePostal";
    private final static String COLUMN_VILLE = "ville";
    private final static String COLUMN_INDEX_ANCIEN = "indexAncien";
    private final static String COLUMN_INDEX_NOUVEAU = "indexNouveau";
    private final static String COLUMN_NOM_RELEVEUR_COMPTEUR = "nomReleveurCompteur";
    private final static String COLUMN_PROBLEME_COMPTEUR = "pbCompteur";
    private final static String COLUMN_PROBLEME_IMAGE_COMPTEUR = "pbImgCompteur";
    private final static String COLUMN_NUM_TELEPHONE = "numTelephone";


    private final static String COLUMN_NOM_RELEVEUR = "nomReleveur";
    private final static String COLUMN_MDP_RELEVEUR = "mdpReleveur";
    private final static String COLUMN_IMAGE_RELEVEUR = "imgReleveur";

    private final static String COLUMNS_DEFINITION_COMPTEUR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOM + " TEXT,"
                    + COLUMN_RUE + " TEXT,"
                    + COLUMN_CODE_POSTAL + " TEXT,"
                    + COLUMN_VILLE + " TEXT,"
                    + COLUMN_INDEX_ANCIEN + " INTEGER,"
                    + COLUMN_INDEX_NOUVEAU + " INTEGER,"
                    + COLUMN_NOM_RELEVEUR_COMPTEUR + " TEXT,"
                    + COLUMN_PROBLEME_COMPTEUR + " TEXT,"
                    + COLUMN_PROBLEME_IMAGE_COMPTEUR + " BLOB, "
                    + COLUMN_NUM_TELEPHONE + " TEXT ";

    private final static String COLUMNS_DEFINITION_RELEVEUR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOM_RELEVEUR + " TEXT,"
                    + COLUMN_MDP_RELEVEUR + " TEXT, "
                    + COLUMN_IMAGE_RELEVEUR + " BLOB ";


    /*
     * constructeur : création ou déclaration de la base de données
     *
     */
    public CompteurSQLLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        leCompteur = this;
    }

    /*
     * Appelé lorsque la base est crée pour la première fois
     *
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Création de la base de donnée !!!!");
        // TODO Auto-generated method stub

        //--- create table compteur ---

        String sqlcreate = "create table " + TABLE_COMPTEUR
                + " (" + COLUMNS_DEFINITION_COMPTEUR + ");";

        db.execSQL (sqlcreate);

        String sqlcreate2 = "create table " + TABLE_RELEVEUR
                + " (" + COLUMNS_DEFINITION_RELEVEUR + ");";

        db.execSQL (sqlcreate2);

        //--- jeu d'essai ---

        Compteur[] t = getJeuDessai();
        for (Compteur co : t){
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOM, co.nom);
            values.put(COLUMN_RUE, co.rue);
            values.put(COLUMN_VILLE, co.ville);
            values.put(COLUMN_CODE_POSTAL, co.codePostal);
            values.put(COLUMN_INDEX_ANCIEN, co.indexAncien);
            values.put(COLUMN_INDEX_NOUVEAU, 0);
            values.put(COLUMN_NOM_RELEVEUR_COMPTEUR, "");
            values.put(COLUMN_PROBLEME_COMPTEUR, "");
            values.putNull(COLUMN_PROBLEME_IMAGE_COMPTEUR);

            Random r = new Random();
            StringBuilder sb= new StringBuilder();
            sb.append("06 ");
            for(int i =0; i<4;i++){
                int random1 = r.nextInt(9);
                int random2 = r.nextInt(9);
                sb.append(random1);
                sb.append(random2);
                sb.append(" ");
            }
            if(!leBooleanDeCon){
                values.put(COLUMN_NUM_TELEPHONE, telMatheo);
                leBooleanDeCon=true;
            }else{
                values.put(COLUMN_NUM_TELEPHONE, sb.toString());
            }

            db.insert(TABLE_COMPTEUR, null,values);
        }




        Releveur[] tReleveur = getJeuDessaiReleveur();
        for (Releveur r : tReleveur){
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOM_RELEVEUR, r.nomReleveur);
            values.put(COLUMN_MDP_RELEVEUR, r.motDePasse);
            values.putNull(COLUMN_IMAGE_RELEVEUR);

            db.insert(TABLE_RELEVEUR, null,values);
        }
    }

    /*
     * Appelé lorsque la base a besoin d'être modifiée
     * Il suffit de modifier DATABASE_VERSION !
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPTEUR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELEVEUR + ";");
        onCreate(db);
    }

    /*
     * retourne un ArraList contenant les données de la table compteur
     */
    @SuppressLint("Range")
    public ArrayList <Compteur> getListeCompteur(){
        ArrayList<Compteur> ar = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        //ar.add(getJeuDessai()[0]);
        String[] columns ={COLUMN_ID, COLUMN_NOM, COLUMN_VILLE, COLUMN_RUE,
                COLUMN_CODE_POSTAL, COLUMN_INDEX_ANCIEN, COLUMN_INDEX_NOUVEAU,
                COLUMN_NOM_RELEVEUR_COMPTEUR, COLUMN_NUM_TELEPHONE
        };
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_COMPTEUR, columns ,null,null,null,null,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Compteur co = new Compteur();

            co.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            co.nom = cursor.getString(cursor.getColumnIndex(COLUMN_NOM));
            co.rue = cursor.getString(cursor.getColumnIndex(COLUMN_RUE));
            co.ville = cursor.getString(cursor.getColumnIndex(COLUMN_VILLE));
            co.codePostal = cursor.getString(cursor.getColumnIndex(COLUMN_CODE_POSTAL));
            co.indexAncien = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_ANCIEN));
            co.indexNouveau = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_NOUVEAU));
            co.nomReleveur=cursor.getString(cursor.getColumnIndex(COLUMN_NOM_RELEVEUR_COMPTEUR));
            co.numTelephone=cursor.getString(cursor.getColumnIndex(COLUMN_NUM_TELEPHONE));
            ar.add(co);

            cursor.moveToNext();
        }

        return ar;
    }

    @SuppressLint("Range")
    public String getLeProbleme(Compteur c){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns ={COLUMN_ID, COLUMN_NOM, COLUMN_VILLE, COLUMN_RUE,
                COLUMN_CODE_POSTAL, COLUMN_INDEX_ANCIEN, COLUMN_INDEX_NOUVEAU,
                COLUMN_NOM_RELEVEUR_COMPTEUR, COLUMN_PROBLEME_COMPTEUR, COLUMN_PROBLEME_IMAGE_COMPTEUR
        };
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_COMPTEUR, columns ,null,null,null,null,null);
        String leProbleme ="";
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            if(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)) == c.id){
                leProbleme = cursor.getString(cursor.getColumnIndex(COLUMN_PROBLEME_COMPTEUR));
            }
            cursor.moveToNext();
        }
        return leProbleme;
    }

    @SuppressLint("Range")
    public byte[] getLImageProbleme(Compteur c){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns ={COLUMN_ID, COLUMN_NOM, COLUMN_VILLE, COLUMN_RUE,
                COLUMN_CODE_POSTAL, COLUMN_INDEX_ANCIEN, COLUMN_INDEX_NOUVEAU,
                COLUMN_NOM_RELEVEUR_COMPTEUR, COLUMN_PROBLEME_COMPTEUR, COLUMN_PROBLEME_IMAGE_COMPTEUR
        };
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_COMPTEUR, columns ,null,null,null,null,null);
        byte[] leProbleme = null;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            if(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)) == c.id){
                leProbleme = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROBLEME_IMAGE_COMPTEUR));
            }
            cursor.moveToNext();
        }
        return leProbleme;
    }


        @SuppressLint("Range")
    public ArrayList <Compteur> getListeCompteurWhereReleveFait(){
        ArrayList<Compteur> ar = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        //ar.add(getJeuDessai()[0]);
        String[] columns ={COLUMN_ID, COLUMN_NOM, COLUMN_VILLE, COLUMN_RUE,
                COLUMN_CODE_POSTAL, COLUMN_INDEX_ANCIEN, COLUMN_INDEX_NOUVEAU, COLUMN_NOM_RELEVEUR_COMPTEUR
        };
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_COMPTEUR, columns ,null,null,null,null,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Compteur co = new Compteur();

            co.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            co.nom = cursor.getString(cursor.getColumnIndex(COLUMN_NOM));
            co.rue = cursor.getString(cursor.getColumnIndex(COLUMN_RUE));
            co.ville = cursor.getString(cursor.getColumnIndex(COLUMN_VILLE));
            co.codePostal = cursor.getString(cursor.getColumnIndex(COLUMN_CODE_POSTAL));
            co.indexAncien = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_ANCIEN));
            co.indexNouveau = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_NOUVEAU));
            co.nomReleveur=cursor.getString(cursor.getColumnIndex(COLUMN_NOM_RELEVEUR_COMPTEUR));
            if(co.indexNouveau!=0){
                ar.add(co);
            }

            cursor.moveToNext();
        }
        return ar;
    }

    @SuppressLint("Range")
    public ArrayList <Compteur> getListeCompteurWhereRelevePasFait(){
        ArrayList<Compteur> ar = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        //ar.add(getJeuDessai()[0]);
        String[] columns ={COLUMN_ID, COLUMN_NOM, COLUMN_VILLE, COLUMN_RUE,
                COLUMN_CODE_POSTAL, COLUMN_INDEX_ANCIEN, COLUMN_INDEX_NOUVEAU, COLUMN_NOM_RELEVEUR_COMPTEUR
        };
        Cursor cursor = db.query(TABLE_COMPTEUR, columns ,null,null,null,null,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Compteur co = new Compteur();

            co.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            co.nom = cursor.getString(cursor.getColumnIndex(COLUMN_NOM));
            co.rue = cursor.getString(cursor.getColumnIndex(COLUMN_RUE));
            co.ville = cursor.getString(cursor.getColumnIndex(COLUMN_VILLE));
            co.codePostal = cursor.getString(cursor.getColumnIndex(COLUMN_CODE_POSTAL));
            co.indexAncien = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_ANCIEN));
            co.indexNouveau = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX_NOUVEAU));
            co.nomReleveur=cursor.getString(cursor.getColumnIndex(COLUMN_NOM_RELEVEUR_COMPTEUR));
            if(co.indexNouveau==0){
                ar.add(co);
            }

            cursor.moveToNext();
        }
        return ar;
    }

    public void setComteur(Compteur co){
        int id = co.id;
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, co.id);
        values.put(COLUMN_NOM, co.nom);
        values.put(COLUMN_RUE, co.rue);
        values.put(COLUMN_VILLE, co.ville);
        values.put(COLUMN_CODE_POSTAL, co.codePostal);
        values.put(COLUMN_INDEX_ANCIEN, co.indexAncien);
        values.put(COLUMN_INDEX_NOUVEAU, co.indexNouveau);
        values.put(COLUMN_NOM_RELEVEUR_COMPTEUR, co.nomReleveur);
        getWritableDatabase().update(TABLE_COMPTEUR,values,COLUMN_ID+"="+id,null);

    }

    public void setProbleme(Compteur co, String probleme, byte[] byteArray){
        int id = co.id;
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, co.id);
        values.put(COLUMN_NOM, co.nom);
        values.put(COLUMN_RUE, co.rue);
        values.put(COLUMN_VILLE, co.ville);
        values.put(COLUMN_CODE_POSTAL, co.codePostal);
        values.put(COLUMN_INDEX_ANCIEN, co.indexAncien);
        values.put(COLUMN_INDEX_NOUVEAU, co.indexNouveau);
        values.put(COLUMN_NOM_RELEVEUR_COMPTEUR, co.nomReleveur);
        values.put(COLUMN_PROBLEME_COMPTEUR, probleme);
        if(byteArray == null){
            if(this.getLImageProbleme(co) == null){
                values.putNull(COLUMN_PROBLEME_IMAGE_COMPTEUR);
            }else{
                values.put(COLUMN_PROBLEME_IMAGE_COMPTEUR, this.getLImageProbleme(co));
            }
        }else{
            values.put(COLUMN_PROBLEME_IMAGE_COMPTEUR, byteArray);
        }
        getWritableDatabase().update(TABLE_COMPTEUR,values,COLUMN_ID+"="+id,null);

    }


    public void updateReleveur(Releveur re){
        Bitmap bmp = re.image;
        byte[] byteArray = null;
        if(bmp != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }


        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM_RELEVEUR, re.nomReleveur);
        values.put(COLUMN_MDP_RELEVEUR, re.motDePasse);
        if(byteArray == null){
            values.putNull(COLUMN_IMAGE_RELEVEUR);
        }else{
            values.put(COLUMN_IMAGE_RELEVEUR, byteArray);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println(re.id);
        long update = db.update(TABLE_RELEVEUR,values,COLUMN_ID+"="+re.id,null);
        System.out.println(update);
    }

    public void addReleveur(Releveur re){
        Bitmap bmp = re.image;
        byte[] byteArray = null;
        if(bmp != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            bmp.recycle();
        }


        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM_RELEVEUR, re.nomReleveur);
        values.put(COLUMN_MDP_RELEVEUR, re.motDePasse);
        if(byteArray == null){
            values.putNull(COLUMN_IMAGE_RELEVEUR);
        }else{
            values.put(COLUMN_IMAGE_RELEVEUR, byteArray);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_RELEVEUR, null,values);

    }

    /*
     * Génération du jeu d'essai
     */
    private Compteur[] getJeuDessai(){
        String[] tVille = {"Limoges", "Couzeix", "Panazol"};

        String[] tCodePostal = {"87000", "87200", "87300"};

        String[] tNom = {"Pasqualini", "Bogusz", "Techer", "Bourgeois", "Tournie"};

        String[] tRue = {"Rue de la Palisse",
                "Rue des Petits Pois",
                "Rue des Milles Etangs",
                "Rue François Perrin",
                "Rue Turgot"};

        int ct = 20;
        Compteur[] t = new Compteur[ct];

        for(int i = 0; i < ct ; i++){
            int iville = (int)(Math.random() * tVille.length);
            int inom = (int)(Math.random() * tNom.length);
            int irue = (int) (Math.random() * tRue.length);

            Compteur co = new Compteur();

            co.nom = tNom[inom];
            co.ville = tVille[iville];
            co.codePostal = tCodePostal[iville];
            co.indexAncien = (int)( Math.random() * 20000);
            co.indexNouveau = 0;
            co.rue = tRue[irue];

            t[i] = co;
        }

        return t;
    }

    /*
     * retourne un ArrayList contenant les noms et mot de passe des visiteurs
     */
    private Releveur[] getJeuDessaiReleveur(){

        Releveur[] liste = new Releveur[3];
        String[] tNom = {"Claude", "Thierry", "Agnès"};
        for(int i=0;i<liste.length;i++){
            Releveur releveur = new Releveur();
            releveur.nomReleveur = tNom[i];
            releveur.motDePasse = tNom[i];
            liste[i] = releveur;
        }

        return liste;

    }

    @SuppressLint("Range")
    public ArrayList <Releveur> getListeReleveurs(){
        ArrayList<Releveur> ar = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        //ar.add(getJeuDessai()[0]);
        String[] columns ={COLUMN_ID,COLUMN_NOM_RELEVEUR,COLUMN_MDP_RELEVEUR, COLUMN_IMAGE_RELEVEUR};
        Cursor cursor = db.query(TABLE_RELEVEUR, columns ,null,null,null,null,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Releveur r = new Releveur();

            r.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            r.nomReleveur= cursor.getString(cursor.getColumnIndex(COLUMN_NOM_RELEVEUR));
            r.motDePasse = cursor.getString(cursor.getColumnIndex(COLUMN_MDP_RELEVEUR));
            byte[] temp = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_RELEVEUR));
            if(temp != null){
                r.image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            }else{
                r.image=null;
            }
            ar.add(r);

            cursor.moveToNext();
        }

        return ar;
    }

}