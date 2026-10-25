
package models;

import java.util.ArrayList;
import java.util.List;

public class Boletim {
    private Aluno aluno;
    private List<Nota> notas;
    private float media;
    private String situacao;

    public Boletim(Aluno aluno) {
        this.aluno = aluno;
        this.notas = new ArrayList<>();
        this.media = 0;
        this.situacao = "NÃO DEFINIDO";
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

    public void adicionarNota(Nota nota) {
        notas.add(nota);
    }

    public float calcularMedia() {
        if (notas.isEmpty()) {
            media = 0;
            situacao = "NÃO DEFINIDO";
            return media;
        }

        float soma = 0;

        for (Nota nota : notas) {
            soma += nota.getPontos();
        }

        media = soma / notas.size();

        if (media >= 7) {
            situacao = "APROVADO";
        } else {
            situacao = "REPROVADO";
        }

        return media;
    }

    public String gerarRelatorio() {
        calcularMedia();

        StringBuilder texto = new StringBuilder();

        texto.append("BOLETIM ESCOLAR\n");
        texto.append("Aluno: ")
                .append(aluno.getNome())
                .append("\n\n");

        for (Nota nota : notas) {
            texto.append(nota.getDisciplina().getNome())
                    .append(": ")
                    .append(nota.getPontos())
                    .append("\n");
        }

        texto.append("\nMédia: ")
                .append(String.format("%.2f", media));

        texto.append("\nResultado: ")
                .append(situacao);

        return texto.toString();
    }
}
