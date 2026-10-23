package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Disciplina {
    private String nome;
    private final Map<Aluno, String> respostas = new LinkedHashMap<>();

    public  Disciplina (String nome, List<Aluno> alunos){
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void adicionarRespostas(Aluno aluno, String gabarito){
        respostas.put(aluno, gabarito.toUpperCase());
    }
}
