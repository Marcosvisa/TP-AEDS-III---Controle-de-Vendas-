package util;
import java.math.BigInteger;
import java.io.*;

public class CriptografiaRSA {
    // Chaves RSA (agora serão armazenadas em arquivo)
    private BigInteger n;  // módulo
    private BigInteger e;  // chave pública
    private BigInteger d;  // chave privada
    
    // Parâmetros primos
    private BigInteger p;
    private BigInteger q;
    private BigInteger z;  // φ(n) = (p-1)*(q-1)
    
    private static final String CHAVES_FILE = "dados/chaves_rsa.dat";
    
    public CriptografiaRSA() throws Exception {
        carregarOuGerarChaves();
    }
    
    private void carregarOuGerarChaves() throws Exception {
        File arquivoChaves = new File(CHAVES_FILE);
        
        if (arquivoChaves.exists()) {
            // Tenta carregar chaves do arquivo
            try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoChaves))) {
                n = new BigInteger(dis.readUTF());
                e = new BigInteger(dis.readUTF());
                d = new BigInteger(dis.readUTF());
                p = new BigInteger(dis.readUTF());
                q = new BigInteger(dis.readUTF());
                z = new BigInteger(dis.readUTF());
                System.out.println("> Chaves RSA carregadas do arquivo");
            } catch (Exception ex) {
                System.err.println("> Erro ao carregar chaves, gerando novas...");
                gerarNovasChaves();
            }
        } else {
            gerarNovasChaves();
        }
    }
    
    private void gerarNovasChaves() throws Exception {
        // PASSO 1: Escolher dois números primos (p e q)
        p = new BigInteger("1019");  // primo 1
        q = new BigInteger("1031");  // primo 2
        
        // PASSO 2: Calcular n = p * q
        n = p.multiply(q);  // n ≈ 1,050,589
        
        // PASSO 3: Calcular z = φ(n) = (p-1) * (q-1)
        z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        // PASSO 4: Escolher e (coprimo com z)
        // e comum: 65537 ou 17
        e = new BigInteger("65537");
        
        // Verifica se e é válido
        if (!e.gcd(z).equals(BigInteger.ONE)) {
            e = encontrarEValido(z);
        }
        
        // PASSO 5: Calcular d (inverso modular de e mod z)
        d = e.modInverse(z);
        
        // Salva as chaves no arquivo
        salvarChaves();
        
        System.out.println("=== CHAVES RSA GERADAS ===");
        System.out.println("p (primo 1): " + p);
        System.out.println("q (primo 2): " + q);
        System.out.println("n = p * q: " + n);
        System.out.println("z = (p-1)*(q-1): " + z);
        System.out.println("e (pública): " + e);
        System.out.println("d (privada): " + d);
        System.out.println("==========================");
    }
    
    private BigInteger encontrarEValido(BigInteger z) {
        // Tenta valores comuns
        BigInteger[] candidatos = {
            new BigInteger("3"),
            new BigInteger("5"),
            new BigInteger("17"),
            new BigInteger("257"),
            new BigInteger("65537")
        };
        
        for (BigInteger candidato : candidatos) {
            if (candidato.compareTo(z) < 0 && candidato.gcd(z).equals(BigInteger.ONE)) {
                return candidato;
            }
        }
        
        // Se não encontrar, procura manualmente
        BigInteger candidato = new BigInteger("3");
        while (candidato.compareTo(z) < 0) {
            if (candidato.gcd(z).equals(BigInteger.ONE)) {
                return candidato;
            }
            candidato = candidato.add(BigInteger.ONE);
        }
        
        return new BigInteger("65537");
    }
    
    private void salvarChaves() throws Exception {
        // Garante que a pasta existe
        new File(CHAVES_FILE).getParentFile().mkdirs();
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CHAVES_FILE))) {
            dos.writeUTF(n.toString());
            dos.writeUTF(e.toString());
            dos.writeUTF(d.toString());
            dos.writeUTF(p.toString());
            dos.writeUTF(q.toString());
            dos.writeUTF(z.toString());
            System.out.println("> Chaves salvas em: " + CHAVES_FILE);
        } catch (Exception e) {
            System.err.println("> Aviso: Chaves não puderam ser salvas: " + e.getMessage());
        }
    }
    
    /**
     * Criptografa uma senha usando RSA
     * Fórmula: C = M^e mod n
     * Cada caractere é criptografado individualmente
     */
    public String criptografar(String senha) {
        if (senha == null || senha.isEmpty()) {
            return senha;
        }
        
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < senha.length(); i++) {
            // Converte o caractere para número (código Unicode)
            BigInteger m = BigInteger.valueOf(senha.charAt(i));
            
            // Aplica RSA: c = m^e mod n
            BigInteger c = m.modPow(e, n);
            
            // Adiciona ao resultado (separado por ponto-e-vírgula)
            if (resultado.length() > 0) {
                resultado.append(";");
            }
            resultado.append(c.toString());
        }
        
        return resultado.toString();
    }
    
    /**
     * Descriptografa uma senha criptografada com RSA
     * Fórmula: M = C^d mod n
     */
    public String descriptografar(String senhaCriptografada) {
        if (senhaCriptografada == null || senhaCriptografada.isEmpty()) {
            return senhaCriptografada;
        }
        
        // Se não contém ponto-e-vírgula, não está criptografada no nosso formato
        if (!senhaCriptografada.contains(";")) {
            return senhaCriptografada;
        }
        
        StringBuilder resultado = new StringBuilder();
        String[] partes = senhaCriptografada.split(";");
        
        for (String parte : partes) {
            try {
                BigInteger c = new BigInteger(parte);
                
                // Aplica RSA inverso: m = c^d mod n
                BigInteger m = c.modPow(d, n);
                
                // Converte de volta para caractere
                char caractere = (char) m.intValue();
                resultado.append(caractere);
            } catch (Exception e) {
                // Se falhar, retorna como texto original
                System.err.println("> Erro ao descriptografar parte: " + parte);
                return senhaCriptografada;
            }
        }
        
        return resultado.toString();
    }
    
    /**
     * Método auxiliar para mostrar o passo a passo (didático)
     */
    public void mostrarPassosCriptografia(String senha) {
        System.out.println("\n🔐 PASSO A PASSO DA CRIPTOGRAFIA:");
        System.out.println("Senha original: " + senha);
        
        for (int i = 0; i < senha.length(); i++) {
            char c = senha.charAt(i);
            BigInteger m = BigInteger.valueOf((int) c);
            BigInteger cripto = m.modPow(e, n);
            
            System.out.printf("'%c' (%d) → %d^%d mod %d = %d\n", 
                c, m.intValue(), m, e, n, cripto);
        }
    }
    
    /**
     * Verifica se as chaves estão carregadas
     */
    public boolean isChavesCarregadas() {
        return n != null && e != null && d != null;
    }
    
}