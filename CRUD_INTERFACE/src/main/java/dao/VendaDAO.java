package dao;
import model.Venda;

public class VendaDAO extends Arquivo<Venda> {
    public VendaDAO() throws Exception {
        super("dados/vendas.db", Venda.class);
    }
    public long createWithOffset(Venda obj) throws Exception {
    return super.createWithOffset(obj);
}
}