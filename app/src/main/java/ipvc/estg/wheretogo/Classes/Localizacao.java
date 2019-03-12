package ipvc.estg.wheretogo.Classes;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

public class Localizacao {
    private String id;
    private ServiceLocation location;
    private String hora;
    private MyUser tecnico;

    public Localizacao(String id, ServiceLocation location, String hora, MyUser tecnico) {
        this.id = id;
        this.location = location;
        this.hora = hora;
        this.tecnico = tecnico;
    }

    public String getId() {
        return id;
    }

    public ServiceLocation getLocation() {
        return location;
    }

    public String getHora() {
        return hora;
    }

    public MyUser getTecnico() {
        return tecnico;
    }
}
