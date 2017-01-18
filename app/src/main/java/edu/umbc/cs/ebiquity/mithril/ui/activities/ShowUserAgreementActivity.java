package edu.umbc.cs.ebiquity.mithril.ui.activities;

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

import java.io.FileNotFoundException;
import java.io.IOException;

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

    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button mButtonPrevious;

    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button mButtonNext;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        savedInstanceState = aSavedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_agreement);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        // Retain view references.
        mImageView = (ImageView) findViewById(R.id.image);
        mButtonPrevious = (Button) findViewById(R.id.previous);
        mButtonNext = (Button) findViewById(R.id.next);
        // Bind events.
//        mButtonPrevious.setOnClickListener(this);
//        mButtonNext.setOnClickListener(this);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        // Bind events.
        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
            }
        });
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer() throws IOException {
        // In this sample, we read a PDF from the assets directory.
        try {
            mFileDescriptor = getApplicationContext().getAssets().openFd(MithrilApplication.getFlierPdfFileName()).getParcelFileDescriptor();
            // This is the PdfRenderer we use to render the PDF.
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
            // Show the first page by default.
            int index = 0;
            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
            if (null != savedInstanceState) {
                index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
            }
            showPage(index);
        } catch (FileNotFoundException e) {
            Log.d(MithrilApplication.getDebugTag(), MithrilApplication.getFlierPdfFileName() + " not found. Make sure the file name/path is correct!");
            finish();
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
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        setTitle(getString(index + 1, pageCount));
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
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }
}