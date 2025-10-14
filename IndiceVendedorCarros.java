// IndiceVendedorCarros.java
import java.lang.reflect.Constructor;
// Não precisa de import java.io.File, pois o HashExtensivel já gerencia o acesso a arquivos

public class IndiceVendedorCarros {

    private HashExtensivel<ParVendedorCarroOffset> indice;
    private final String NOME_DIRETORIO = "dados/vend_carro_d.hash_d.db";
    private final String NOME_CESTOS = "dados/vend_carro_c.hash_c.db";

      public IndiceVendedorCarros() throws Exception {
        
        // CORRIGIDO: Obtém o construtor PADRÃO (sem argumentos)
        Constructor<ParVendedorCarroOffset> construtor = 
            ParVendedorCarroOffset.class.getConstructor(); // <-- Removido (int.class, long.class)

        indice = new HashExtensivel<>(
            construtor, // Agora, este é o construtor vazio
            4, 
            NOME_DIRETORIO, 
            NOME_CESTOS
        );
    }
    
    public boolean create(int idVendedor, long offset) throws Exception {
        return indice.create(new ParVendedorCarroOffset(idVendedor, offset));
    }
    
    public ParVendedorCarroOffset read(int idVendedor) throws Exception {
        // A busca é feita usando o ID do Vendedor como chave (hashCode)
        return indice.read(idVendedor); 
    }
    
    public boolean delete(int idVendedor) throws Exception {
        return indice.delete(idVendedor);
    }
}