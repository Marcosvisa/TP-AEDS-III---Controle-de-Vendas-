package dao;

import java.io.*;
import java.util.*;

public class IndiceCarroVenda {
    private BPlusTreeCarroVenda indice;
    private final String NOME_ARQUIVO = "dados/carro_vendas_bptree.dat";

    public IndiceCarroVenda() throws Exception {
        indice = new BPlusTreeCarroVenda(NOME_ARQUIVO);
        
        // Tenta carregar do disco se existir
        try {
            indice.loadFromDisk();
        } catch (FileNotFoundException e) {
            // Arquivo não existe ainda - normal na primeira execução
            System.out.println("Criando nova B+ Tree para Carro-Venda...");
        } catch (Exception e) {
            System.err.println("Erro ao carregar B+ Tree: " + e.getMessage());
        }
    }
    
    // Adiciona uma relação carro-venda (N:N)
    public boolean addVendaToCarro(int idCarro, int idVenda) throws Exception {
        try {
            indice.addVendaToCarro(idCarro, idVenda);
            indice.saveToDisk(); // Persiste após cada operação
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar venda ao carro: " + e.getMessage());
            return false;
        }
    }
    
    // Busca todas as vendas de um carro específico
    public List<Integer> getVendasPorCarro(int idCarro) throws Exception {
        try {
            return indice.search(idCarro);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vendas do carro: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Busca todos os carros de uma venda específica
    public List<Integer> getCarrosPorVenda(int idVenda) throws Exception {
        try {
            return indice.getCarrosPorVenda(idVenda);
        } catch (Exception e) {
            System.err.println("Erro ao buscar carros da venda: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Remove uma relação carro-venda
    public boolean removeVendaFromCarro(int idCarro, int idVenda) throws Exception {
        try {
            boolean removed = indice.removeVendaFromCarro(idCarro, idVenda);
            if (removed) {
                indice.saveToDisk();
            }
            return removed;
        } catch (Exception e) {
            System.err.println("Erro ao remover venda do carro: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeCarro(int idCarro) throws Exception {
        try {
            indice.remove(idCarro);
            indice.saveToDisk();
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao remover carro: " + e.getMessage());
            return false;
        }
    }
    
    public void printTree() {
        indice.printTree();
    }
    
    public List<Integer> getAllKeys() {
        return indice.getAllKeys();
    }
    
    public void close() throws Exception {

        indice.saveToDisk();
    }
}