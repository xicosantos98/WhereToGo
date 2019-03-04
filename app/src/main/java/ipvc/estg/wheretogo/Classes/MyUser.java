package ipvc.estg.wheretogo.Classes;

public class MyUser {
    private String id;
    private String nome;
    private String email;
    private String contato;
    private TipoUser tipo;
    private String token;

    public MyUser(String id, String nome, String email, String contato, TipoUser tipo, String token) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.contato = contato;
        this.tipo = tipo;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getContato() {
        return contato;
    }

    public TipoUser getTipo() {
        return tipo;
    }

    public String getToken() {
        return token;
    }
}
