package service;

import models.Aluno;
import models.Disciplina;
import models.Nota;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class GeradorResultado {

    private static final int QTD_QUESTOES = 10;

    // Mesmas pastas usadas pelo GerenciadorDeArquivos
    private static final File DIR_RESPOSTAS = new File("src/dados/gabaritos");
    private static final File DIR_RESULTADOS = new File("src/dados/resultados");
    // Subpasta: o gerador de boletim lê só os arquivos diretos de "resultados"
    private static final File DIR_POR_NOTA = new File(DIR_RESULTADOS, "por_nota");
    // Gabaritos oficiais criados pela opção 2 do submenu (um .txt por disciplina)
    private static final File DIR_OFICIAIS = new File("src/dados/oficiais");

    private final Scanner scan;

    public GeradorResultado(Scanner scan) {
        this.scan = scan;
    }

    // Ponto de entrada chamado pela opção 2 da Main: abre o submenu
    public void gerar() {
        while (true) {
            System.out.println("\n=== RESULTADO ===");
            System.out.println("1 - Gerar resultado");
            System.out.println("2 - Criar gabarito oficial");
            System.out.println("0 - Voltar");

            String opcao = scan.nextLine().trim();
            switch (opcao) {
                case "1":
                    gerarResultado();
                    break;
                case "2":
                    criarGabaritoOficial();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void gerarResultado() {
        List<String> disciplinas = listarDisciplinas();
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada. Use a opção 1 primeiro.");
            return;
        }

        String nomeDisciplina = escolherDisciplina(disciplinas);

        File oficial = new File(DIR_OFICIAIS, nomeDisciplina + ".txt");
        if (oficial.exists()) {
            System.out.println("Informe o caminho do arquivo com o gabarito oficial");
            System.out.println("(ou aperte Enter para usar o que você criou: " + oficial.getPath() + "):");
        } else {
            System.out.println("Informe o caminho do arquivo com o gabarito oficial:");
        }
        String caminho = scan.nextLine().trim().replace("\"", "");
        if (caminho.isEmpty() && oficial.exists()) {
            caminho = oficial.getPath();
        }
        if (caminho.isEmpty()) {
            System.out.println("Nenhum caminho informado.");
            return;
        }

        try {
            String gabaritoOficial = lerGabaritoOficial(Paths.get(caminho));
            Map<String, String> respostas = lerRespostasDosAlunos(nomeDisciplina);

            List<Nota> notas = corrigir(nomeDisciplina, respostas, gabaritoOficial);
            if (notas.isEmpty()) {
                System.out.println("Nenhum aluno encontrado nessa disciplina.");
                return;
            }

            List<Nota> alfabetica = new ArrayList<>(notas);
            alfabetica.sort(Comparator.comparing((Nota n) -> n.getAluno().getNome()));

            List<Nota> porNota = new ArrayList<>(notas);
            porNota.sort(Comparator.comparingDouble(Nota::getPontos).reversed()
                    .thenComparing((Nota n) -> n.getAluno().getNome()));

            double media = calcularMedia(notas);

            gravar(alfabetica, new File(DIR_RESULTADOS, nomeDisciplina + ".txt"), null);
            gravar(porNota, new File(DIR_POR_NOTA, nomeDisciplina + ".txt"), media);

            exibir("ORDEM ALFABÉTICA", alfabetica, null);
            exibir("POR NOTA (DECRESCENTE)", porNota, media);

        } catch (IOException e) {
            System.out.println("Erro ao gerar o resultado: " + e.getMessage());
        }
    }

    // Cria o arquivo txt do gabarito oficial: uma linha com as 10 respostas
    private void criarGabaritoOficial() {
        List<String> disciplinas = listarDisciplinas();
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada. Use a opção 1 primeiro.");
            return;
        }

        String nomeDisciplina = escolherDisciplina(disciplinas);

        String gabarito;
        while (true) {
            System.out.println("Digite as " + QTD_QUESTOES + " respostas oficiais (V ou F), ou Enter para cancelar:");
            String linha = scan.nextLine().trim();
            if (linha.isEmpty()) {
                System.out.println("Cancelado.");
                return;
            }
            gabarito = limpar(linha);
            if (gabarito.length() == QTD_QUESTOES) {
                break;
            }
            System.out.println("São necessárias exatamente " + QTD_QUESTOES + " respostas V ou F.");
        }

        File destino = new File(DIR_OFICIAIS, nomeDisciplina + ".txt");
        try {
            Files.createDirectories(DIR_OFICIAIS.toPath());
            Files.write(destino.toPath(), Collections.singletonList(gabarito));
            System.out.println("Gabarito oficial de " + nomeDisciplina + " salvo em:");
            System.out.println(destino.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar o gabarito oficial: " + e.getMessage());
        }
    }

    // Disciplinas = arquivos .txt da pasta onde a opção 1 grava as respostas
    private List<String> listarDisciplinas() {
        List<String> nomes = new ArrayList<>();
        File[] arquivos = DIR_RESPOSTAS.listFiles((dir, nome) -> nome.endsWith(".txt"));
        if (arquivos != null) {
            Arrays.sort(arquivos, Comparator.comparing((File f) -> f.getName().toLowerCase()));
            for (File f : arquivos) {
                nomes.add(f.getName().replaceFirst("\\.txt$", ""));
            }
        }
        return nomes;
    }

    private String escolherDisciplina(List<String> disciplinas) {
        System.out.println("Escolha a disciplina:");
        for (int i = 0; i < disciplinas.size(); i++) {
            System.out.println((i + 1) + " - " + disciplinas.get(i));
        }
        while (true) {
            try {
                int escolha = Integer.parseInt(scan.nextLine().trim());
                if (escolha >= 1 && escolha <= disciplinas.size()) {
                    return disciplinas.get(escolha - 1);
                }
            } catch (NumberFormatException e) {
                // cai na mensagem abaixo
            }
            System.out.println("Opção inválida, tente novamente.");
        }
    }

    // O arquivo tem apenas uma linha com as 10 respostas corretas
    private String lerGabaritoOficial(Path arquivo) throws IOException {
        if (!Files.exists(arquivo)) {
            throw new IOException("arquivo não encontrado: " + arquivo);
        }
        List<String> linhas = Files.readAllLines(arquivo);
        String gabarito = linhas.isEmpty() ? "" : limpar(linhas.get(0));
        if (gabarito.length() != QTD_QUESTOES) {
            throw new IOException("o gabarito deve ter " + QTD_QUESTOES + " respostas (V ou F)");
        }
        return gabarito;
    }

    // Formato gravado pelo GerenciadorDeArquivos.criargabaritos:  RESPOSTAS <tab> NOME
    // Mapa por nome: se o aluno aparece duas vezes (arquivo em modo append), vale a última.
    private Map<String, String> lerRespostasDosAlunos(String nomeDisciplina) throws IOException {
        Map<String, String> respostas = new LinkedHashMap<>();
        Path arquivo = new File(DIR_RESPOSTAS, nomeDisciplina + ".txt").toPath();

        for (String linha : Files.readAllLines(arquivo)) {
            String[] partes = linha.split("\t", 2);
            if (partes.length != 2 || partes[1].trim().isEmpty()) {
                continue;
            }
            respostas.put(partes[1].trim().toUpperCase(), limpar(partes[0]));
        }
        return respostas;
    }

    // Usa Disciplina, Aluno e Nota dos colegas, do jeito que eles foram escritos
    private List<Nota> corrigir(String nomeDisciplina, Map<String, String> respostas, String gabarito) {
        Disciplina disciplina = new Disciplina(nomeDisciplina, new ArrayList<>());
        List<Nota> notas = new ArrayList<>();
        for (Map.Entry<String, String> e : respostas.entrySet()) {
            int acertos = calcularAcertos(e.getValue(), gabarito);
            notas.add(new Nota(new Aluno(e.getKey()), disciplina, acertos));
        }
        return notas;
    }

    private int calcularAcertos(String respostasAluno, String gabarito) {
        // Marcou tudo V ou tudo F: nota 0
        if (respostasAluno.matches("V+|F+")) {
            return 0;
        }
        int acertos = 0;
        for (int i = 0; i < gabarito.length() && i < respostasAluno.length(); i++) {
            if (respostasAluno.charAt(i) == gabarito.charAt(i)) {
                acertos++;
            }
        }
        return acertos;
    }

    private String limpar(String s) {
        return s.toUpperCase().replaceAll("[^VF]", "");
    }

    private double calcularMedia(List<Nota> notas) {
        double soma = 0;
        for (Nota nota : notas) {
            soma += nota.getPontos();
        }
        return soma / notas.size();
    }

    // media == null: não escreve a linha da média
    private void gravar(List<Nota> notas, File destino, Double media) throws IOException {
        Files.createDirectories(destino.getParentFile().toPath());

        List<String> linhas = new ArrayList<>();
        for (Nota nota : notas) {
            linhas.add(nota.getAluno().getNome() + "\t" + (int) nota.getPontos());
        }
        if (media != null) {
            linhas.add("");
            linhas.add(String.format("MÉDIA DA TURMA: %.2f", media));
        }
        Files.write(destino.toPath(), linhas);
    }

    private void exibir(String titulo, List<Nota> notas, Double media) {
        System.out.println("\n=== " + titulo + " ===");
        for (Nota nota : notas) {
            System.out.println(nota.getAluno().getNome() + " - " + (int) nota.getPontos());
        }
        if (media != null) {
            System.out.println(String.format("MÉDIA DA TURMA: %.2f", media));
        }
        System.out.println();
    }
}
