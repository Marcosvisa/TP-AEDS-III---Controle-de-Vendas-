package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;

public class Vendedor implements Registro {

    private String cpf; // Agora o CPF é o ID
    private String nome;
    private String[] email;
    private LocalDate data_contratacao;
    private int numero_vendas;
    private float faturamento;

    public Vendedor() {
        this.cpf = "";
        this.nome = "";
        this.email = new String[0];
        this.data_contratacao = LocalDate.now();
        this.numero_vendas = 0;
        this.faturamento = 0f;
    }

    public Vendedor(String cpf, String nome, String[] email, LocalDate data_contratacao, int numero_vendas, float faturamento) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.data_contratacao = data_contratacao;
        this.numero_vendas = numero_vendas;
        this.faturamento = faturamento;
    }

    // ---------------- Métodos da interface Registro ----------------

    @Override
    public int getId() {
        // Como a interface exige int, mas temos String (CPF), 
        // vamos usar o hashCode do CPF como ID numérico
        return Math.abs(cpf.hashCode());
    }

    @Override
    public void setId(int id) {
        // Não podemos converter int para CPF, então este método fica vazio
        // O CPF deve ser definido via setter
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(cpf);
        dos.writeUTF(nome);
        dos.writeInt(email.length);
        for (String e : email) {
            dos.writeUTF(e);
        }
        dos.writeUTF(data_contratacao.toString());
        dos.writeInt(numero_vendas);
        dos.writeFloat(faturamento);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        cpf = dis.readUTF();
        nome = dis.readUTF();
        int tam = dis.readInt();
        email = new String[tam];
        for (int i = 0; i < tam; i++) {
            email[i] = dis.readUTF();
        }
        data_contratacao = LocalDate.parse(dis.readUTF());
        numero_vendas = dis.readInt();
        faturamento = dis.readFloat();
    }

    // ---------------- Getters e Setters ----------------

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String[] getEmail() { return email; }
    public void setEmail(String[] email) { this.email = email; }

    public LocalDate getData_contratacao() { return data_contratacao; }
    public void setData_contratacao(LocalDate data_contratacao) { this.data_contratacao = data_contratacao; }

    public int getNumero_vendas() { return numero_vendas; }
    public void setNumero_vendas(int numero_vendas) { this.numero_vendas = numero_vendas; }

    public float getFaturamento() { return faturamento; }
    public void setFaturamento(float faturamento) { this.faturamento = faturamento; }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\nCPF: ").append(cpf);
        sb.append("\nNome: ").append(nome);
        sb.append("\nData de contratação: ").append(data_contratacao.format(df));
        sb.append("\nE-mails: ");
        for (String e : email) sb.append(e).append(" ");
        sb.append("\nNúmero de vendas: ").append(numero_vendas);
        sb.append("\nFaturamento: R$ ").append(faturamento);
        return sb.toString();
    }
}