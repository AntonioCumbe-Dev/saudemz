package com.saudemz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sequencia_consulta")
public class SequenciaConsulta {

    @Id
    @Column(name = "data_codigo", length = 4, nullable = false)
    private String dataCodigo;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero;

    public SequenciaConsulta() {
    }

    public SequenciaConsulta(String dataCodigo, Integer ultimoNumero) {
        this.dataCodigo = dataCodigo;
        this.ultimoNumero = ultimoNumero;
    }

    public String getDataCodigo() {
        return dataCodigo;
    }

    public void setDataCodigo(String dataCodigo) {
        this.dataCodigo = dataCodigo;
    }

    public Integer getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(Integer ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }
}