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
    Call<UsuarioObjeto> crearUsuario(@Body UsuarioObjeto usuarioObjeto);

    @GET("usuarios/{id}")
    Call<UsuarioObjeto> obtenerUsuario(@Path("id") int id);

    @PUT("usuarios/{id}")
    Call<UsuarioObjeto> actualizarUsuario(@Path("id") int id, @Body UsuarioObjeto usuarioObjeto);

    @DELETE("usuarios/{id}")
    Call<Void> eliminarUsuario(@Path("id") int id);

    //Hacer lo mismo con SplitObjeto y PagoObjeto
}
