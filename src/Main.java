import service.GerenciadorDeArquivos;
import service.GeradorResultado;

import java.util.Scanner;

public class Main {

    private static final Scanner scan = new Scanner(System.in);
    private static final GerenciadorDeArquivos gerenciador = new GerenciadorDeArquivos();
    private static final GeradorResultado geradorResultado = new GeradorResultado(scan);

    public static void main(String[] args) {

        while (true) {
            System.out.println("=== CORRETOR DE GABARITOS ===");
            System.out.println("Selecione uma opção:");
            System.out.println("1 - Cadastrar notas");
            System.out.println("2 - Gerar resultado");
            System.out.println("3 - Gerar boletim");
            System.out.println("0 - sair");

            int opcao = scan.nextInt();
            scan.nextLine();

            switch (opcao) {
                case (1): {
                    System.out.println("Digite o nome da disciplina");
                    String nomeDisciplina = scan.nextLine();


                    if (gerenciador.getTurma().isEmpty()) {
                        gerenciador.criarturma();
                    }

                    gerenciador.criargabaritos(nomeDisciplina);
                    break;

                }
                case 2: {
                    geradorResultado.gerar();
                    break;
                }
                case 3: {
                    gerenciador.gerarBoletins();
                    break;
                }
                case (0): {
                    System.out.println("Saindo...");
                    return;
                }
            }


        }
    }

}