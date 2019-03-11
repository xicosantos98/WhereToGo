package ipvc.estg.wheretogo.Classes;

public class TipoServico {
    private String id;
    private String nome;
    private double tempoDuracao;

    public TipoServico(String id, String nome, double tempoDuracao) {
        this.id = id;
        this.nome = nome;
        this.tempoDuracao = tempoDuracao;
    }

    public TipoServico() {
    }

    public String getId() {
        return id;
    }


    public String getNome() {
        return nome;
    }

    public double getTempoDuracao() {
        return tempoDuracao;
    }
}
