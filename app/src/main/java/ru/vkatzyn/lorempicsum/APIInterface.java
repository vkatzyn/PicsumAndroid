package ru.vkatzyn.lorempicsum;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("/{width}/{height}/?random")
    Call<Void> getImage(
            @Path("width") String width,
            @Path("height") String height
    );
}
