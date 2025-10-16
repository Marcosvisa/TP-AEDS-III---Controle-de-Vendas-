// ParVendedorCarroOffset.java
package dao;
import java.io.*;
import util.RegistroHashExtensivel;

public class ParVendedorCarroOffset implements RegistroHashExtensivel<ParVendedorCarroOffset> {

  private int idVendedor; //chave: ID do Vendedor
  private long offset;   //o offset do registro Carro no arquivo de dados
  private short TAMANHO = 12; //int (4 bytes) + long (8 bytes)

  public ParVendedorCarroOffset() {
    this(-1, -1L);
  }

  public ParVendedorCarroOffset(int idVendedor, long offset) {
    this.idVendedor = idVendedor;
    this.offset = offset;
  }

  @Override
  public int hashCode() {
    return Math.abs(idVendedor);
  }

  @Override
  public short size() {
    return TAMANHO;
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    
    //escreve os dados
    dos.writeInt(idVendedor);
    dos.writeLong(offset);
    dos.flush();
    
    byte[] bs = baos.toByteArray();
    
    //preenche com bytes para garantir o tamanho FIXO TAMANHO (12)
    byte[] bs2 = new byte[TAMANHO];
    //copia os bytes reais (12 bytes)
    System.arraycopy(bs, 0, bs2, 0, bs.length);
    //não é necessário preencher o restante, pois int+long já é 12 bytes
    
    return bs2;
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    this.idVendedor = dis.readInt();
    this.offset = dis.readLong();
  }

  public int getIdVendedor() {
    return idVendedor;
  }

  public long getOffset() {
    return offset;
  }

  @Override
  public String toString() {
    return "Vendedor ID " + idVendedor + " @ Carro Offset " + offset;
  }

  
}