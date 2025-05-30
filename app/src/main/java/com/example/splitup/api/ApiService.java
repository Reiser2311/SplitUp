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
    Call<Usuario> obtenerUsuarioPorCorreo(@Path("correo") String correo);

    @GET("usuarios/{id}")
    Call<Usuario> obtenerUsuarioPorId(@Path("id") int id);

    @GET("usuarios")
    Call<List<Usuario>> obtenerUsuarios();

    @PUT("usuarios/{id}")
    Call<Void> actualizarUsuario(@Path("id") int id, @Query("correo") String correo, @Query("nombre") String nombre, @Query("contrasenya") String constrasenya);

    @DELETE("usuarios/{id}")
    Call<Void> eliminarUsuario(@Path("id") int id);

    @POST("splits")
    Call<Split> crearSplit(@Body Split splitObjeto);

    @GET("splits/{id}")
    Call<Split> obtenerSplit(@Path("id") int id);

    @GET("splits")
    Call<List<Split>> obtenerSplits();

    @GET("usuariosplit/splits/{usuarioId}")
    Call<List<Split>> obtenerSplitsPorUsuario(@Path("usuarioId") int usuarioId);

    @PUT("splits/{id}")
    Call<Void> actualizarSplit(@Path("id") int id, @Query("titulo") String titulo);

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

    @GET("participantes/{id}")
    Call<Participante> obtenerParticipante(@Path("id") int id);

    @GET("participantes")
    Call<List<Participante>> obtenerParticipantes();

    @GET("participantes/splits/{id}")
    Call<List<Participante>> obtenerParticipantePorSplit(@Path("id") int id);

    @POST("participantes")
    Call<Participante> crearParticipante(@Body Participante participanteObjeto);

    @PUT("participantes/{id}")
    Call<Void> actualizarParticipante(@Path("id") int id, @Query("nombre") String nombre, @Query("correo") String correo);

    @DELETE("participantes/{id}")
    Call<Void> eliminarParticipante(@Path("id") int id);

    @GET("usuariosplit/splits/{idUsuario}")
    Call<List<Integer>> obtenerSplitsDeUsuario(@Path("idUsuario") int idUsuario);

    @DELETE("usuariosplit/{usuarioId}/{splitId}")
    Call<Void> eliminarUsuarioDeSplit(@Path("usuarioId") int usuarioId, @Path("splitId") int splitId);

    @POST("usuariosplit")
    Call<UsuarioSplit> crearRelacionUsuarioSplit(@Body UsuarioSplit usuarioSplit);

    @GET("usuario_participante/usuario/{usuarioId}")
    Call<List<Integer>> obtenerParticipantesDeUsuario(@Path("usuarioId") int usuarioId);

    @GET("usuario_participante/participante/{participanteId}")
    Call<Integer> obtenerUsuarioDeParticipante(@Path("participanteId") int participanteId);

    @POST("usuarioparticipante")
    Call<UsuarioParticipante> crearRelacionUsuarioParticipante(@Body UsuarioParticipante relacion);




}
