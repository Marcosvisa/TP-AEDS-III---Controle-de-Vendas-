package dao;
import model.Cliente;

public class ClienteDAO extends Arquivo<Cliente> {
    public ClienteDAO() throws Exception {
        super("dados/clientes.db", Cliente.class);
    }
}