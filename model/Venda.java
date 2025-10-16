package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;

public class Venda implements Registro {

    private int id;
    private String cpfVendedor; //Chave fk para Vendedor
    private String cpfCliente;  //chave fk para Cliente
    private int[] idsCarros;    //array de Ids dos carros
    private LocalDate data_venda;
    private float valor_total;

    public Venda() {
        this.id = 0;
        this.cpfVendedor = "";
        this.cpfCliente = "";
        this.idsCarros = new int[0];
        this.data_venda = LocalDate.now();
        this.valor_total = 0f;
    }

    public Venda(int id, String cpfVendedor, String cpfCliente, int[] idsCarros, LocalDate data_venda, float valor_total) {
        this.id = id;
        this.cpfVendedor = cpfVendedor;
        this.cpfCliente = cpfCliente;
        this.idsCarros = idsCarros;
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
        dos.writeUTF(cpfVendedor);
        dos.writeUTF(cpfCliente);
        dos.writeInt(idsCarros.length);
        for (int idCarro : idsCarros) dos.writeInt(idCarro);
        dos.writeUTF(data_venda.toString());
        dos.writeFloat(valor_total);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        cpfVendedor = dis.readUTF();
        cpfCliente = dis.readUTF();
        int n = dis.readInt();
        idsCarros = new int[n];
        for (int i = 0; i < n; i++) idsCarros[i] = dis.readInt();
        data_venda = LocalDate.parse(dis.readUTF());
        valor_total = dis.readFloat();
    }

    public String getCpfVendedor() { return cpfVendedor; }
    public void setCpfVendedor(String cpfVendedor) { this.cpfVendedor = cpfVendedor; }
    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    public int[] getIdsCarros() { return idsCarros; }
    public void setIdsCarros(int[] idsCarros) { this.idsCarros = idsCarros; }
    public LocalDate getData_venda() { return data_venda; }
    public void setData_venda(LocalDate data_venda) { this.data_venda = data_venda; }
    public float getValor_total() { return valor_total; }
    public void setValor_total(float valor_total) { this.valor_total = valor_total; }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\nID Venda: ").append(id);
        sb.append("\nVendedor CPF: ").append(cpfVendedor);
        sb.append("\nCliente CPF: ").append(cpfCliente);
        sb.append("\nCarros IDs: ");
        for (int id : idsCarros) sb.append(id).append(" ");
        sb.append("\nData da venda: ").append(data_venda.format(df));
        sb.append("\nValor total: R$ ").append(valor_total);
        return sb.toString();
    }
}