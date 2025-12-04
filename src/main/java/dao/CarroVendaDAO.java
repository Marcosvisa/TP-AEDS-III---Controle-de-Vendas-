package dao;
import model.CarroVenda;
import java.util.ArrayList;

public class CarroVendaDAO extends Arquivo<CarroVenda> {
    public CarroVendaDAO() throws Exception {
        super("dados/carro_venda.db", CarroVenda.class);
    }

    public CarroVenda read(int idCarro, int idVenda) throws Exception {
        ArrayList<CarroVenda> todas = readAll();
        for (CarroVenda cv : todas) {
            if (cv.getIdCarro() == idCarro && cv.getIdVenda() == idVenda) {
                return cv;
            }
        }
        return null;
    }

    public ArrayList<CarroVenda> getByCarro(int idCarro) throws Exception {
        ArrayList<CarroVenda> resultado = new ArrayList<>();
        ArrayList<CarroVenda> todas = readAll();
        for (CarroVenda cv : todas) {
            if (cv.getIdCarro() == idCarro) {
                resultado.add(cv);
            }
        }
        return resultado;
    }

    public ArrayList<CarroVenda> getByVenda(int idVenda) throws Exception {
        ArrayList<CarroVenda> resultado = new ArrayList<>();
        ArrayList<CarroVenda> todas = readAll();
        for (CarroVenda cv : todas) {
            if (cv.getIdVenda() == idVenda) {
                resultado.add(cv);
            }
        }
        return resultado;
    }

    public boolean create(int idCarro, int idVenda) throws Exception {
    
        if (read(idCarro, idVenda) != null) {
            throw new Exception("Relação Carro-Venda já existe!");
        }
        
        CarroVenda novaRelacao = new CarroVenda(idCarro, idVenda);
        create(novaRelacao);
        return true;
    }

    public boolean delete(int idCarro, int idVenda) throws Exception {
        ArrayList<CarroVenda> todas = readAll();
        for (int i = 0; i < todas.size(); i++) {
            CarroVenda cv = todas.get(i);
            if (cv.getIdCarro() == idCarro && cv.getIdVenda() == idVenda) {
                return delete(cv.getId());
            }
        }
        return false;
    }

    public int deleteByCarro(int idCarro) throws Exception {
        ArrayList<CarroVenda> relacoes = getByCarro(idCarro);
        int count = 0;
        for (CarroVenda cv : relacoes) {
            if (delete(cv.getId())) {
                count++;
            }
        }
        return count;
    }

    public int deleteByVenda(int idVenda) throws Exception {
        ArrayList<CarroVenda> relacoes = getByVenda(idVenda);
        int count = 0;
        for (CarroVenda cv : relacoes) {
            if (delete(cv.getId())) {
                count++;
            }
        }
        return count;
    }
}