package model;
import java.io.*;
import util.Registro;

public class CarroVenda implements Registro {
    private int id;
    private int idCarro;
    private int idVenda;
    
    public CarroVenda() {
        this.id = 0;
        this.idCarro = 0;
        this.idVenda = 0;
    }
    
    public CarroVenda(int id, int idCarro, int idVenda) {
        this.id = id;
        this.idCarro = idCarro;
        this.idVenda = idVenda;
    }
    
    @Override
    public int getId() { return id; }
    
    @Override
    public void setId(int id) { this.id = id; }
    
    public int getIdCarro() { return idCarro; }
    public void setIdCarro(int idCarro) { this.idCarro = idCarro; }
    
    public int getIdVenda() { return idVenda; }
    public void setIdVenda(int idVenda) { this.idVenda = idVenda; }
    
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idCarro);
        dos.writeInt(idVenda);
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idCarro = dis.readInt();
        idVenda = dis.readInt();
    }
    
    @Override
    public String toString() {
        return "CarroVenda [ID: " + id + ", Carro: " + idCarro + ", Venda: " + idVenda + "]";
    }
}