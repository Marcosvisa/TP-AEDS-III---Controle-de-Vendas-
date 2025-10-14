package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;


public class Carro implements Registro {

    private int id;
    private String modelo;
    private String[] cores;
    private LocalDate data_fabricacao;
    private float preco;
    private int id_vendedor;

    public Carro() {
        this.id = 0;
        this.modelo = "";
        this.cores = new String[0];
        this.data_fabricacao = LocalDate.now();
        this.preco = 0f;
        this.id_vendedor=0;
    }

    public Carro(int id, String modelo, String[] cores, LocalDate data_fabricacao, float preco, int id_vendedor) {
        this.id = id;
        this.modelo = modelo;
        this.cores = cores;
        this.data_fabricacao = data_fabricacao;
        this.preco = preco;
        this.id_vendedor=id_vendedor;

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
        dos.writeUTF(modelo);
        dos.writeInt(cores.length);
        for (String c : cores) dos.writeUTF(c);
        dos.writeUTF(data_fabricacao.toString());
        dos.writeFloat(preco);
        dos.writeInt(id_vendedor);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        modelo = dis.readUTF();

        int n = dis.readInt();
        cores = new String[n];
        for (int i = 0; i < n; i++) cores[i] = dis.readUTF();

        data_fabricacao = LocalDate.parse(dis.readUTF());
        preco = dis.readFloat();
        id_vendedor = dis.readInt();
    }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String[] getCores() { return cores; }
    public void setCores(String[] cores) { this.cores = cores; }
    public LocalDate getData_fabricacao() { return data_fabricacao; }
    public void setData_fabricacao(LocalDate data_fabricacao) { this.data_fabricacao = data_fabricacao; }
    public float getPreco() { return preco; }
    public void setPreco(float preco) { this.preco = preco; }
    public int getid_vendedor() { return id_vendedor; }
    public void setid_vendedor(int id_vendedor) { this.id_vendedor=id_vendedor;}

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\nID: ").append(id);
        sb.append("\nModelo: ").append(modelo);
        sb.append("\nCores: ");
        for (String c : cores) sb.append(c).append(" ");
        sb.append("\nData de fabricação: ").append(data_fabricacao.format(df));
        sb.append("\nPreço: R$ ").append(preco);
        sb.append("\nId_vendedor: ").append(id_vendedor);
        return sb.toString();
    }
}
