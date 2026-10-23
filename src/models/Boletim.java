package models;

import java.util.List;

public class Boletim {
    private Aluno aluno;
    private List<Nota> notas;
    private float media;
    private String situacao;

    public Boletim (Aluno aluno, List<Nota> notas, float media, String situacao) {
        this.aluno = aluno;
        this.notas = notas;
        this.media = media;
        this.situacao = situacao;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public float getMedia() {
        return media;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public String getSituacao() {
        return situacao;
    }
}
