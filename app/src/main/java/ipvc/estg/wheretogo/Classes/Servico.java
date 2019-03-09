package ipvc.estg.wheretogo.Classes;

import java.time.LocalDate;
import java.util.Date;

public class Servico {
    private String id;
    private String morada;
    private String descricao;
    private Estado estado;
    private ServiceLocation coordenadas;
    private TipoServico tipo;
    private String contato;
    private String horaPrevista;
    private boolean atrasado;
    private MyUser tecnico;

    public Servico(String id, String morada, String descricao, Estado estado, ServiceLocation coordenadas, TipoServico tipo, String contato, String horaPrevista, boolean atrasado, MyUser tecnico) {
        this.id = id;
        this.morada = morada;
        this.descricao = descricao;
        this.estado = estado;
        this.coordenadas = coordenadas;
        this.tipo = tipo;
        this.contato = contato;
        this.horaPrevista = horaPrevista;
        this.atrasado = atrasado;
        this.tecnico = tecnico;
    }

    public String getId() {
        return id;
    }

    public String getMorada() {
        return morada;
    }

    public String getDescricao() {
        return descricao;
    }

    public Estado getEstado() {
        return estado;
    }

    public ServiceLocation getCoordenadas() {
        return coordenadas;
    }

    public TipoServico getTipo() {
        return tipo;
    }

    public String getContato() {
        return contato;
    }

    public String getHoraPrevista() {
        return horaPrevista;
    }

    public boolean isAtrasado() {
        return atrasado;
    }

    public MyUser getTecnico() {
        return tecnico;
    }
}
