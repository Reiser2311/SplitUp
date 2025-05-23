package com.example.splitup.objetos;

public class UsuarioParticipante {
    private Integer usuarioId;
    private Integer participanteId;

    public UsuarioParticipante(Integer participanteId, Integer usuarioId) {
        this.participanteId = participanteId;
        this.usuarioId = usuarioId;
    }

    public Integer getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Integer participanteId) {
        this.participanteId = participanteId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
