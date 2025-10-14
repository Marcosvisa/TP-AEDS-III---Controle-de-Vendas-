public class CarroDAO extends Arquivo<Carro> {
    public CarroDAO() throws Exception {
        super("dados/carros.db", Carro.class);
    }
}
