package model;
import java.io.*;
import java.util.Objects;
import util.Registro;

public class CarroVenda implements Registro {
    private int idCarro;  //parte da PK composta
    private int idVenda;  //parte da PK composta
    
    public CarroVenda() {
        this.idCarro = 0;
        this.idVenda = 0;
    }
    
    public CarroVenda(int idCarro, int idVenda) {
        this.idCarro = idCarro;
        this.idVenda = idVenda;
    }
    
    @Override
    public int getId() {
        // Para manter compatibilidade com a interface, podemos usar um hash da PK composta
        return (idCarro + "-" + idVenda).hashCode();
    }
    
    @Override
    public void setId(int id) {

    }
    
    // Método para verificar igualdade baseada na PK composta
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CarroVenda that = (CarroVenda) obj;
        return idCarro == that.idCarro && idVenda == that.idVenda;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idCarro, idVenda);
    }
    
    public int getIdCarro() { return idCarro; }
    public void setIdCarro(int idCarro) { this.idCarro = idCarro; }
    
    public int getIdVenda() { return idVenda; }
    public void setIdVenda(int idVenda) { this.idVenda = idVenda; }
    
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idCarro);  // Parte 1 da PK composta
        dos.writeInt(idVenda);  // Parte 2 da PK composta
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idCarro = dis.readInt();
        idVenda = dis.readInt();
    }
    
    @Override
    public String toString() {
        return "CarroVenda [Carro: " + idCarro + ", Venda: " + idVenda + "]";
    }
}