package fr.gds.relevecompteur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSION = 1;
    ImageView imageView;
    Bitmap bitmap = null;
    byte img[];
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = findViewById(R.id.register);
        Button retour = findViewById(R.id.retour);
        layout = findViewById(R.id.img);

        register.setOnClickListener(this);
        retour.setOnClickListener(this);
        layout.setOnClickListener(this);

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
            case R.id.register:
                EditText identifiant = findViewById(R.id.identifiant);
                EditText mdp = findViewById(R.id.password);
                if(identifiant.getText().toString().equals("")){
                    Toast toast = Toast.makeText(this,"Erreur : veuillez entrer un identifiant",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(mdp.getText().toString().equals("")){
                    Toast toast = Toast.makeText(this,"Erreur : veuillez entrer un mot de passe",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Releveur r = new Releveur();
                r.nomReleveur = identifiant.getText().toString();
                r.motDePasse = mdp.getText().toString();
                r.image = bitmap;
                CompteurSQLLite compteurSQLLite = CompteurSQLLite.getLeCompteur();
                compteurSQLLite.addReleveur(r);
                Intent intent3 = new Intent(this,Connexion.class);
                Toast toast = Toast.makeText(this,"Inscription réussie, veuillez vous connecter",Toast.LENGTH_SHORT);
                toast.show();
                Connexion.releveurFraichementInscrit=r;
                startActivity(intent3);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.retour:
                Intent intent = new Intent(this,MainActivity.class);
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
                Intent imageSelection = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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

                bitmap = getCorrectlyOrientedImage(this,selectedImage);
                //bitmap = Bitmap.createScaledBitmap(bitmap,150,150,true);

                ImageView bg = findViewById(R.id.bg);
                bg.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 &&  orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 360 &&  rotatedHeight > 360) {
            float widthRatio = ((float) rotatedWidth) / ((float) 360);
            float heightRatio = ((float) rotatedHeight) / ((float) 360);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }




}