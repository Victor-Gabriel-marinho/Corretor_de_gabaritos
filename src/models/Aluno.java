package models;

import java.util.ArrayList;
import java.util.List;

public class Aluno {
    private String nome;

    public Aluno (String nome){
        this.nome = nome.trim().toUpperCase();
    }

    public String getNome() {
        return nome;
    }
}
