package model;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.Registro;
import util.CriptografiaRSA;

public class Vendedor implements Registro {

    private String cpf; //cpf pk
    private String nome;
    private String[] email;
    private LocalDate data_contratacao;
    private int numero_vendas;
    private float faturamento;
    private String senha; // senha sera cripitografada

    private static CriptografiaRSA criptografia;
    
    static {
        try {
            criptografia = new CriptografiaRSA();
            System.out.println("> Criptografia RSA inicializada para Vendedor");
        } catch (Exception e) {
            System.err.println("> Criptografia não disponível para Vendedor: " + e.getMessage());
            criptografia = null;
        }
    }

    public Vendedor() {
        this.cpf = "";
        this.nome = "";
        this.email = new String[0];
        this.data_contratacao = LocalDate.now();
        this.numero_vendas = 0;
        this.faturamento = 0f;
        this.senha = "";
    }

    public Vendedor(String cpf, String nome, String[] email, LocalDate data_contratacao, 
                   int numero_vendas, float faturamento, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.data_contratacao = data_contratacao;
        this.numero_vendas = numero_vendas;
        this.faturamento = faturamento;
        this.senha = senha;
    }

    @Override
    public int getId() {
        return Math.abs(cpf.hashCode());
    }

    @Override
    public void setId(int id) {
        // Não utilizado pois o ID é baseado no CPF
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
        
        // SENHA: COM criptografia
        String senhaParaSalvar = senha;
        if (criptografia != null && criptografia.isChavesCarregadas() && 
            senha != null && !senha.trim().isEmpty()) {
            try {
                senhaParaSalvar = criptografia.criptografar(senha.trim());
                System.out.println("> Senha criptografada para: " + nome);
            } catch (Exception e) {
                System.err.println("> Falha ao criptografar senha: " + e.getMessage());
                // Mantém original em caso de erro
            }
        }
        dos.writeUTF(senhaParaSalvar);

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
        
        // SENHA: COM descriptografia
        String senhaLida = dis.readUTF();
        this.senha = senhaLida; // Valor padrão
        
        if (criptografia != null && criptografia.isChavesCarregadas() && 
            senhaLida != null && !senhaLida.trim().isEmpty()) {
            try {
                // Se contém ponto-e-vírgula, está criptografada no nosso formato
                if (senhaLida.contains(";")) {
                    this.senha = criptografia.descriptografar(senhaLida);
                    System.out.println("> Senha descriptografada para: " + nome);
                } else {
                    // Não está criptografada, mantém original
                    System.out.println(">  Senha em texto plano para: " + nome);
                }
            } catch (Exception e) {
                System.err.println("> Falha ao descriptografar senha, mantendo texto plano: " + e.getMessage());
                this.senha = senhaLida;
            }
        }
    }

    // GETTERS E SETTERS
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

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    /**
     * Método para verificar se a senha está correta
     */
    public boolean verificarSenha(String senhaDigitada) {
        if (criptografia != null && criptografia.isChavesCarregadas()) {
            try {
                // Se a senha armazenada está criptografada, descriptografa para comparar
                String senhaArmazenada = this.senha;
                if (senhaArmazenada.contains(";")) {
                    senhaArmazenada = criptografia.descriptografar(senhaArmazenada);
                }
                return senhaDigitada.equals(senhaArmazenada);
            } catch (Exception e) {
                System.err.println("> Erro ao verificar senha: " + e.getMessage());
                return false;
            }
        }
        return senhaDigitada.equals(this.senha);
    }

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
        sb.append("\nSenha: [PROTEGIDA]");
        return sb.toString();
    }
}