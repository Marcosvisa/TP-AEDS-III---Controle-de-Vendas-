package util;
import java.io.*;
import java.util.*;

public class CompressorLZW implements Compressor {
    
    private static final int DICT_SIZE = 4096; // tamanho máximo do dicionário (12 bits)
    private static final int MAX_CODE = DICT_SIZE - 1;
    
    @Override
    public void comprimir(String[] arquivosEntrada, String arquivoSaida) throws Exception {
        System.out.println("Comprimindo com LZW...");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoSaida))) {
            oos.writeUTF("LZW");
            oos.writeInt(arquivosEntrada.length);
            
            for (String arquivo : arquivosEntrada) {
                File file = new File(arquivo);
                if (!file.exists()) continue;
                
                byte[] dados = lerArquivo(arquivo);
                byte[] dadosComprimidos = comprimirLZW(dados);
                
                oos.writeUTF(file.getName());
                oos.writeInt(dados.length);
                oos.writeInt(dadosComprimidos.length);
                oos.write(dadosComprimidos);
                
                System.out.println(" - " + file.getName() + ": " + dados.length + " → " + dadosComprimidos.length + " bytes");
            }
        }
    }
    
    @Override
    public void descomprimir(String arquivoEntrada, String pastaSaida) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoEntrada))) {
            String metodo = ois.readUTF();
            if (!metodo.equals("LZW")) {
                throw new Exception("Arquivo não compatível com LZW");
            }
            
            int numArquivos = ois.readInt();
            System.out.println("Descomprimindo " + numArquivos + " arquivos...");
            
            for (int i = 0; i < numArquivos; i++) {
                String nomeArquivo = ois.readUTF();
                int tamanhoOriginal = ois.readInt();
                int tamanhoComprimido = ois.readInt();
                byte[] dadosComprimidos = new byte[tamanhoComprimido];
                ois.readFully(dadosComprimidos);
                
                byte[] dadosOriginais = descomprimirLZW(dadosComprimidos, tamanhoOriginal);
                String caminhoCompleto = pastaSaida + File.separator + nomeArquivo;
                salvarArquivo(caminhoCompleto, dadosOriginais);
                
                System.out.println(" - " + nomeArquivo + ": " + tamanhoComprimido + " → " + dadosOriginais.length + " bytes");
            }
        }
    }
    
    private byte[] comprimirLZW(byte[] dados) {
        if (dados.length == 0) return new byte[0];
        
        //inicializa dicionário com todos os bytes individuais
        Map<String, Integer> dicionario = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dicionario.put(String.valueOf((char) i), i);
        }
        
        int proximoCodigo = 256;
        List<Integer> codigos = new ArrayList<>();
        String sequenciaAtual = "";
        
        for (byte b : dados) {
            char caractere = (char) (b & 0xFF);
            String sequenciaTeste = sequenciaAtual + caractere;
            
            if (dicionario.containsKey(sequenciaTeste)) {
                sequenciaAtual = sequenciaTeste;
            } else {
                //adiciona sequência atual à saída
                codigos.add(dicionario.get(sequenciaAtual));
                
                // adiciona nova sequência ao dicionário
                if (proximoCodigo <= MAX_CODE) {
                    dicionario.put(sequenciaTeste, proximoCodigo++);
                }
                
                //reinicia com o caractere atual
                sequenciaAtual = String.valueOf(caractere);
            }
        }
        
        //adiciona a última sequência
        if (!sequenciaAtual.isEmpty()) {
            codigos.add(dicionario.get(sequenciaAtual));
        }
        
        return codigosParaBytes(codigos);
    }
    
    private byte[] descomprimirLZW(byte[] dadosComprimidos, int tamanhoOriginal) {
        if (dadosComprimidos.length == 0) return new byte[0];
        
        List<Integer> codigos = bytesParaCodigos(dadosComprimidos);
        
        //inicializa dicionário com todos os bytes individuais
        Map<Integer, String> dicionario = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dicionario.put(i, String.valueOf((char) i));
        }
        
        int proximoCodigo = 256;
        StringBuilder resultado = new StringBuilder();
        
        if (codigos.isEmpty()) return new byte[0];
        
        int codigoAnterior = codigos.get(0);
        resultado.append(dicionario.get(codigoAnterior));
        
        for (int i = 1; i < codigos.size(); i++) {
            int codigoAtual = codigos.get(i);
            String sequencia;
            
            if (dicionario.containsKey(codigoAtual)) {
                sequencia = dicionario.get(codigoAtual);
            } else if (codigoAtual == proximoCodigo) {
                //caso especial: código ainda não está no dicionário
                sequencia = dicionario.get(codigoAnterior) + dicionario.get(codigoAnterior).charAt(0);
            } else {
                throw new IllegalArgumentException("Código inválido na descompressão: " + codigoAtual);
            }
            
            resultado.append(sequencia);
            
            //adiciona nova entrada ao dicionário
            if (proximoCodigo <= MAX_CODE) {
                String novaEntrada = dicionario.get(codigoAnterior) + sequencia.charAt(0);
                dicionario.put(proximoCodigo++, novaEntrada);
            }
            
            codigoAnterior = codigoAtual;
        }
        
        //converte StringBuilder para byte[]
        String resultadoString = resultado.toString();
        byte[] dadosOriginais = new byte[resultadoString.length()];
        for (int i = 0; i < resultadoString.length(); i++) {
            dadosOriginais[i] = (byte) resultadoString.charAt(i);
        }
        
        return dadosOriginais;
    }
    
    private byte[] codigosParaBytes(List<Integer> codigos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            
            // Escreve número de códigos
            dos.writeInt(codigos.size());
            
            // Escreve cada código (2 bytes cada)
            for (int codigo : codigos) {
                dos.writeShort(codigo);
            }
            
            dos.close();
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Erro ao converter códigos para bytes", e);
        }
    }
    
    private List<Integer> bytesParaCodigos(byte[] dados) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dados);
            DataInputStream dis = new DataInputStream(bais);
            
            List<Integer> codigos = new ArrayList<>();
            
            // Lê número de códigos
            int numCodigos = dis.readInt();
            
            // Lê cada código (2 bytes cada)
            for (int i = 0; i < numCodigos; i++) {
                codigos.add((int) dis.readShort());
            }
            
            dis.close();
            return codigos;
            
        } catch (IOException e) {
            throw new RuntimeException("Erro ao converter bytes para códigos", e);
        }
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
        return "LZW";
    }
}