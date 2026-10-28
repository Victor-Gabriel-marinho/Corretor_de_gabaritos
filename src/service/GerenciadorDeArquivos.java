
package service;

import models.Aluno;
import models.Boletim;
import models.Disciplina;
import models.Nota;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.InputMismatchException;

public class GerenciadorDeArquivos {

    private Scanner scan = new Scanner(System.in);

    private List<Disciplina> disciplinas = new ArrayList<>();
    private List<Aluno> turma = new ArrayList<>();

    private File BaseDirGabaritos =
            new File("src/dados/gabaritos");

    private File BaseDirProvas =
            new File("src/dados/prova");

    private File BaseDirResultados =
            new File("src/dados/resultados");

    private File BaseDirBoletins =
            new File("src/dados/boletins");


    public void criargabaritos(String nomeDisciplina) {

        Disciplina disciplina =
                new Disciplina(nomeDisciplina, new ArrayList<>());

        BaseDirGabaritos.mkdirs();

        File novoGabarito = new File(
                BaseDirGabaritos,
                nomeDisciplina + ".txt"
        );

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(novoGabarito, true))) {

            for (Aluno aluno : turma) {

                String gabarito;

                do {
                    System.out.println(
                            "Digite as respostas do aluno "
                                    + aluno.getNome()
                                    + " (ex.: VFVFFVVFVF)"
                    );

                    gabarito = scan.nextLine().trim().toUpperCase();

                    if (gabarito.length() != 10) {
                        System.out.println(
                                "Erro: digite exatamente 10 caracteres (você digitou "
                                        + gabarito.length() + ")."
                        );
                    } else if (!gabarito.matches("[VF]{10}")) {
                        System.out.println("Erro: use apenas as letras V e F.");
                    }

                } while (!gabarito.matches("[VF]{10}"));

                disciplina.adicionarRespostas(aluno, gabarito);

                bw.write(gabarito + "\t" + aluno.getNome().trim().toUpperCase());
                bw.newLine();
            }

            disciplinas.add(disciplina);

        } catch (IOException e) {
            System.out.println(
                    "Erro ao escrever as respostas: " + e.getMessage()
            );
        }
    }


    public void criarturma() {
        int qtdAlunos = 0;
        while (qtdAlunos < 0 || qtdAlunos == 0) {
            try {
                System.out.println("Digite o número de alunos");
                qtdAlunos = scan.nextInt();

                if (qtdAlunos<= 0){
                    System.out.println("Digite uma quantidade váilida");
                }

                scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Digite um número válido!");
                scan.nextLine();
            }

        }
        for (int i = 0; i < qtdAlunos; i++) {

            System.out.println(
                    "Digite o nome do aluno " + (i + 1)
            );

            String nome = scan.nextLine();

            Aluno aluno = new Aluno(nome);

            turma.add(aluno);
        }
    }


    public List<Aluno> getTurma() {
        return this.turma;
    }


    public void gerarBoletins() {

        Map<String, Boletim> boletins = new TreeMap<>();

        try {

            if (!BaseDirResultados.exists()) {
                System.out.println(
                        "A pasta de resultados ainda não existe."
                );
                return;
            }

            Files.createDirectories(BaseDirBoletins.toPath());

            try (var arquivos = Files.list(BaseDirResultados.toPath())) {

                arquivos
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".txt"))
                        .forEach(arquivo -> {

                            String nomeDisciplina = arquivo.getFileName()
                                    .toString()
                                    .replaceFirst("\\.txt$", "");

                            Disciplina disciplina =
                                    new Disciplina(
                                            nomeDisciplina,
                                            new ArrayList<>()
                                    );

                            try {

                                List<String> linhas =
                                        Files.readAllLines(arquivo);

                                for (String linha : linhas) {

                                    String[] partes =
                                            linha.split("\\t", 2);

                                    if (partes.length != 2) {
                                        continue;
                                    }

                                    String nomeAluno =
                                            partes[0].trim().toUpperCase();

                                    try {

                                        float pontos = Float.parseFloat(
                                                partes[1].trim()
                                                        .replace(",", ".")
                                        );

                                        Boletim boletim =
                                                boletins.computeIfAbsent(
                                                        nomeAluno,
                                                        nome -> new Boletim(
                                                                new Aluno(nome)
                                                        )
                                                );

                                        Nota nota = new Nota(
                                                boletim.getAluno(),
                                                disciplina,
                                                pontos
                                        );

                                        boletim.adicionarNota(nota);

                                    } catch (NumberFormatException e) {

                                        System.out.println(
                                                "Nota inválida no arquivo "
                                                        + arquivo.getFileName()
                                                        + ": " + linha
                                        );
                                    }
                                }

                            } catch (IOException e) {

                                throw new UncheckedIOException(e);
                            }
                        });
            }

            for (Boletim boletim : boletins.values()) {

                String nomeArquivo =
                        boletim.getAluno().getNome()
                                .replaceAll("[\\\\/:*?\"<>|]", "_");

                Path destino = Paths.get(
                        BaseDirBoletins.getPath(),
                        nomeArquivo + ".txt"
                );

                Files.writeString(
                        destino,
                        boletim.gerarRelatorio()
                );
            }

            System.out.println(
                    boletins.size() + " boletim(ns) gerado(s) em: "
                            + BaseDirBoletins.getPath()
            );

        } catch (IOException | UncheckedIOException e) {

            System.out.println(
                    "Erro ao gerar boletins: " + e.getMessage()
            );
        }
    }
}
