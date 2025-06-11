package com.example.splitup.objetos;

public class ParticipantePago {
    private Integer participanteId;
    private Integer pagoId;

    public Integer getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Integer participanteId) {
        this.participanteId = participanteId;
    }

    public Integer getPagoId() {
        return pagoId;
    }

    public void setPagoId(Integer pagoId) {
        this.pagoId = pagoId;
    }

    public ParticipantePago(Integer participanteId, Integer pagoId) {
        this.participanteId = participanteId;
        this.pagoId = pagoId;
    }
}
