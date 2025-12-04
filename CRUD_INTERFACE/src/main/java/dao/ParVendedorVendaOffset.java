package dao;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import util.RegistroHashExtensivel;

public class ParVendedorVendaOffset implements RegistroHashExtensivel<ParVendedorVendaOffset> {

  private String cpfVendedor;
  private List<Long> offsets; //lista de offsets para múltiplas vendas
  private short TAMANHO = 500; //tamanho maior para acomodar a lista

  public ParVendedorVendaOffset() {
    this.cpfVendedor = "";
    this.offsets = new ArrayList<>();
  }

  public ParVendedorVendaOffset(String cpfVendedor, long offset) {
    this.cpfVendedor = cpfVendedor;
    this.offsets = new ArrayList<>();
    this.offsets.add(offset);
  }

  public ParVendedorVendaOffset(String cpfVendedor, List<Long> offsets) {
    this.cpfVendedor = cpfVendedor;
    this.offsets = offsets != null ? offsets : new ArrayList<>();
  }

  @Override
  public int hashCode() {
    return Math.abs(cpfVendedor.hashCode());
  }

  @Override
  public short size() {
    return TAMANHO;
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    
    //escreve CPF
    dos.writeUTF(cpfVendedor);
    
    //escreve quantidade de offsets
    dos.writeInt(offsets.size());
    
    //escreve cada offset
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
    
    this.cpfVendedor = dis.readUTF();
    
    //lê quantidade de offsets
    int quantidade = dis.readInt();
    this.offsets = new ArrayList<>();
    
    //lê cada offset
    for (int i = 0; i < quantidade; i++) {
      offsets.add(dis.readLong());
    }
  }

  //método para adicionar um offset à lista
  public void addOffset(long offset) {
    if (!offsets.contains(offset)) {
      offsets.add(offset);
    }
  }

  //metdo para remover um offset da lista
  public boolean removeOffset(long offset) {
    return offsets.remove(offset);
  }

  public String getCpfVendedor() { return cpfVendedor; }
  public List<Long> getOffsets() { return offsets; }
  public int getQuantidadeVendas() { return offsets.size(); }

  @Override
  public String toString() {
    return "Vendedor CPF " + cpfVendedor + " → " + offsets.size() + " vendas - Offsets: " + offsets;
  }
}