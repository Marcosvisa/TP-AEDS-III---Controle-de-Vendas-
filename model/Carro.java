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

    public Carro() {
        this.id = 0;
        this.modelo = "";
        this.cores = new String[0];
        this.data_fabricacao = LocalDate.now();
        this.preco = 0f;
    }

    public Carro(int id, String modelo, String[] cores, LocalDate data_fabricacao, float preco) {
        this.id = id;
        this.modelo = modelo;
        this.cores = cores;
        this.data_fabricacao = data_fabricacao;
        this.preco = preco;
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
        dos.writeUTF(modelo); // ⭐ AGORA SEM CRIPTOGRAFIA
        
        dos.writeInt(cores.length);
        for (String c : cores) dos.writeUTF(c);
        dos.writeLong(data_fabricacao.toEpochDay()); // ⭐ MELHOR: usa epoch day
        dos.writeFloat(preco);
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        modelo = dis.readUTF(); // ⭐ AGORA SEM DESCRIPTOGRAFIA
        
        int n = dis.readInt();
        cores = new String[n];
        for (int i = 0; i < n; i++) cores[i] = dis.readUTF();
        
        data_fabricacao = LocalDate.ofEpochDay(dis.readLong());
        preco = dis.readFloat();
    }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String[] getCores() { return cores; }
    public void setCores(String[] cores) { this.cores = cores; }
    public LocalDate getData_fabricacao() { return data_fabricacao; }
    public void setData_fabricacao(LocalDate data_fabricacao) { this.data_fabricacao = data_fabricacao; }
    public float getPreco() { return preco; }
    public void setPreco(float preco) { this.preco = preco; }

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
        return sb.toString();
    }
}