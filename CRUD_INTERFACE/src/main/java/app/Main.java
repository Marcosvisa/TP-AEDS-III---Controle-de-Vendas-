package app;

import view.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE CONCESSIONÁRIA ===");
        
        // Configuração AVANÇADA do Look and Feel
        configurarAparencia();
        
        // Inicia a aplicação
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                System.out.println("✅ Sistema iniciado com sucesso!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao iniciar sistema: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar sistema:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private static void configurarAparencia() {
        try {
            // Tenta usar o Look and Feel do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configurações ESPECÍFICAS para botões
            UIManager.put("Button.background", new Color(52, 152, 219)); // Azul padrão
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focus", new Color(41, 128, 185));
            UIManager.put("Button.select", new Color(41, 128, 185));
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
            
            // Remove o foco pintado (aquela borda pontilhada)
            UIManager.put("Button.focusPainted", false);
            
            // Configura fonte para botões
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
            
            System.out.println("✅ Look and Feel configurado");
            
        } catch (Exception e) {
            System.err.println("⚠️  Não foi possível configurar aparência: " + e.getMessage());
            // Usa padrão do Swing
        }
    }
}