package ru.vkatzyn.lorempicsum;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImagesFragment extends Fragment {

    private ImageView picture;
    private ProgressBar progressBar;
    private TextView startHintTextView;
    private APIInterface apiInterface;
    private Call<Void> call;
    private String url;
    private boolean isRetrievingImage;
    private boolean isCaching;
    private ConnectivityManager connectivityManager;
    private Toast toast;

    public ImagesFragment() {
    }

    public static ImagesFragment newInstance() {
        return new ImagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        url = null;
        isRetrievingImage = false;
        isCaching = false;
        connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        picture = (ImageView) getView().findViewById(R.id.iv_picture);
        progressBar = (ProgressBar) getView().findViewById(R.id.image_retrieving_progress);
        startHintTextView = (TextView) getView().findViewById(R.id.tv_start_hint);

        progressBar.setVisibility(View.GONE);
        picture.setVisibility(View.GONE);
        startHintTextView.setVisibility(View.VISIBLE);

        // Check for internet-connection.
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            startHintTextView.setText(R.string.start_hint);
        } else {
            startHintTextView.setText(R.string.no_internet_connection);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore fragment state.
        if (savedInstanceState != null) {
            // Solution to a bug when a hidden fragment appears visible on restoring it's state.
            if (savedInstanceState.getBoolean("isHidden")) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
            }
            url = savedInstanceState.getString("url");
            if (url != null) {
                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                Glide.with(getActivity())
                        .load(url)
                        .apply(requestOptions)
                        .into(picture);
                progressBar.setVisibility(View.GONE);
                picture.setVisibility(View.VISIBLE);
                startHintTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHidden", isHidden());
        outState.putString("url", url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check internet-connection.
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            startHintTextView.setText(R.string.no_internet_connection);
            startHintTextView.setVisibility(View.VISIBLE);
            picture.setVisibility(View.GONE);
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        if (id == R.id.action_random) {
            if (isRetrievingImage) {
                return super.onOptionsItemSelected(item);
            }

            isRetrievingImage = true;
            startHintTextView.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            call = apiInterface.getImage(String.valueOf(MainActivity.imageWidth),
                    String.valueOf(MainActivity.imageHeight));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    url = response.raw().request().url().toString();
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(getActivity())
                            .load(url)
                            .apply(requestOptions)
                            .into(picture);

                    isRetrievingImage = false;
                    picture.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    isRetrievingImage = false;
                    picture.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if (!call.isCanceled()) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getContext(), getString(R.string.random_failure),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        } else if (id == R.id.action_add_to_favorite) {
            if (url == null || isCaching || isRetrievingImage) {
                return super.onOptionsItemSelected(item);
            }
            if (CacheUtils.isPictureUrlCached(url, getContext())) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getContext(), getString(R.string.add_to_favorite_already_added),
                        Toast.LENGTH_SHORT);
                toast.show();
                return super.onOptionsItemSelected(item);
            }
            if (url != null) {
                isCaching = true;
                progressBar.setVisibility(View.VISIBLE);

                call = apiInterface.getImage(String.valueOf(MainActivity.imageWidth),
                        String.valueOf(MainActivity.imageHeight));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        RequestOptions requestOptions =
                                new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
                        // Couldn't get this to work the right way, so had to use deprecated method.
                        Glide.with(getActivity())
                                .load(url)
                                .apply(requestOptions)
                                .downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                                        com.bumptech.glide.request.target.Target.SIZE_ORIGINAL);
                        CacheUtils.savePictureUrlToCache(url, getContext());
                        isCaching = false;
                        progressBar.setVisibility(View.GONE);
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getContext(), getString(R.string.add_to_favorite_success),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        isCaching = false;
                        progressBar.setVisibility(View.GONE);
                        if (!call.isCanceled()) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(getContext(), getString(R.string.add_to_favorite_failure),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            Log.e(ImagesFragment.class.getSimpleName(), "Failed to cache image");
                        }
                    }
                });

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
