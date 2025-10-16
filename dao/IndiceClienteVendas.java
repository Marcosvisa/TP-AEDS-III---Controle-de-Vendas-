package dao;
import java.lang.reflect.Constructor;
import java.util.List;

public class IndiceClienteVendas {

    private HashExtensivel<ParClienteVendaOffset> indice;
    private final String NOME_DIRETORIO = "dados/cli_venda_d.hash_d.db";
    private final String NOME_CESTOS = "dados/cli_venda_c.hash_c.db";

    public IndiceClienteVendas() throws Exception {
        Constructor<ParClienteVendaOffset> construtor = 
            ParClienteVendaOffset.class.getConstructor();
        indice = new HashExtensivel<>(
            construtor,
            4, 
            NOME_DIRETORIO, 
            NOME_CESTOS
        );
    }
    
    public boolean addVenda(String cpfCliente, long offset) throws Exception {
        ParClienteVendaOffset parExistente = indice.read(Math.abs(cpfCliente.hashCode()));
        
        if (parExistente == null) {
            ParClienteVendaOffset novoPar = new ParClienteVendaOffset(cpfCliente, offset);
            return indice.create(novoPar);
        } else {
            parExistente.addOffset(offset);
            return indice.update(parExistente);
        }
    }
    
    public ParClienteVendaOffset read(String cpfCliente) throws Exception {
        return indice.read(Math.abs(cpfCliente.hashCode()));
    }
    
    public boolean delete(String cpfCliente) throws Exception {
        return indice.delete(Math.abs(cpfCliente.hashCode()));
    }
    
    public List<Long> getVendasPorCliente(String cpfCliente) throws Exception {
        ParClienteVendaOffset par = read(cpfCliente);
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