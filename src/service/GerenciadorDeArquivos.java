package service;

import models.Aluno;
import models.Disciplina;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class GerenciadorDeArquivos {

    private Scanner scan = new Scanner(System.in);
    private List<Disciplina> disciplinas;
    private List<Aluno> turma = new ArrayList<>();
    private File BaseDirGabaritos = new File("src/dados/gabaritos");
    private File BaseDirProvas = new File("src/dados/prova");


    public void criargabaritos(String nomeDisciplina) {

        Disciplina disciplina = new Disciplina(nomeDisciplina, new ArrayList<>());
        File novoGabarito = new File(BaseDirGabaritos + File.separator + nomeDisciplina + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(novoGabarito, true))) {

            for (Aluno aluno : turma) {

                System.out.println("Digite o gabarito do aluno "+ aluno.getNome() +" (VFVFFVVFVF)");
                String gabarito = scan.nextLine();

                disciplina.adicionarRespostas(aluno, gabarito);

                bw.write(gabarito.toUpperCase() + "\t" + aluno.getNome().trim().toUpperCase());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Erro ao escrever o gabarito: " + e.getMessage());
        }
    }

    public void criarturma(int quantidadeDeAlunos) {
        for (int i = 0; i < quantidadeDeAlunos; i++) {

            System.out.println("Digite o nome do aluno "+ i+1);
            String nome = scan.nextLine();

            Aluno aluno = new Aluno(nome);
            turma.add(aluno);
        }
    }

    public List<Aluno> getTurma() {
        return this.turma;
    }

}
