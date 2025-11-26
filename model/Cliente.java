package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;
import util.CriptografiaRSA;

public class Cliente implements Registro {

    private String cpf;
    private String nome;
    private String[] email;
    private LocalDate data_cadastro;
    private String telefone;

    private static CriptografiaRSA criptografia;
    
    static {
        try {
            criptografia = new CriptografiaRSA();
          //  System.out.println("✅ Criptografia RSA inicializada para Cliente");
        } catch (Exception e) {
            System.err.println("❌ Criptografia não disponível: " + e.getMessage());
            criptografia = null;
        }
    }

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

        // ⭐ ORDEM CORRETA DE ESCRITA
        dos.writeUTF(cpf);
        dos.writeUTF(nome);
        
        // Emails
        dos.writeInt(email.length);
        for (String e : email) {
            dos.writeUTF(e);
        }
        
        // Data (SEM criptografia)
        dos.writeUTF(data_cadastro.toString());
        
        // Telefone (COM criptografia)
        String telefoneParaSalvar = telefone;
        if (criptografia != null && criptografia.isChavesCarregadas() && 
            telefone != null && !telefone.trim().isEmpty()) {
            try {
                telefoneParaSalvar = criptografia.criptografar(telefone.trim());
                //System.out.println("🔒 Telefone criptografado: " + telefone + " → [CRIPTOGRAFADO]");
            } catch (Exception e) {
                System.err.println("❌ Falha ao criptografar telefone: " + e.getMessage());
                // Mantém original em caso de erro
            }
        }
        dos.writeUTF(telefoneParaSalvar);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // ⭐ ORDEM CORRETA DE LEITURA (DEVE SER A MESMA DA ESCRITA)
        cpf = dis.readUTF();
        nome = dis.readUTF();
        
        // Emails
        int n = dis.readInt();
        email = new String[n];
        for (int i = 0; i < n; i++) {
            email[i] = dis.readUTF();
        }
        
        // Data (SEM criptografia)
        String dataStr = dis.readUTF();
        try {
            data_cadastro = LocalDate.parse(dataStr);
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler data: " + dataStr);
            data_cadastro = LocalDate.now();
        }
        
        // Telefone (COM criptografia)
        String telefoneLido = dis.readUTF();
        this.telefone = telefoneLido; // Valor padrão
        
        if (criptografia != null && criptografia.isChavesCarregadas() && 
            telefoneLido != null && !telefoneLido.trim().isEmpty()) {
            try {
                // Tenta descriptografar - se falhar, mantém o texto original
                if (telefoneLido.length() > 50 && telefoneLido.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                    this.telefone = criptografia.descriptografar(telefoneLido);
                   // System.out.println("🔓 Telefone descriptografado: [CRIPTOGRAFADO] → " + this.telefone);
                } else {
                    // Não está criptografado, mantém original
                    System.out.println("ℹ️  Telefone em texto plano: " + telefoneLido);
                }
            } catch (Exception e) {
                System.err.println("❌ Falha ao descriptografar, mantendo texto plano: " + e.getMessage());
                this.telefone = telefoneLido;
            }
        }

       // System.out.println("📋 Cliente carregado - CPF: " + cpf + ", Tel: " + this.telefone + ", Data: " + data_cadastro);
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
