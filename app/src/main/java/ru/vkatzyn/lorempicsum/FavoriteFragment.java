package ru.vkatzyn.lorempicsum;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private FavoriteAdapter adapter;
    private ArrayList<String> urls;
    private RecyclerView recyclerView;
    /**
     * Contains the message to show when no images were cached.
     */
    private TextView emptyTextView;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Preloader for recyclerView for smooth scrolling.
        ListPreloader.PreloadSizeProvider sizeProvider =
                new FixedPreloadSizeProvider(MainActivity.imageWidth, MainActivity.imageHeight);
        ListPreloader.PreloadModelProvider modelProvider = new FavoritePreloadModelProvider();
        RecyclerViewPreloader<ImageModel> preloader =
                new RecyclerViewPreloader<ImageModel>(Glide.with(getActivity()),
                        modelProvider, sizeProvider, 10);

        emptyTextView = (TextView) result.findViewById(R.id.tv_favorite_empty);
        recyclerView = (RecyclerView) result.findViewById(R.id.rv_favorite_images);

        /* Setting up recyclerView */
        urls = CacheUtils.getPictureUrls(getContext());
        adapter = new FavoriteAdapter(Glide.with(getActivity()), urls);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.addOnScrollListener(preloader);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        if (urls == null) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
        /* ----------------------- */

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    /**
     * Preloader for RecyclerView. Preloads images for smooth scrolling.
     */
    private class FavoritePreloadModelProvider implements ListPreloader.PreloadModelProvider {

        @NonNull
        @Override
        public List getPreloadItems(int position) {
            String url = urls.get(position);
            if (url == null || url.length() == 0) {
                return Collections.emptyList();
            }
            return Collections.singletonList(url);
        }

        @Nullable
        @Override
        public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Object url) {
            RequestOptions requestOptions
                    = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .onlyRetrieveFromCache(true);
            return Glide.with(getActivity())
                    .load(url).apply(requestOptions);
        }
    }
}
