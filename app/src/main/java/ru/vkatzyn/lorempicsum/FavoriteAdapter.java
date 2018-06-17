package ru.vkatzyn.lorempicsum;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private final RequestManager glide;
    private ArrayList<String> urls;


    public FavoriteAdapter(RequestManager glide, ArrayList<String> urls) {
        this.glide = glide;
        this.urls = urls;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .onlyRetrieveFromCache(true);
        glide.load(urls.get(position)).apply(requestOptions).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (urls == null)
            return 0;
        return urls.size();
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
