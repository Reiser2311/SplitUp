package com.example.splitup.objetos;

public class UsuarioSplit {
    private Integer usuarioId;
    private Integer splitId;

    public UsuarioSplit(Integer usuarioId, Integer splitId) {
        this.usuarioId = usuarioId;
        this.splitId = splitId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getSplitId() {
        return splitId;
    }

    public void setSplitId(Integer splitId) {
        this.splitId = splitId;
    }
}
