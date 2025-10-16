package dao;

import java.io.*;
import java.util.*;

public class BPlusTreeCarroVenda {
    private static final int ORDER = 4;
    private BPlusTreeNode root;
    private String dataFile;
    private BPlusTreeNode firstLeaf; // Ponteiro para a primeira folha

    private class BPlusTreeNode implements Serializable {
        boolean isLeaf;
        List<Integer> keys;
        List<List<Integer>> values; // Lista de IDs de Vendas para cada carro
        List<BPlusTreeNode> children;
        BPlusTreeNode next; // Próxima folha (apenas para nós folha)
        BPlusTreeNode parent;

        BPlusTreeNode(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
            if (!isLeaf) {
                this.children = new ArrayList<>();
            }
        }
    }

    public BPlusTreeCarroVenda(String dataFile) {
        this.root = new BPlusTreeNode(true);
        this.firstLeaf = root;
        this.dataFile = dataFile;
    }

    // ADICIONA uma venda a um carro (relação N:N)
    public void addVendaToCarro(int idCarro, int idVenda) {
        List<Integer> vendas = search(idCarro);
        if (vendas.isEmpty()) {
            // Primeira venda para este carro - insere nova entrada
            insert(idCarro, idVenda);
        } else {
            // Carro já existe - adiciona à lista existente
            if (!vendas.contains(idVenda)) {
                vendas.add(idVenda);
                update(idCarro, vendas);
            }
        }
    }

    // INSERÇÃO de nova relação carro-venda
    private void insert(int key, int value) {
        BPlusTreeNode leaf = findLeaf(key);
        
        // Verifica se a chave já existe
        int pos = 0;
        while (pos < leaf.keys.size() && leaf.keys.get(pos) < key) {
            pos++;
        }
        
        if (pos < leaf.keys.size() && leaf.keys.get(pos) == key) {
            // Chave existe - adiciona à lista existente
            if (!leaf.values.get(pos).contains(value)) {
                leaf.values.get(pos).add(value);
            }
        } else {
            // Nova chave
            leaf.keys.add(pos, key);
            List<Integer> newValueList = new ArrayList<>();
            newValueList.add(value);
            leaf.values.add(pos, newValueList);
        }
        
        if (leaf.keys.size() > ORDER - 1) {
            splitLeaf(leaf);
        }
    }

    // BUSCA todas as vendas de um carro
    public List<Integer> search(int idCarro) {
        return search(root, idCarro);
    }

    private List<Integer> search(BPlusTreeNode node, int key) {
        if (node.isLeaf) {
            for (int i = 0; i < node.keys.size(); i++) {
                if (node.keys.get(i) == key) {
                    return new ArrayList<>(node.values.get(i));
                }
            }
            return new ArrayList<>();
        }
        
        int i = 0;
        while (i < node.keys.size() && key >= node.keys.get(i)) {
            i++;
        }
        return search(node.children.get(i), key);
    }

    // ATUALIZA a lista de vendas de um carro
    public void update(int idCarro, List<Integer> vendas) {
        BPlusTreeNode leaf = findLeaf(idCarro);
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == idCarro) {
                leaf.values.set(i, new ArrayList<>(vendas));
                return;
            }
        }
    }

    // REMOVE uma relação carro-venda
    public void remove(int idCarro) {
        BPlusTreeNode leaf = findLeaf(idCarro);
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i) == idCarro) {
                leaf.keys.remove(i);
                leaf.values.remove(i);
                
                // Se for a raiz e ficar vazia, não faz nada
                if (leaf == root) {
                    return;
                }
                
                // Verifica underflow
                if (leaf.keys.size() < (ORDER - 1) / 2) {
                    handleUnderflow(leaf);
                }
                return;
            }
        }
    }

    // BUSCA todos os carros de uma venda (outro lado da relação N:N)
    public List<Integer> getCarrosPorVenda(int idVenda) {
        List<Integer> carros = new ArrayList<>();
        BPlusTreeNode current = firstLeaf;
        
        while (current != null) {
            for (int i = 0; i < current.values.size(); i++) {
                if (current.values.get(i).contains(idVenda)) {
                    carros.add(current.keys.get(i));
                }
            }
            current = current.next;
        }
        return carros;
    }

    // Encontra a folha onde a chave deve estar
    private BPlusTreeNode findLeaf(int key) {
        return findLeaf(root, key);
    }

    private BPlusTreeNode findLeaf(BPlusTreeNode node, int key) {
        if (node.isLeaf) {
            return node;
        }
        
        int i = 0;
        while (i < node.keys.size() && key >= node.keys.get(i)) {
            i++;
        }
        return findLeaf(node.children.get(i), key);
    }

    // Divide uma folha
    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = leaf.keys.size() / 2;
        
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);
        
        // Move metade das chaves e valores para a nova folha
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.values.addAll(leaf.values.subList(mid, leaf.values.size()));
        
        // Remove as chaves e valores movidos da folha original
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.values.subList(mid, leaf.values.size()).clear();
        
        // Atualiza ponteiros das folhas
        newLeaf.next = leaf.next;
        leaf.next = newLeaf;
        
        // Insere a chave do meio no pai
        insertIntoParent(leaf, newLeaf.keys.get(0), newLeaf);
    }

    // Divide um nó interno
    private void splitInternal(BPlusTreeNode node) {
        int mid = node.keys.size() / 2;
        int promoteKey = node.keys.get(mid);
        
        BPlusTreeNode newInternal = new BPlusTreeNode(false);
        
        // Move metade das chaves e filhos para o novo nó
        newInternal.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
        newInternal.children.addAll(node.children.subList(mid + 1, node.children.size()));
        
        // Remove as chaves e filhos movidos do nó original
        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();
        
        // Atualiza pais dos filhos movidos
        for (BPlusTreeNode child : newInternal.children) {
            child.parent = newInternal;
        }
        
        // Insere no pai
        if (node == root) {
            createNewRoot(promoteKey, node, newInternal);
        } else {
            insertIntoParent(node, promoteKey, newInternal);
        }
    }

    // Insere no nó pai
    private void insertIntoParent(BPlusTreeNode left, int key, BPlusTreeNode right) {
        BPlusTreeNode parent = left.parent;
        
        if (parent == null) {
            createNewRoot(key, left, right);
            return;
        }
        
        // Encontra a posição para inserir
        int pos = 0;
        while (pos < parent.keys.size() && parent.keys.get(pos) < key) {
            pos++;
        }
        
        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);
        right.parent = parent;
        
        if (parent.keys.size() > ORDER - 1) {
            splitInternal(parent);
        }
    }

    // Cria nova raiz
    private void createNewRoot(int key, BPlusTreeNode left, BPlusTreeNode right) {
        BPlusTreeNode newRoot = new BPlusTreeNode(false);
        newRoot.keys.add(key);
        newRoot.children.add(left);
        newRoot.children.add(right);
        
        left.parent = newRoot;
        right.parent = newRoot;
        
        root = newRoot;
    }

    // Trata underflow (fusão ou redistribuição)
    private void handleUnderflow(BPlusTreeNode node) {
        // Implementação simplificada - em produção, implementar fusão e redistribuição
        // Por enquanto, apenas aceita underflow para simplificar
    }

    // REMOVE uma relação específica carro-venda
    public boolean removeVendaFromCarro(int idCarro, int idVenda) {
        List<Integer> vendas = search(idCarro);
        if (vendas.contains(idVenda)) {
            vendas.remove((Integer) idVenda);
            if (vendas.isEmpty()) {
                remove(idCarro);
            } else {
                update(idCarro, vendas);
            }
            return true;
        }
        return false;
    }

    // LISTA todas as relações
    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(BPlusTreeNode node, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        
        if (node.isLeaf) {
            System.out.println(indent + "Leaf: " + node.keys + " -> " + node.values);
        } else {
            System.out.println(indent + "Internal: " + node.keys);
            for (BPlusTreeNode child : node.children) {
                printTree(child, level + 1);
            }
        }
    }

    // Obtém todas as chaves (para debug)
    public List<Integer> getAllKeys() {
        List<Integer> keys = new ArrayList<>();
        BPlusTreeNode current = firstLeaf;
        
        while (current != null) {
            keys.addAll(current.keys);
            current = current.next;
        }
        return keys;
    }

    // Persistência
    public void saveToDisk() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
             new FileOutputStream(dataFile))) {
            oos.writeObject(root);
            oos.writeObject(firstLeaf);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromDisk() throws IOException, ClassNotFoundException {
        File file = new File(dataFile);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                 new FileInputStream(dataFile))) {
                root = (BPlusTreeNode) ois.readObject();
                firstLeaf = (BPlusTreeNode) ois.readObject();
            }
        }
    }
}