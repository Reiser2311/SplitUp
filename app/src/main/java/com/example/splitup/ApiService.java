package com.example.splitup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @GET("usuarios/{id}")
    Call<Usuario> obtenerUsuario(@Path("id") int id);

    @PUT("usuarios/{id}")
    Call<Usuario> actualizarUsuario(@Path("id") int id, @Body Usuario usuario);

    @DELETE("usuarios/{id}")
    Call<Void> eliminarUsuario(@Path("id") int id);

    //Hacer lo mismo con Split y Pago
}
