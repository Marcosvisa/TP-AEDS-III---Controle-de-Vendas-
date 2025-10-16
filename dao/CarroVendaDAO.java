package dao;
import model.CarroVenda;

public class CarroVendaDAO extends Arquivo<CarroVenda> {
    public CarroVendaDAO() throws Exception {
        super("dados/carro_venda.db", CarroVenda.class);
    }
}