import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Autopecas implements Registro {

    private int id;
    private String descricao;
    private String tipo;
    private LocalDate data_fabricacao;
    private float preco;

    public Autopecas() {
        this.id = 0;
        this.descricao = "";
        this.tipo = "";
        this.data_fabricacao = LocalDate.now();
        this.preco = 0f;
    }

    public Autopecas(int id, String descricao, String tipo, LocalDate data_fabricacao, float preco) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
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
        dos.writeUTF(descricao);
        dos.writeUTF(tipo);
        dos.writeUTF(data_fabricacao.toString());
        dos.writeFloat(preco);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        descricao = dis.readUTF();
        tipo = dis.readUTF();
        data_fabricacao = LocalDate.parse(dis.readUTF());
        preco = dis.readFloat();
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDate getData_fabricacao() { return data_fabricacao; }
    public void setData_fabricacao(LocalDate data_fabricacao) { this.data_fabricacao = data_fabricacao; }
    public float getPreco() { return preco; }
    public void setPreco(float preco) { this.preco = preco; }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "\nID: " + id +
               "\nDescrição: " + descricao +
               "\nTipo: " + tipo +
               "\nData de fabricação: " + data_fabricacao.format(df) +
               "\nPreço: R$ " + preco;
    }
}
