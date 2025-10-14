package dao;
import model.Venda_Autopecas;

public class VendaAutopecasDAO extends Arquivo<Venda_Autopecas> {
    public VendaAutopecasDAO() throws Exception {
        super("dados/vendas_autopecas.db", Venda_Autopecas.class);
    }
}