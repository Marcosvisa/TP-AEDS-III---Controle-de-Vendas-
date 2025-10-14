package dao;
import model.Autopecas;

public class AutopecasDAO extends Arquivo<Autopecas> {
    public AutopecasDAO() throws Exception {
        super("dados/autopecas.db", Autopecas.class);
    }
}