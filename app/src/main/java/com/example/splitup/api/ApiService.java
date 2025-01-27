package com.example.splitup.api;

import com.example.splitup.objetos.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("usuarios")
    Call<ObjetoUsuario> crearUsuario(@Body ObjetoUsuario usuarioObjeto);

    @GET("usuarios/{correo}")
    Call<ObjetoUsuario> obtenerUsuario(@Path("correo") String correo);

    @GET("/usuarios")
    Call<List<ObjetoUsuario>> obtenerUsuarios();

    @PUT("usuarios/{correo}")
    Call<ObjetoUsuario> actualizarUsuario(@Path("correo") String correo, @Body ObjetoUsuario usuarioObjeto);

    @DELETE("usuarios/{correo}")
    Call<Void> eliminarUsuario(@Path("correo") String correo);

    @POST("splits")
    Call<ObjetoSplit> crearSplit(@Body ObjetoSplit splitObjeto);

    @GET("splits/{id}")
    Call<ObjetoSplit> obtenerSplit(@Path("id") int id);

    @GET("/splits")
    Call<List<ObjetoSplit>> obtenerSplits();

    @GET("/splits/usuarios/{correo}")
    Call<List<ObjetoSplit>> obtenerSplitsPorUsuario(@Path("correo") String correo);

    @PUT("splits/{id}")
    Call<ObjetoSplit> actualizarSplit(@Path("id") int id, @Body ObjetoSplit splitObjeto);

    @DELETE("splits/{id}")
    Call<Void> eliminarSplit(@Path("id") int id);

    @POST("pagos")
    Call<ObjetoPago> crearPago(@Body ObjetoPago pagoObjeto);

    @GET("pagos/{id}")
    Call<ObjetoPago> obtenerPago(@Path("id") int id);

    @GET("pagos/splits/{id}")
    Call<List<ObjetoPago>> obtenerPagosPorSplit(@Path("id") int id);

    @GET("/pagos")
    Call<List<ObjetoPago>> obtenerPagos();

    @PUT("pagos/{id}")
    Call<ObjetoPago> actualizarPago(@Path("id") int id, @Body ObjetoPago pagoObjeto);

    @DELETE("pagos/{id}")
    Call<Void> eliminarPago(@Path("id") int id);
}
