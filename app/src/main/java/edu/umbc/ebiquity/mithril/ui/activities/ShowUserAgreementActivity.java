package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

/**
 * This fragment has a big {@ImageView} that shows PDF pages, and 2 {@link android.widget.Button}s to move between
 * pages. We use a {@link android.graphics.pdf.PdfRenderer} to render PDF pages as {@link android.graphics.Bitmap}s.
 */
public class ShowUserAgreementActivity extends AppCompatActivity {
    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link android.widget.ImageView} that shows a PDF page as a {@link android.graphics.Bitmap}
     */
    private ImageView mImageView;

    private Button mButtonNext;
    private Button mIAgreeBtn;
    private Button mIDisagreeBtn;

    private SharedPreferences sharedPreferences;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        savedInstanceState = aSavedInstanceState;
        super.onCreate(savedInstanceState);
//        testUserAgreementAndLaunchNextActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        makeFullScreen();
        initViews();
        try {
            openRenderer();
        } catch (IOException e) {
            e.printStackTrace();
            PermissionHelper.toast(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT);
            finish();
        }
    }

    private void makeFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

//    private void testUserAgreementAndLaunchNextActivity() {
//        if (PermissionHelper.isAllRequiredPermissionsGranted(this) && !PermissionHelper.needsUsageStatsPermission(this))
//            startNextActivity(this, CoreActivity.class);
//        else
//            startNextActivity(this, PermissionAcquisitionActivity.class);
//    }

    private void initViews() {
        setContentView(R.layout.activity_show_user_agreement);
        sharedPreferences = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);

        mImageView = (ImageView) findViewById(R.id.image);
        mButtonNext = (Button) findViewById(R.id.next);
        mIAgreeBtn = (Button) findViewById(R.id.iAgreeBtn);
        mIDisagreeBtn = (Button) findViewById(R.id.iDisagreeBtn);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getInt(MithrilAC.getPrefKeyUserAgreementPageNumber(), 0) == 1) {
                    showPage(0);
                    mButtonNext.setText(R.string.next);
                } else {
                    showPage(1);
                    mButtonNext.setText(R.string.previous);
                }
            }
        });
        mIAgreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = v.getContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putBoolean(MithrilAC.getPrefKeyUserConsent(), true);
                editor.apply();
                // User has agreed, ask for permissions
                startNextActivity(v.getContext(), InstallAppsActivity.class);
            }
        });
        mIDisagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = v.getContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putBoolean(MithrilAC.getPrefKeyUserConsent(), false);
                editor.apply();
                PermissionHelper.quitMithril(v.getContext(), MithrilAC.MITHRIL_BYE_BYE_MESSAGE);
            }
        });
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer() throws IOException {
        File parent = getFilesDir();
        String child = MithrilAC.getFlierPdfFileName();
        // Copy the pdf to a usable location
        copyAssets(parent, child);

//        String strDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + "Pdfs";
//        File fileDir = new File(strDir);
//        fileDir.mkdirs();
//        File file = new File(fileDir, MithrilAC.getFlierPdfFileName());
//        File file = new File(parent, child);
        //new File("/data/data/" + getPackageName() + "/files/" + MithrilAC.getFlierPdfFileName());

        // In this sample, we read a PDF from the assets directory.
        try {
            // This is the PdfRenderer we use to render the PDF.
            mPdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(new File(parent, child), ParcelFileDescriptor.MODE_READ_ONLY));
            // Show the first page by default.
            int index = 0;
            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
            if (null != savedInstanceState) {
                index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
            }
            // Showing initial page
            showPage(index);
        } catch (FileNotFoundException e) {
            Log.e(MithrilAC.getDebugTag(), MithrilAC.getFlierPdfFileName() + " not found. Make sure the file name/path is correct!");
            // File could not be found!
        }
    }

    private void copyAssets(File parent, String child) {
        File file = new File(parent, child);

        if (!file.exists()) {
            try {
                writeFile(openFileOutput(file.getName(), Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                Log.e(MithrilAC.getDebugTag(), MithrilAC.getFlierPdfFileName() + " not found. Make sure the file name/path is correct!");
                // File could not be found!
            }
//        } else {
//            Log.d(MithrilAC.getDebugTag(), "file already exists");
        }
    }

    private void writeFile(OutputStream destination) {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        try {
            in = assetManager.open(MithrilAC.getFlierPdfFileName());

            copyFile(in, destination);

            in.close();
            destination.flush();
            destination.close();
        } catch (IOException iOException) {
//            Log.e(MithrilAC.getDebugTag(), iOException.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
//                    Log.d(MithrilAC.getDebugTag(), "Filer file threw NullPointerException");
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
//                    Log.d(MithrilAC.getDebugTag(), "output file threw NullPointerException");
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage)
            mCurrentPage.close();
        if (mPdfRenderer != null)
            mPdfRenderer.close();
        if (mFileDescriptor != null)
            mFileDescriptor.close();
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        SharedPreferences.Editor editor = this.getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        editor.putInt(MithrilAC.getPrefKeyUserAgreementPageNumber(), index);
        editor.apply();
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(
                mCurrentPage.getWidth(),
                mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        mImageView.setImageBitmap(bitmap);
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onPause() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    @Override
    public void onBackPressed() {
        finish(); // quit app
    }

    private void startNextActivity(Context context, Class activityClass) {
        Intent launchNextActivity = new Intent(context, activityClass);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchNextActivity);
    }
}