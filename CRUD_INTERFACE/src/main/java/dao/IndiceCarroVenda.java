package dao;
import java.util.List;

public class IndiceCarroVenda {
    private BPlusTreeCarroVenda bptree;
    
    public IndiceCarroVenda() {
        this.bptree = new BPlusTreeCarroVenda("dados/carro_vendas_bptree.db");
    }
    
    // 🔗 ADICIONA venda a um carro
    public void addVendaToCarro(int idCarro, int idVenda) {
        bptree.addVendaToCarro(idCarro, idVenda);
    }
    
    // 🔍 BUSCA todas as vendas de um carro
    public List<Integer> getVendasPorCarro(int idCarro) {
        return bptree.search(idCarro);
    }
    
    // 🔍 BUSCA todos os carros de uma venda
    public List<Integer> getCarrosPorVenda(int idVenda) {
        return bptree.getCarrosPorVenda(idVenda);
    }
    
    public void removeVendaFromCarro(int idCarro, int idVenda) {
        List<Integer> vendas = bptree.search(idCarro);
        if (vendas.contains(idVenda)) {
            vendas.remove((Integer) idVenda);
            if (vendas.isEmpty()) {
                bptree.remove(idCarro);
            } else {
                bptree.update(idCarro, vendas);
            }
        }
    }
    
    // 💾 FECHA e salva no disco
    public void close() {
        bptree.close();
    }
    

}