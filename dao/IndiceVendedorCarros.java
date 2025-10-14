package dao;
import java.lang.reflect.Constructor;

public class IndiceVendedorCarros {

    private HashExtensivel<ParVendedorCarroOffset> indice;
    private final String NOME_DIRETORIO = "dados/vend_carro_d.hash_d.db";
    private final String NOME_CESTOS = "dados/vend_carro_c.hash_c.db";

    public IndiceVendedorCarros() throws Exception {
        Constructor<ParVendedorCarroOffset> construtor = 
            ParVendedorCarroOffset.class.getConstructor();
        indice = new HashExtensivel<>(
            construtor,
            4, 
            NOME_DIRETORIO, 
            NOME_CESTOS
        );
    }
    
    public boolean create(int idVendedor, long offset) throws Exception {
        return indice.create(new ParVendedorCarroOffset(idVendedor, offset));
    }
    
    public ParVendedorCarroOffset read(int idVendedor) throws Exception {
        return indice.read(idVendedor); 
    }
    
    public boolean delete(int idVendedor) throws Exception {
        return indice.delete(idVendedor);
    }
}