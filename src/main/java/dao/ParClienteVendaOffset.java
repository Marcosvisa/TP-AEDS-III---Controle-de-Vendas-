package dao;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import util.RegistroHashExtensivel;

public class ParClienteVendaOffset implements RegistroHashExtensivel<ParClienteVendaOffset> {

  private String cpfCliente;
  private List<Long> offsets;
  private short TAMANHO = 500;

  public ParClienteVendaOffset() {
    this.cpfCliente = "";
    this.offsets = new ArrayList<>();
  }

  public ParClienteVendaOffset(String cpfCliente, long offset) {
    this.cpfCliente = cpfCliente;
    this.offsets = new ArrayList<>();
    this.offsets.add(offset);
  }

  @Override
  public int hashCode() {
    return Math.abs(cpfCliente.hashCode());
  }

  @Override
  public short size() {
    return TAMANHO;
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    
    dos.writeUTF(cpfCliente);
    dos.writeInt(offsets.size());
    
    for (Long offset : offsets) {
      dos.writeLong(offset);
    }
    
    byte[] bs = baos.toByteArray();
    byte[] bs2 = new byte[TAMANHO];
    System.arraycopy(bs, 0, bs2, 0, Math.min(bs.length, TAMANHO));
    
    return bs2;
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    
    this.cpfCliente = dis.readUTF();
    int quantidade = dis.readInt();
    this.offsets = new ArrayList<>();
    
    for (int i = 0; i < quantidade; i++) {
      offsets.add(dis.readLong());
    }
  }

  public void addOffset(long offset) {
    if (!offsets.contains(offset)) {
      offsets.add(offset);
    }
  }

  public boolean removeOffset(long offset) {
    return offsets.remove(offset);
  }

  public String getCpfCliente() { return cpfCliente; }
  public List<Long> getOffsets() { return offsets; }
  public int getQuantidadeVendas() { return offsets.size(); }

  @Override
  public String toString() {
    return "Cliente CPF " + cpfCliente + " → " + offsets.size() + " vendas - Offsets: " + offsets;
  }
}