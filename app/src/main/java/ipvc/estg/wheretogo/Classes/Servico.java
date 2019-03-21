package ipvc.estg.wheretogo.Classes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Servico {
    private String id;
    private String morada;
    private String descricao;
    private Estado estado;
    private ServiceLocation coordenadas;
    private TipoServico tipo;
    private String contato;
    private String data;
    private String tecnico;

    public Servico(String id, String morada, String descricao, Estado estado, ServiceLocation coordenadas, TipoServico tipo, String contato, String data, String tecnico) {
        this.id = id;
        this.morada = morada;
        this.descricao = descricao;
        this.estado = estado;
        this.coordenadas = coordenadas;
        this.tipo = tipo;
        this.contato = contato;
        this.data = data;
        this.tecnico = tecnico;
    }

    public Servico() {
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

    public String getData() {return data;}

    public String getTecnico() {
        return tecnico;
    }



}
