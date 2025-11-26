package dao;
import java.io.*;
import java.util.*;

public class BPlusTreeCarroVenda implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final int ORDER = 4;
    private BPlusTreeNode root;
    private String dataFile;

    private static class BPlusTreeNode implements Serializable {
        private static final long serialVersionUID = 2L;
        boolean isLeaf;
        List<Integer> keys;
        List<List<Integer>> values;
        List<BPlusTreeNode> children;
        transient BPlusTreeNode next;

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
        this.dataFile = dataFile;
        loadFromDisk(); //agora carrega com formato simples
    }

    // ADICIONA uma venda a um carro
    public void addVendaToCarro(int idCarro, int idVenda) {
        List<Integer> vendas = search(idCarro);
        if (vendas.isEmpty()) {
            insert(idCarro, idVenda);
        } else {
            if (!vendas.contains(idVenda)) {
                vendas.add(idVenda);
                update(idCarro, vendas);
            }
        }
        saveToDisk(); //salva com formato simples
    }

    public void saveToDisk() {
        try {
            File file = new File(dataFile);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(dataFile))) {
                saveNode(dos, root);
                System.out.println("B+ Tree salva: " + dataFile);
            }
        } catch (IOException e) {
            System.err.println("ERRO ao salvar B+ Tree: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveNode(DataOutputStream dos, BPlusTreeNode node) throws IOException {
        //tipo do nó
        dos.writeBoolean(node.isLeaf);
        
        //chaves
        dos.writeInt(node.keys.size());
        for (int key : node.keys) {
            dos.writeInt(key);
        }
        
        if (node.isLeaf) {
            // Valores (folha) - lista de vendas para cada carro
            for (List<Integer> vendas : node.values) {
                dos.writeInt(vendas.size());
                for (int venda : vendas) {
                    dos.writeInt(venda);
                }
            }
        } else {
            // Filhos (nó interno) - salva recursivamente
            for (BPlusTreeNode child : node.children) {
                saveNode(dos, child);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromDisk() {
        File file = new File(dataFile);
        if (!file.exists()) {
            System.out.println("Arquivo de índice não encontrado, criando nova B+ Tree: " + dataFile);
            this.root = new BPlusTreeNode(true);
            return;
        }
        
        if (file.length() == 0) {
            System.out.println("Arquivo de índice vazio, criando nova B+ Tree");
            this.root = new BPlusTreeNode(true);
            return;
        }
        
        try {
            //Tenta carregar com formato simples primeiro
            try (DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
                this.root = loadNode(dis);
               
                return;
            } catch (Exception e) {
                System.out.println("⚠️  Formato simples falhou, tentando formato antigo...");
            }
            
            //  Tenta formato antigo (serialização Java)
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                this.root = (BPlusTreeNode) ois.readObject();
                
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado, criando nova B+ Tree");
            this.root = new BPlusTreeNode(true);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ERRO ao carregar B+ Tree (arquivo corrompido), criando nova: " + e.getMessage());
            this.root = new BPlusTreeNode(true);
            
            try {
                if (file.delete()) {
                    System.out.println("Arquivo corrompido deletado: " + dataFile);
                }
            } catch (SecurityException ex) {
                System.err.println("Não foi possível deletar arquivo corrompido: " + ex.getMessage());
            }
        }
    }

    //Carrega nó recursivamente do formato simples
    private BPlusTreeNode loadNode(DataInputStream dis) throws IOException {
        boolean isLeaf = dis.readBoolean();
        BPlusTreeNode node = new BPlusTreeNode(isLeaf);
        
        // Chaves
        int numKeys = dis.readInt();
        for (int i = 0; i < numKeys; i++) {
            node.keys.add(dis.readInt());
        }
        
        if (isLeaf) {
            // Valores (folha) - lista de vendas para cada carro
            for (int i = 0; i < numKeys; i++) {
                int numVendas = dis.readInt();
                List<Integer> vendas = new ArrayList<>();
                for (int j = 0; j < numVendas; j++) {
                    vendas.add(dis.readInt());
                }
                node.values.add(vendas);
            }
        } else {
            // Filhos (nó interno) - carrega recursivamente
            for (int i = 0; i <= numKeys; i++) { // n keys = n+1 children
                node.children.add(loadNode(dis));
            }
        }
        
        return node;
    }

    // ⭐ MÉTODO PARA VISUALIZAR ESTRUTURA DO ARQUIVO
    public void analisarArquivoDB() {
        System.out.println("\n=== ANÁLISE DO ARQUIVO B+ TREE ===");
        System.out.println("Arquivo: " + dataFile);
        
        File file = new File(dataFile);
        if (!file.exists()) {
            System.out.println("❌ Arquivo não existe");
            return;
        }
        
        System.out.println("Tamanho: " + file.length() + " bytes");
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
            System.out.println("📊 Estrutura do arquivo:");
            analisarNode(dis, 0);
        } catch (Exception e) {
            System.out.println("❌ Erro na análise: " + e.getMessage());
        }
    }
    
    private void analisarNode(DataInputStream dis, int nivel) throws IOException {
        String indent = "  ".repeat(nivel);
        boolean isLeaf = dis.readBoolean();
        int numKeys = dis.readInt();
        
        System.out.println(indent + (isLeaf ? "🍃" : "🌳") + " Nível " + nivel + ": " + numKeys + " chaves");
        
        // Lê chaves
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < numKeys; i++) {
            keys.add(dis.readInt());
        }
        System.out.println(indent + "  Chaves: " + keys);
        
        if (isLeaf) {
            // Lê valores (folha)
            for (int i = 0; i < numKeys; i++) {
                int numVendas = dis.readInt();
                List<Integer> vendas = new ArrayList<>();
                for (int j = 0; j < numVendas; j++) {
                    vendas.add(dis.readInt());
                }
                System.out.println(indent + "  Carro " + keys.get(i) + " → Vendas: " + vendas);
            }
        } else {
            // lê filhos
            for (int i = 0; i <= numKeys; i++) {
                analisarNode(dis, nivel + 1);
            }
        }
    }

    // metodos originais
    public void update(int key, List<Integer> newValues) {
        update(root, key, newValues);
    }

    private void update(BPlusTreeNode node, int key, List<Integer> newValues) {
        if (node.isLeaf) {
            for (int i = 0; i < node.keys.size(); i++) {
                if (node.keys.get(i) == key) {
                    node.values.set(i, new ArrayList<>(newValues));
                    return;
                }
            }
        } else {
            int i = 0;
            while (i < node.keys.size() && key >= node.keys.get(i)) {
                i++;
            }
            update(node.children.get(i), key, newValues);
        }
    }

    public void remove(int key) {
        remove(root, key);
    }

    private void remove(BPlusTreeNode node, int key) {
        if (node.isLeaf) {
            for (int i = 0; i < node.keys.size(); i++) {
                if (node.keys.get(i) == key) {
                    node.keys.remove(i);
                    node.values.remove(i);
                    return;
                }
            }
        } else {
            int i = 0;
            while (i < node.keys.size() && key >= node.keys.get(i)) {
                i++;
            }
            remove(node.children.get(i), key);
        }
    }

    // INSERÇÃO
    private void insert(int key, int value) {
        BPlusTreeNode r = root;
        if (r.keys.size() == (2 * ORDER - 1)) {
            BPlusTreeNode s = new BPlusTreeNode(false);
            s.children.add(r);
            splitChild(s, 0);
            root = s;
        }
        insertNonFull(root, key, value);
    }

    private void insertNonFull(BPlusTreeNode node, int key, int value) {
        if (node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key > node.keys.get(i)) {
                i++;
            }
            
            if (i < node.keys.size() && node.keys.get(i) == key) {
                if (!node.values.get(i).contains(value)) {
                    node.values.get(i).add(value);
                }
            } else {
                node.keys.add(i, key);
                List<Integer> novaLista = new ArrayList<>();
                novaLista.add(value);
                node.values.add(i, novaLista);
            }
        } else {
            int i = 0;
            while (i < node.keys.size() && key > node.keys.get(i)) {
                i++;
            }
            BPlusTreeNode child = node.children.get(i);
            
            if (child.keys.size() == (2 * ORDER - 1)) {
                splitChild(node, i);
                if (key > node.keys.get(i)) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(BPlusTreeNode parent, int i) {
        BPlusTreeNode fullChild = parent.children.get(i);
        BPlusTreeNode newChild = new BPlusTreeNode(fullChild.isLeaf);
        
        int mid = ORDER - 1;
        parent.keys.add(i, fullChild.keys.get(mid));
        
        if (fullChild.isLeaf) {
            newChild.keys.addAll(fullChild.keys.subList(mid, fullChild.keys.size()));
            newChild.values.addAll(fullChild.values.subList(mid, fullChild.values.size()));
            fullChild.keys.subList(mid, fullChild.keys.size()).clear();
            fullChild.values.subList(mid, fullChild.values.size()).clear();
            
            newChild.next = fullChild.next;
            fullChild.next = newChild;
        } else {
            newChild.keys.addAll(fullChild.keys.subList(mid + 1, fullChild.keys.size()));
            newChild.children.addAll(fullChild.children.subList(mid + 1, fullChild.children.size()));
            fullChild.keys.subList(mid, fullChild.keys.size()).clear();
            fullChild.children.subList(mid + 1, fullChild.children.size()).clear();
        }
        
        parent.children.add(i + 1, newChild);
    }

    // BUSCA
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

    // BUSCA todos os carros de uma venda
    public List<Integer> getCarrosPorVenda(int idVenda) {
        List<Integer> carros = new ArrayList<>();
        collectCarrosPorVenda(root, idVenda, carros);
        return carros;
    }

    private void collectCarrosPorVenda(BPlusTreeNode node, int idVenda, List<Integer> carros) {
        if (node.isLeaf) {
            for (int i = 0; i < node.values.size(); i++) {
                if (node.values.get(i).contains(idVenda)) {
                    carros.add(node.keys.get(i));
                }
            }
            if (node.next != null) {
                collectCarrosPorVenda(node.next, idVenda, carros);
            }
        } else {
            for (BPlusTreeNode child : node.children) {
                collectCarrosPorVenda(child, idVenda, carros);
            }
        }
    }

    // BUSCA todas as vendas de um carro
    public List<Integer> getVendasPorCarro(int idCarro) {
        return search(idCarro);
    }

    public void close() {
        saveToDisk();
    }

    public void printTree() {
        printTree(root, 0);
    }
    
    private void printTree(BPlusTreeNode node, int level) {
        if (node == null) return;
        
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        
        System.out.println(indent + "Level " + level + ": " + node.keys + " - " + node.values);
        
        if (!node.isLeaf && node.children != null) {
            for (BPlusTreeNode child : node.children) {
                printTree(child, level + 1);
            }
        }
    }
}