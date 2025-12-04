package view;  // ← IMPORTANTE: deve começar assim

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.VendedorDAO;
import model.Vendedor;
import java.util.ArrayList;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LoginFrame extends JFrame {
    private JTextField txtCpf;
    private JPasswordField txtSenha;
    private JButton btnLogin, btnCadastrar;
    private VendedorDAO vendedorDAO;
    
    public LoginFrame() {
        try {
            vendedorDAO = new VendedorDAO();
            initComponents();
            configurarJanela();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar sistema: " + e.getMessage(), 
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Painel de título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(44, 62, 80));
        JLabel lblTitulo = new JLabel("SISTEMA DE GERENCIAMENTO DE VENDAS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        painelTitulo.add(lblTitulo);
        add(painelTitulo, BorderLayout.NORTH);
        
        // Painel central
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // TÍTULO + LOGO (carro)
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;
        
        JPanel tituloLogoPanel = new JPanel();
        tituloLogoPanel.setLayout(new BoxLayout(tituloLogoPanel, BoxLayout.Y_AXIS));
        tituloLogoPanel.setBackground(Color.WHITE);
        
        // Título em duas linhas
        JPanel painelTituloApp = new JPanel();
        painelTituloApp.setLayout(new GridLayout(2, 1, 0, 2));
        painelTituloApp.setBackground(Color.WHITE);
        
        JLabel lblGerente = new JLabel("GERENCIADOR", JLabel.CENTER);
        lblGerente.setFont(new Font("Arial", Font.BOLD, 28));
        lblGerente.setForeground(new Color(41, 128, 185));
        
        JLabel lblVendas = new JLabel("DE VENDAS", JLabel.CENTER);
        lblVendas.setFont(new Font("Arial", Font.BOLD, 24));
        lblVendas.setForeground(new Color(52, 152, 219));
        
        painelTituloApp.add(lblGerente);
        painelTituloApp.add(lblVendas);
        
        // Logo (imagem do carro)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.WHITE);
        
        try {
            // Tenta carregar a imagem PNG do carro
            File logoFile = new File("insumos/logo.png");
            if (logoFile.exists()) {
                // Carrega a imagem com ImageIO
                BufferedImage originalImage = ImageIO.read(logoFile);
                
                // Tamanho para a imagem do carro
                int novaAltura = 120; // Altura maior para o carro
                int novaLargura = (originalImage.getWidth() * novaAltura) / originalImage.getHeight();
                
                // Redimensiona com qualidade
                ImageIcon logoIcon = criarLogoComQualidade(logoFile, novaLargura, novaAltura);
                
                if (logoIcon != null) {
                    JLabel logoLabel = new JLabel(logoIcon);
                    logoPanel.add(logoLabel);
                } else {
                    // Fallback se não conseguir carregar
                    JLabel textLabel = new JLabel("🚗");
                    textLabel.setFont(new Font("Arial", Font.BOLD, 48));
                    textLabel.setForeground(new Color(231, 76, 60));
                    logoPanel.add(textLabel);
                }
            } else {
                // Se não encontrar a imagem, usa emoji de carro
                JLabel textLabel = new JLabel("🚗");
                textLabel.setFont(new Font("Arial", Font.BOLD, 48));
                textLabel.setForeground(new Color(231, 76, 60));
                logoPanel.add(textLabel);
            }
        } catch (Exception e) {
            // Fallback para emoji de carro
            JLabel textLabel = new JLabel("🚗");
            textLabel.setFont(new Font("Arial", Font.BOLD, 48));
            textLabel.setForeground(new Color(231, 76, 60));
            logoPanel.add(textLabel);
        }
        
        // Adiciona os componentes ao painel principal
        tituloLogoPanel.add(painelTituloApp);
        tituloLogoPanel.add(Box.createVerticalStrut(10)); // Espaço entre título e logo
        tituloLogoPanel.add(logoPanel);
        
        painelCentral.add(tituloLogoPanel, gbc);
        
        // CPF
        gbc.gridy = 1; 
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel lblCpf = new JLabel("CPF:");
        lblCpf.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCentral.add(lblCpf, gbc);
            
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        txtCpf.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCentral.add(txtCpf, gbc);
        
        // Senha
        gbc.gridx = 0; 
        gbc.gridy = 2;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCentral.add(lblSenha, gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCentral.add(txtSenha, gbc);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelBotoes.setBackground(Color.WHITE);
        
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(120, 40));
        btnLogin.addActionListener(e -> fazerLogin());
        
        btnCadastrar = new JButton("Cadastrar Vendedor");
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCadastrar.setBackground(new Color(52, 152, 219));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setPreferredSize(new Dimension(180, 40));
        btnCadastrar.addActionListener(e -> abrirCadastroVendedor());
        
        // Configurações visuais dos botões
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        
        btnCadastrar.setOpaque(true);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setFocusPainted(false);
        
        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCadastrar);
        
        gbc.gridx = 0; 
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        painelCentral.add(painelBotoes, gbc);
        
        // Adiciona listener para Enter
        txtSenha.addActionListener(e -> fazerLogin());
        
        add(painelCentral, BorderLayout.CENTER);
        
        // Rodapé
        JLabel lblRodape = new JLabel("Sistema de Gerenciamento de Vendas © 2024", JLabel.CENTER);
        lblRodape.setFont(new Font("Arial", Font.ITALIC, 12));
        lblRodape.setForeground(Color.GRAY);
        add(lblRodape, BorderLayout.SOUTH);
    }
    
    // Método para criar logo com alta qualidade
    private ImageIcon criarLogoComQualidade(File arquivoLogo, int largura, int altura) {
        try {
            BufferedImage originalImage = ImageIO.read(arquivoLogo);
            
            // Para imagens, use TYPE_INT_ARGB (suporta transparência)
            BufferedImage resizedImage = new BufferedImage(
                largura, 
                altura, 
                BufferedImage.TYPE_INT_ARGB
            );
            
            Graphics2D g2d = resizedImage.createGraphics();
            
            // Configurações de alta qualidade
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            
            // Desenha a imagem redimensionada
            g2d.drawImage(originalImage, 0, 0, largura, altura, null);
            g2d.dispose();
            
            return new ImageIcon(resizedImage);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private void configurarJanela() {
        setTitle("Login - Sistema de Vendas");
        setSize(600, 650); // Aumentei a altura para 650 para acomodar o título
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void fazerLogin() {
        String cpf = txtCpf.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        if (cpf.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha CPF e senha!", 
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            Vendedor vendedorLogado = null;
            
            for (Vendedor v : vendedores) {
                if (v.getCpf().equals(cpf)) {
                    vendedorLogado = v;
                    break;
                }
            }
            
            if (vendedorLogado == null) {
                JOptionPane.showMessageDialog(this, "Vendedor não encontrado!", 
                                             "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (vendedorLogado.getSenha().equals(senha)) {
                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!\nBem-vindo, " + 
                                             vendedorLogado.getNome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                // Abre a tela principal
                new MainFrame(vendedorLogado, vendedorDAO).setVisible(true);
                dispose(); // Fecha a tela de login
            } else {
                JOptionPane.showMessageDialog(this, "Senha incorreta!", 
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao fazer login: " + e.getMessage(), 
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirCadastroVendedor() {
        new CadastroVendedorFrame(vendedorDAO, this).setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}