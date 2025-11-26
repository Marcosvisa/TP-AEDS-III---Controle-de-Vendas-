package util;

public interface Compressor {
    void comprimir(String[] arquivosEntrada, String arquivoSaida) throws Exception;
    void descomprimir(String arquivoEntrada, String pastaSaida) throws Exception;
    String getNomeMetodo();
}