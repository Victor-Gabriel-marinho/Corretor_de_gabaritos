package models;

public class Nota {
    private Aluno aluno;
    private Disciplina disciplina;
    private float pontos;

    public Nota (Aluno aluno, Disciplina disciplina, float pontos){
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.pontos = pontos;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public float getPontos() {
        return pontos;
    }
}
