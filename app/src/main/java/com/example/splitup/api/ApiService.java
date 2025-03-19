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
import retrofit2.http.Query;

public interface ApiService {
    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuarioObjeto);

    @GET("usuarios/{correo}")
    Call<Usuario> obtenerUsuario(@Path("correo") String correo);

    @GET("usuarios")
    Call<List<Usuario>> obtenerUsuarios();

    @PUT("usuarios/{correo}")
    Call<Void> actualizarUsuario(@Path("correo") String correo, @Query("nombre") String nombre, @Query("contrasenya") String constrasenya);

    @DELETE("usuarios/{correo}")
    Call<Void> eliminarUsuario(@Path("correo") String correo);

    @POST("splits")
    Call<Split> crearSplit(@Body Split splitObjeto);

    @GET("splits/{id}")
    Call<Split> obtenerSplit(@Path("id") int id);

    @GET("splits")
    Call<List<Split>> obtenerSplits();

    @GET("splits/usuarios/{correo}")
    Call<List<Split>> obtenerSplitsPorUsuario(@Path("correo") String correo);

    @PUT("splits/{id}")
    Call<Void> actualizarSplit(@Path("id") int id, @Query("titulo") String titulo, @Query("participantes") List<String> participantes);

    @DELETE("splits/{id}")
    Call<Void> eliminarSplit(@Path("id") int id);

    @POST("pagos")
    Call<Pago> crearPago(@Body Pago pagoObjeto);

    @GET("pagos/{id}")
    Call<Pago> obtenerPago(@Path("id") int id);

    @GET("pagos/splits/{id}")
    Call<List<Pago>> obtenerPagosPorSplit(@Path("id") int id);

    @GET("pagos")
    Call<List<Pago>> obtenerPagos();

    @PUT("pagos/{id}")
    Call<Void> actualizarPago(@Path("id") int id, @Query("titulo") String titulo, @Query("importe") Double importe, @Query("pagadoPor") String pagadoPor);

    @DELETE("pagos/{id}")
    Call<Void> eliminarPago(@Path("id") int id);
}
