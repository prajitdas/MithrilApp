package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

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

    private Button mButtonOkay;
    private Button mButtonCancel;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        savedInstanceState = aSavedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
        try {
            openRenderer();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void initViews() {
        setContentView(R.layout.activity_show_user_agreement);

        mImageView = (ImageView) findViewById(R.id.image);
        mButtonOkay = (Button) findViewById(R.id.okay);
        mButtonCancel = (Button) findViewById(R.id.cancel);

        makeFullScreen();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mButtonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultOkay();
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCanceled();
            }
        });
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer() throws IOException {
        // Copy the pdf to a usable location
        CopyReadAssets();

//        String strDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + "Pdfs";
//        File fileDir = new File(strDir);
//        fileDir.mkdirs();
//        File file = new File(fileDir, MithrilApplication.getFlierPdfFileName());
        File file = new File("/data/data/" + getPackageName() + "/files/" + MithrilApplication.getFlierPdfFileName());

        // In this sample, we read a PDF from the assets directory.
        try {
            // This is the PdfRenderer we use to render the PDF.
            mPdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            // Show the first page by default.
            int index = 0;
            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
            if (null != savedInstanceState) {
                index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
            }
            // We just have one page
            showPage(0);
        } catch (FileNotFoundException e) {
            Log.d(MithrilApplication.getDebugTag(), MithrilApplication.getFlierPdfFileName() + " not found. Make sure the file name/path is correct!");
            // File could not be found!
            resultCanceled();
        }
    }

    private void resultOkay() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void resultCanceled() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void CopyReadAssets() {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), MithrilApplication.getFlierPdfFileName());

        if (!file.exists()) {
            try {
                in = assetManager.open(MithrilApplication.getFlierPdfFileName());
                out = openFileOutput(file.getName(), Context.MODE_PRIVATE);

                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        } else {
            Log.d("test", "file already exists");
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
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
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
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }
}