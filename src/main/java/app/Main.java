package app;

import view.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENDAS - CONCESSIONÁRIA ===");
        
        //configuracoes de estilo
        configurarAparencia();
        
        //aqui acorre o gatilho para iniciar o sistema
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                System.out.println("> Sistema iniciado com sucesso!");
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
            //
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            //configurações botões
            UIManager.put("Button.background", new Color(52, 152, 219)); // Azul padrão
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focus", new Color(41, 128, 185));
            UIManager.put("Button.select", new Color(41, 128, 185));
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
            
            UIManager.put("Button.focusPainted", false);
            
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
            
           //system.out.println("botoes estilizados");
            
        } catch (Exception e) {
            System.err.println("⚠️  Não foi possível configurar aparência: " + e.getMessage());
            //usa o padrao do swing
        }
    }
}