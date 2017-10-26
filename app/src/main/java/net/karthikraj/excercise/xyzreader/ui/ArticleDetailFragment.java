package net.karthikraj.excercise.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import net.karthikraj.excercise.xyzreader.R;
import net.karthikraj.excercise.xyzreader.data.ArticleLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "ArticleDetailFragment";
    @BindView(R.id.photo)
    ImageView mPhotoView;
    @BindView(R.id.meta_bar)
    LinearLayout metaBar;
    @BindView(R.id.article_title)
    TextView mTitleView;
    @BindView(R.id.article_author)
    TextView mAuthorView;
    @BindView(R.id.article_body)
    TextView mBodyView;
    @BindView(R.id.share_fab)
    FloatingActionButton mShareFab;
    @Nullable
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Nullable
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    private long mItemId;
    private Unbinder unbinder;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }


    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor cursor) {
        if (cursor == null || cursor.isClosed() || !cursor.moveToFirst()) {
            return;
        }

        mBodyView.setText(cursor.getString(ArticleLoader.Query.BODY));

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        final String title = cursor.getString(ArticleLoader.Query.TITLE);
        mTitleView.setText(title);

        mAuthorView.setText(DateUtils.getRelativeTimeSpanString(
                        cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString()
                        + " by "
                        + cursor.getString(ArticleLoader.Query.AUTHOR));

        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(cursor.getString(ArticleLoader.Query.BODY))
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        String photo = cursor.getString(ArticleLoader.Query.PHOTO_URL);


        Picasso.with(mPhotoView.getContext())
                .load(cursor.getString(ArticleLoader.Query.THUMB_URL))
                .placeholder(R.drawable.photo_background_protection)
                .error(R.drawable.photo_background_protection)
                .into(mPhotoView, PicassoPalette.with(cursor.getString(ArticleLoader.Query.THUMB_URL), mPhotoView)
                        .use(PicassoPalette.Profile.MUTED_DARK)
                        .intoBackground(metaBar));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
