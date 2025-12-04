package dao;
import model.Vendedor;

public class VendedorDAO extends Arquivo<Vendedor> {
    public VendedorDAO() throws Exception {
        super("dados/vendedores.db", Vendedor.class);
    }
}