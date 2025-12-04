package util;
import java.io.*;
import java.util.*;

public class CompressorHuffman implements Compressor {
    
    // Classe interna para os nós da árvore Huffman
    private static class NoHuffman implements Comparable<NoHuffman>, Serializable {
        byte byteVal;
        int frequencia;
        NoHuffman esquerda, direita;
        
        NoHuffman(byte byteVal, int frequencia) {
            this.byteVal = byteVal;
            this.frequencia = frequencia;
        }
        
        NoHuffman(int frequencia, NoHuffman esquerda, NoHuffman direita) {
            this.frequencia = frequencia;
            this.esquerda = esquerda;
            this.direita = direita;
        }
        
        boolean isFolha() {
            return esquerda == null && direita == null;
        }
        
        @Override
        public int compareTo(NoHuffman outro) {
            return this.frequencia - outro.frequencia;
        }
    }
    
    @Override
    public void comprimir(String[] arquivosEntrada, String arquivoSaida) throws Exception {
        System.out.println("Comprimindo com Huffman...");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoSaida))) {
            // Escreve cabeçalho identificando o método
            oos.writeUTF("HUFFMAN");
            oos.writeInt(arquivosEntrada.length);
            
            for (String arquivo : arquivosEntrada) {
                File file = new File(arquivo);
                if (!file.exists()) continue;
                
                // Lê o arquivo original
                byte[] dados = lerArquivo(arquivo);
                
                // Comprime com Huffman
                byte[] dadosComprimidos = comprimirHuffman(dados);
                
                // Escreve no arquivo compactado
                oos.writeUTF(file.getName());
                oos.writeInt(dados.length); // tamanho original
                oos.writeInt(dadosComprimidos.length); // tamanho comprimido
                oos.write(dadosComprimidos);
                
                System.out.println(" - " + file.getName() + ": " + dados.length + " > " + dadosComprimidos.length + " bytes");
            }
        }
    }
    
    @Override
    public void descomprimir(String arquivoEntrada, String pastaSaida) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoEntrada))) {
            String metodo = ois.readUTF();
            if (!metodo.equals("HUFFMAN")) {
                throw new Exception("Arquivo não compatível com Huffman");
            }
            
            int numArquivos = ois.readInt();
            System.out.println("Descomprimindo " + numArquivos + " arquivos...");
            
            for (int i = 0; i < numArquivos; i++) {
                String nomeArquivo = ois.readUTF();
                int tamanhoOriginal = ois.readInt();
                int tamanhoComprimido = ois.readInt();
                byte[] dadosComprimidos = new byte[tamanhoComprimido];
                ois.readFully(dadosComprimidos);
                
                // Descomprime
                byte[] dadosOriginais = descomprimirHuffman(dadosComprimidos, tamanhoOriginal);
                
                // Salva o arquivo
                String caminhoCompleto = pastaSaida + File.separator + nomeArquivo;
                salvarArquivo(caminhoCompleto, dadosOriginais);
                
                System.out.println(" - " + nomeArquivo + ": " + tamanhoComprimido + " → " + dadosOriginais.length + " bytes");
            }
        }
    }
    
    private byte[] comprimirHuffman(byte[] dados) {
        if (dados.length == 0) return new byte[0];
        
        // 1. Calcular frequências
        Map<Byte, Integer> frequencias = calcularFrequencias(dados);
        
        // 2. Construir árvore Huffman
        NoHuffman raiz = construirArvoreHuffman(frequencias);
        
        // 3. Gerar códigos Huffman
        Map<Byte, String> codigosHuffman = gerarCodigosHuffman(raiz);
        
        // 4. Codificar dados
        StringBuilder bitsCodificados = new StringBuilder();
        for (byte b : dados) {
            bitsCodificados.append(codigosHuffman.get(b));
        }
        
        // 5. Converter bits para bytes
        byte[] dadosComprimidos = bitsParaBytes(bitsCodificados.toString());
        
        // 6. Serializar árvore Huffman e dados comprimidos
        return serializarDadosComprimidos(raiz, dadosComprimidos, bitsCodificados.length());
    }
    
    private byte[] descomprimirHuffman(byte[] dadosComprimidos, int tamanhoOriginal) {
        if (dadosComprimidos.length == 0) return new byte[0];
        
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dadosComprimidos);
            ObjectInputStream ois = new ObjectInputStream(bais);
            
            // Desserializar árvore Huffman
            NoHuffman raiz = (NoHuffman) ois.readObject();
            
            // Ler dados comprimidos
            int numBits = ois.readInt();
            byte[] bitsComprimidos = new byte[ois.available()];
            ois.readFully(bitsComprimidos);
            
            // Decodificar
            return decodificarDados(raiz, bitsComprimidos, numBits, tamanhoOriginal);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro na descompressão Huffman", e);
        }
    }
    
    private Map<Byte, Integer> calcularFrequencias(byte[] dados) {
        Map<Byte, Integer> frequencias = new HashMap<>();
        for (byte b : dados) {
            frequencias.put(b, frequencias.getOrDefault(b, 0) + 1);
        }
        return frequencias;
    }
    
    private NoHuffman construirArvoreHuffman(Map<Byte, Integer> frequencias) {
        PriorityQueue<NoHuffman> fila = new PriorityQueue<>();
        
        // Criar folhas para cada byte
        for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
            fila.offer(new NoHuffman(entry.getKey(), entry.getValue()));
        }
        
        // Construir árvore
        while (fila.size() > 1) {
            NoHuffman esquerda = fila.poll();
            NoHuffman direita = fila.poll();
            NoHuffman pai = new NoHuffman(esquerda.frequencia + direita.frequencia, esquerda, direita);
            fila.offer(pai);
        }
        
        return fila.poll();
    }
    
    private Map<Byte, String> gerarCodigosHuffman(NoHuffman raiz) {
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigosRecursivo(raiz, "", codigos);
        return codigos;
    }
    
    private void gerarCodigosRecursivo(NoHuffman no, String codigo, Map<Byte, String> codigos) {
        if (no.isFolha()) {
            codigos.put(no.byteVal, codigo.isEmpty() ? "0" : codigo);
        } else {
            gerarCodigosRecursivo(no.esquerda, codigo + "0", codigos);
            gerarCodigosRecursivo(no.direita, codigo + "1", codigos);
        }
    }
    
    private byte[] bitsParaBytes(String bits) {
        int length = (bits.length() + 7) / 8;
        byte[] bytes = new byte[length];
        
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                bytes[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        
        return bytes;
    }
    
    private byte[] serializarDadosComprimidos(NoHuffman raiz, byte[] dadosComprimidos, int numBits) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            
            // Serializar árvore Huffman
            oos.writeObject(raiz);
            
            // Serializar número de bits e dados comprimidos
            oos.writeInt(numBits);
            oos.write(dadosComprimidos);
            
            oos.close();
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Erro na serialização", e);
        }
    }
    
    private byte[] decodificarDados(NoHuffman raiz, byte[] bitsComprimidos, int numBits, int tamanhoOriginal) {
        // Converter bytes para string de bits
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < bitsComprimidos.length; i++) {
            byte b = bitsComprimidos[i];
            for (int j = 7; j >= 0; j--) {
                if (bits.length() < numBits) {
                    bits.append((b & (1 << j)) != 0 ? '1' : '0');
                }
            }
        }
        
        // Decodificar usando a árvore Huffman
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NoHuffman atual = raiz;
        
        for (int i = 0; i < numBits; i++) {
            char bit = bits.charAt(i);
            atual = (bit == '0') ? atual.esquerda : atual.direita;
            
            if (atual.isFolha()) {
                baos.write(atual.byteVal);
                atual = raiz;
                
                // Parar se atingirmos o tamanho original
                if (baos.size() >= tamanhoOriginal) {
                    break;
                }
            }
        }
        
        return baos.toByteArray();
    }
    
    private byte[] lerArquivo(String caminho) throws IOException {
        File file = new File(caminho);
        byte[] dados = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(dados);
        }
        return dados;
    }
    
    private void salvarArquivo(String caminho, byte[] dados) throws IOException {
        File pasta = new File(caminho).getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(caminho)) {
            fos.write(dados);
        }
    }
    
    @Override
    public String getNomeMetodo() {
        return "Huffman";
    }
}