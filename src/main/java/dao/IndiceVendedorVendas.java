package dao;
import java.lang.reflect.Constructor;
import java.util.List;

public class IndiceVendedorVendas {

    private HashExtensivel<ParVendedorVendaOffset> indice;
    private final String NOME_DIRETORIO = "dados/vend_venda_d.hash_d.db";
    private final String NOME_CESTOS = "dados/vend_venda_c.hash_c.db";

    public IndiceVendedorVendas() throws Exception {
        Constructor<ParVendedorVendaOffset> construtor = 
            ParVendedorVendaOffset.class.getConstructor();
        indice = new HashExtensivel<>(
            construtor,
            4, 
            NOME_DIRETORIO, 
            NOME_CESTOS
        );
    }
    
    //método para adicionar uma venda ao vendedor
    public boolean addVenda(String cpfVendedor, long offset) throws Exception {
        ParVendedorVendaOffset parExistente = indice.read(Math.abs(cpfVendedor.hashCode()));
        
        if (parExistente == null) {
            ParVendedorVendaOffset novoPar = new ParVendedorVendaOffset(cpfVendedor, offset);
            return indice.create(novoPar);
        } else {
            parExistente.addOffset(offset);
            return indice.update(parExistente);
        }
    }
    
    public ParVendedorVendaOffset read(String cpfVendedor) throws Exception {
        return indice.read(Math.abs(cpfVendedor.hashCode()));
    }
    
    public boolean delete(String cpfVendedor) throws Exception {
        return indice.delete(Math.abs(cpfVendedor.hashCode()));
    }
    
    // Método para buscar todas as vendas de um vendedor
    public List<Long> getVendasPorVendedor(String cpfVendedor) throws Exception {
        ParVendedorVendaOffset par = read(cpfVendedor);
        if (par != null) {
            return par.getOffsets();
        }
        return new java.util.ArrayList<>();
    }
    
    public void close() throws Exception {
        if (indice != null) {
            indice.close();
        }
    }
}