package me.declangao.wordpressreader.app;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.model.Post;

public class MainActivity extends AppCompatActivity implements
        MainFragment.CategoryListener,
        RecyclerViewFragment.PostListListener, PostFragment.PostListener,
        TabLayoutFragment.TabLayoutListener, SearchResultFragment.SearchResultListener,
        CommentFragment.CommentListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //public static final String TAB_LAYOUT_FRAGMENT_TAG = "TabLayoutFragment";
    public static final String POST_FRAGMENT_TAG = "PostFragment";
    public static final String COMMENT_FRAGMENT_TAG = "CommentFragment";
    public static final String MAIN_FRAGMENT_TAG = "MainFragment";
    public static final String RECYCLER_VIEW_FRAGMENT_TAG = "RecyclerViewFragment";
    public static final String SEARCH_RESULT_FRAGMENT_TAG = "SearchResultFragment";

    private FragmentManager fm = null;

    private PostFragment pf;
    private MainFragment mf;
    private CommentFragment cf;
    private SearchResultFragment srf;
    private RecyclerViewFragment rvf;

    private boolean isClickedBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        // Setup fragments
        srf = new SearchResultFragment();
        pf = new PostFragment();
        cf = new CommentFragment();
        mf = new MainFragment();
        rvf = new RecyclerViewFragment();

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(android.R.id.content, pf, POST_FRAGMENT_TAG);
        ft.add(android.R.id.content, cf, COMMENT_FRAGMENT_TAG);
        ft.add(android.R.id.content, srf, SEARCH_RESULT_FRAGMENT_TAG);
        ft.add(android.R.id.content, mf, MAIN_FRAGMENT_TAG);
        ft.add(android.R.id.content, rvf, RECYCLER_VIEW_FRAGMENT_TAG);

        ft.hide(pf);
        ft.hide(cf);
        ft.hide(srf);
        ft.hide(rvf);
        ft.commit();
    }


    /**
     * Invoked when a post in the list is selected
     *
     * @param post Selected Post object
     */
    @Override
    public void onPostSelected(Post post, boolean isSearch) {
        // Find the fragment in order to set it up later
        pf = (PostFragment) getSupportFragmentManager().findFragmentByTag(POST_FRAGMENT_TAG);

        // Set necessary arguments
        Bundle args = new Bundle();
        args.putInt("id", post.getId());
        args.putString("title", post.getTitle());
        args.putString("date", post.getDate());
        args.putString("author", post.getAuthor());
        args.putString("content", post.getContent());
        args.putString("url", post.getUrl());
        args.putBoolean("isAudio", post.isAudio());
        args.putBoolean("isEvent", post.isEvent());
        //args.putString("thumbnailUrl", post.getThumbnailUrl());
        args.putString("featuredImage", post.getFeaturedImageUrl());

        // Configure PostFragment to display the right post
        pf.setUIArguments(args);

        // Show the fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (!isSearch) { // Hide TabLayoutFragment if this is not search result
            //ft.hide(tlf);
            ft.hide(rvf);
        } else { // Otherwise, hide the search result, ie. SearchResultFragment.
            ft.hide(srf);
        }
        ft.show(pf);
        ft.hide(mf);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onCategorySelected(Category cat) {
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        // Send query to fragment using factory method
        rvf = RecyclerViewFragment.newInstance(cat.getId());
        ft.add(android.R.id.content, rvf);
        ft.hide(mf);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Invoked when a search query is submitted
     *
     * @param query Selected Post object
     */
    @Override
    public void onSearchSubmitted(String query) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        // Send query to fragment using factory method
        srf = SearchResultFragment.newInstance(query);
        ft.add(android.R.id.content, srf);
        ft.hide(mf);
        ft.hide(rvf);
        ft.hide(pf);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Invoked when comment menu is selected
     *
     * @param id ID of the article, assigned by WordPress
     */
    @Override
    public void onCommentSelected(int id) {
        cf = (CommentFragment) getSupportFragmentManager().findFragmentByTag(COMMENT_FRAGMENT_TAG);
        Bundle args = new Bundle();
        args.putInt("id", id);
        // Setup CommentFragment to display the right comments page
        cf.setUIArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.hide(pf);
        ft.show(cf);
        ft.addToBackStack(null);
        ft.commit();
    }


    /**
     * Intercept back button event, reset ActionBar if necessary
     */
    @Override
    public void onBackPressed() {

        FragmentTransaction ft = fm.beginTransaction();

        resetActionBarIfApplicable();

        //if(srf.isVisible() && mf.isVisible()) ft.hide(mf);

        if(!mf.isVisible()){super.onBackPressed(); return;}

        if(!isClickedBack && !rvf.isVisible()){
            Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show();
            isClickedBack = true;
        }
        else{
            //finish();
            super.onBackPressed();
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() { isClickedBack = false; }
        }.start();
    }

    /**
     * Simulate a back button press when home is selected
     */
    @Override
    public void onHomePressed() {
        resetActionBarIfApplicable();
        //fm.popBackStack();
        super.onBackPressed();
    }

    @Override
    public void onHomePressedFromList() {
        Log.d("WOX", "Home pressed received");
        super.onBackPressed();
    }

    /**
     * Reset TabLayoutFragment's ActionBar if necessary
     */
    private void resetActionBarIfApplicable() {
        Log.d(TAG, "SearchResultFragment is visible: " + srf.isHidden());
        if (srf.isVisible()) {
            mf.resetActionBar();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    // Commented out coz we will let fragments handle their own Options Menus
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_main, menu);
    //    return true;
    //}

}
