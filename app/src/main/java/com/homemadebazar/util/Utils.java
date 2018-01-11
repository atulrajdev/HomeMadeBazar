package com.homemadebazar.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.homemadebazar.activity.FoodieHomeActivity;
import com.homemadebazar.activity.HomeActivity;
import com.homemadebazar.activity.MarketPlaceHomeActivity;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sumit on 27/08/17.
 */

public class Utils {

    public static final String TYPE_PHONE_NUMBER = "phone_number";
    public static final String TYPE_EMAIL = "email";
    public static Uri cameraUri;

    public static void generateKeyHash(Context mContext) {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo("com.homemadebazar", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static void handleError(String message, final Context context, Runnable runnable) {
        DialogUtils.showAlert(context, message);
    }

    public static boolean isValid(String input, String type) {
        boolean valid = false;
        try {
            if (type.equals(TYPE_EMAIL)) {
                if (!TextUtils.isEmpty(input.trim())) {
                    valid = android.util.Patterns.EMAIL_ADDRESS.matcher(input)
                            .matches();
                }
            } else if (type.equals(TYPE_PHONE_NUMBER) && !TextUtils.isEmpty(input.trim())) {
                String regEx = "^[0-9]{10}$";
                valid = input.matches(regEx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return valid;
        }
    }

    public static void cameraIntent(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.Keys.REQUEST_PERMISSION_CAMERA);

        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
            cameraUri = Uri.fromFile(file);
//            cameraUri = FileProvider.getUriForFile(activity,
//                    BuildConfig.APPLICATION_ID + ".provider",
//                    file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            activity.startActivityForResult(cameraIntent, Constants.Keys.REQUEST_CAMERA);
        }
    }

    public static void cameraIntent(Activity activity, android.support.v4.app.Fragment fragment) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.Keys.REQUEST_PERMISSION_CAMERA);

        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
            cameraUri = Uri.fromFile(file);
//            cameraUri = FileProvider.getUriForFile(activity,
//                    BuildConfig.APPLICATION_ID + ".provider",
//                    file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            fragment.startActivityForResult(cameraIntent, Constants.Keys.REQUEST_CAMERA);
        }
    }

    public static Uri getCameraUri() {
        if (cameraUri != null)
            return cameraUri;
        return null;
    }

    public static void gallaryIntent(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.Keys.REQUEST_PERMISSION_GALLERY);
        } else {
            Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            gallaryIntent.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(gallaryIntent, "Select Picture"), Constants.Keys.REQUEST_GALLERY);
        }
    }

    public static void gallaryIntent(Activity activity, android.support.v4.app.Fragment fragment) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.Keys.REQUEST_PERMISSION_GALLERY);
        } else {
            Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            gallaryIntent.setType("image/*");
            fragment.startActivityForResult(Intent.createChooser(gallaryIntent, "Select Picture"), Constants.Keys.REQUEST_GALLERY);
        }
    }


    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                System.out.println("Coulumn name :" + cursor.getColumnName(i));
                System.out.println("Coulumn type :" + cursor.getType(i));
                try {
                    System.out.println("Coulumn value :" + cursor.getString(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void openAccountTypeHomeScreen(Context context, String accountType) {
        if (accountType.equals(Constants.AccountType.HOME_CHEF)) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if (accountType.equals(Constants.AccountType.FOODIE)) {
            Intent intent = new Intent(context, FoodieHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if (accountType.equals(Constants.AccountType.MARKET_PLACE)) {
            Intent intent = new Intent(context, MarketPlaceHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Log.i("URI", uri + "");
        String result = uri + "";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length - 1];
            final String[] dat = imgary.split("%3A");
            final String docId = dat[1];
            final String type = dat[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
            } else if ("audio".equals(type)) {
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    dat[1]
            };
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean checkLocationPermission(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.Keys.LOCATION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.Keys.LOCATION_REQUEST);

                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static int parseInteger(String value) {
        try {
            int a = Integer.parseInt(value);
            return a;
        } catch (Exception e) {
            return 0;
        }
    }

    public static Bitmap takeScreenshot(View rootView) {
        Bitmap b = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        rootView.draw(c);
        return b;

    }


}
