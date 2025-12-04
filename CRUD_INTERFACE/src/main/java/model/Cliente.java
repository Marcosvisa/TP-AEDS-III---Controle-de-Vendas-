package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;

public class Cliente implements Registro {

    private String cpf;
    private String nome;
    private String[] email;
    private LocalDate data_cadastro;
    private String telefone;

    public Cliente() {
        this.cpf = "";
        this.nome = "";
        this.email = new String[0];
        this.data_cadastro = LocalDate.now();
        this.telefone = "";
    }

    public Cliente(String cpf, String nome, String[] email, LocalDate data_cadastro, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.data_cadastro = data_cadastro;
        this.telefone = telefone;
    }

    @Override
    public int getId() {
        return Math.abs(cpf.hashCode());
    }

    @Override
    public void setId(int id) {
        // Mantido vazio
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
        
        dos.writeUTF(data_cadastro.toString());
        dos.writeUTF(telefone); // SEM criptografia

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        cpf = dis.readUTF();
        nome = dis.readUTF();
        
        int n = dis.readInt();
        email = new String[n];
        for (int i = 0; i < n; i++) {
            email[i] = dis.readUTF();
        }
        
        String dataStr = dis.readUTF();
        try {
            data_cadastro = LocalDate.parse(dataStr);
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler data: " + dataStr);
            data_cadastro = LocalDate.now();
        }
        
        telefone = dis.readUTF(); // SEM criptografia
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String[] getEmail() { return email; }
    public void setEmail(String[] email) { this.email = email; }
    public LocalDate getData_cadastro() { return data_cadastro; }
    public void setData_cadastro(LocalDate data_cadastro) { this.data_cadastro = data_cadastro; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\nCPF: ").append(cpf);
        sb.append("\nNome: ").append(nome);
        sb.append("\nE-mails: ");
        for (String e : email) sb.append(e).append(" ");
        sb.append("\nData de cadastro: ").append(data_cadastro.format(df));
        sb.append("\nTelefone: ").append(telefone);
        return sb.toString();
    }
}
