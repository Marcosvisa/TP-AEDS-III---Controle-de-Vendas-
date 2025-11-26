package util;
import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;
import java.io.*;

public class CriptografiaRSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final String CHAVES_FILE = "dados/chaves_rsa.dat";
    private boolean chavesCarregadas = false;
    
    public CriptografiaRSA() throws Exception {
        carregarOuGerarChaves();
    }
    
    private void carregarOuGerarChaves() throws Exception {
        File arquivoChaves = new File(CHAVES_FILE);
        
        if (arquivoChaves.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoChaves))) {
                this.privateKey = (PrivateKey) ois.readObject();
                this.publicKey = (PublicKey) ois.readObject();
                this.chavesCarregadas = true;
                //System.out.println("✅ Chaves RSA carregadas com sucesso");
            } catch (Exception e) {
                //System.err.println("❌ Erro ao carregar chaves, gerando novas: " + e.getMessage());
                gerarNovasChaves();
            }
        } else {
            gerarNovasChaves();
        }
    }
    
    private void gerarNovasChaves() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        
        // Garante que a pasta existe
        new File(CHAVES_FILE).getParentFile().mkdirs();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHAVES_FILE))) {
            oos.writeObject(privateKey);
            oos.writeObject(publicKey);
            this.chavesCarregadas = true;
            //System.out.println("✅ Novas chaves RSA geradas e salvas");
        } catch (Exception e) {
            System.err.println("❌ Aviso: Chaves não puderam ser salvas, usando em memória: " + e.getMessage());
        }
    }
    
    public String criptografar(String texto) throws Exception {
        if (!chavesCarregadas || texto == null || texto.isEmpty()) {
            return texto; // ⭐ FALLBACK: retorna texto original
        }
        
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] textoCriptografado = cipher.doFinal(texto.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(textoCriptografado);
        } catch (Exception e) {
            System.err.println("❌ Falha na criptografia, usando texto plano: " + e.getMessage());
            return texto; // ⭐ FALLBACK: retorna texto original
        }
    }
    
    public String descriptografar(String textoCriptografado) throws Exception {
        if (!chavesCarregadas || textoCriptografado == null || textoCriptografado.isEmpty()) {
            return textoCriptografado; // ⭐ FALLBACK: retorna como está
        }
        
        try {
            // Tenta detectar se é Base64 (dado criptografado)
            if (!textoCriptografado.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                return textoCriptografado; // ⭐ Não é Base64, retorna original
            }
            
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] textoBytes = Base64.getDecoder().decode(textoCriptografado);
            byte[] textoDescriptografado = cipher.doFinal(textoBytes);
            return new String(textoDescriptografado, "UTF-8");
            
        } catch (Exception e) {
            System.err.println("❌ Falha na descriptografia, usando texto original: " + e.getMessage());
            return textoCriptografado; // ⭐ FALLBACK: retorna texto como está
        }
    }
    
    public boolean isChavesCarregadas() {
        return chavesCarregadas;
    }
}