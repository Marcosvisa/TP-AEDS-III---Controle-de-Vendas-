import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Venda_Autopecas implements Registro {

    private int id;
    private int id_vendedor;
    private int[] ids_autopecas;
    private LocalDate data_venda;
    private float valor_total;

    public Venda_Autopecas() {
        this.id = 0;
        this.id_vendedor = 0;
        this.ids_autopecas = new int[0];
        this.data_venda = LocalDate.now();
        this.valor_total = 0f;
    }

    public Venda_Autopecas(int id, int id_vendedor, int[] ids_autopecas, LocalDate data_venda, float valor_total) {
        this.id = id;
        this.id_vendedor = id_vendedor;
        this.ids_autopecas = ids_autopecas;
        this.data_venda = data_venda;
        this.valor_total = valor_total;
    }

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeInt(id_vendedor);
        dos.writeInt(ids_autopecas.length);
        for (int i : ids_autopecas) dos.writeInt(i);
        dos.writeUTF(data_venda.toString());
        dos.writeFloat(valor_total);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        id_vendedor = dis.readInt();
        int n = dis.readInt();
        ids_autopecas = new int[n];
        for (int i = 0; i < n; i++) ids_autopecas[i] = dis.readInt();
        data_venda = LocalDate.parse(dis.readUTF());
        valor_total = dis.readFloat();
    }

    public int getId_vendedor() { return id_vendedor; }
    public void setId_vendedor(int id_vendedor) { this.id_vendedor = id_vendedor; }
    public int[] getIds_autopecas() { return ids_autopecas; }
    public void setIds_autopecas(int[] ids_autopecas) { this.ids_autopecas = ids_autopecas; }
    public LocalDate getData_venda() { return data_venda; }
    public void setData_venda(LocalDate data_venda) { this.data_venda = data_venda; }
    public float getValor_total() { return valor_total; }
    public void setValor_total(float valor_total) { this.valor_total = valor_total; }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\nID Venda: ").append(id);
        sb.append("\nVendedor ID: ").append(id_vendedor);
        sb.append("\nAutopeças IDs: ");
        for (int i : ids_autopecas) sb.append(i).append(" ");
        sb.append("\nData da venda: ").append(data_venda.format(df));
        sb.append("\nValor total: R$ ").append(valor_total);
        return sb.toString();
    }
}
