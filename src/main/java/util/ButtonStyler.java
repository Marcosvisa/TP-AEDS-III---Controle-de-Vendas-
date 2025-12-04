//classe para corrigir bug de botes sem cor

package util;

import javax.swing.*;
import java.awt.*;

public class ButtonStyler {
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        aplicarEstiloCompleto(button, backgroundColor);
        return button;
    }
    
    //método para aplicar estilo completo aos botões
    public static void aplicarEstiloCompleto(JButton botao, Color corFundo) {
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(corFundo.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(corFundo);
            }
        });
    }
    
    //métodos específicos para cada tipo de botão
    public static void estiloSucesso(JButton botao) {
        aplicarEstiloCompleto(botao, COLOR_SUCCESS);
    }
    
    public static void estiloPrimario(JButton botao) {
        aplicarEstiloCompleto(botao, COLOR_PRIMARY);
    }
    
    public static void estiloPerigo(JButton botao) {
        aplicarEstiloCompleto(botao, COLOR_DANGER);
    }
    
    public static void estiloAviso(JButton botao) {
        aplicarEstiloCompleto(botao, COLOR_WARNING);
    }
    
    public static void estiloSecundario(JButton botao) {
        aplicarEstiloCompleto(botao, COLOR_SECONDARY);
    }
    
    //cores padrão
    public static final Color COLOR_SUCCESS = new Color(46, 204, 113);  // Verde
    public static final Color COLOR_PRIMARY = new Color(52, 152, 219);  // Azul
    public static final Color COLOR_DANGER = new Color(231, 76, 60);    // Vermelho
    public static final Color COLOR_WARNING = new Color(241, 196, 15);  // Amarelo
    public static final Color COLOR_SECONDARY = new Color(149, 165, 166); // Cinza
}