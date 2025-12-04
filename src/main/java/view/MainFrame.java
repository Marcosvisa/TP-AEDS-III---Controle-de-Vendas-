package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.*;
import model.Vendedor;  

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private Vendedor vendedorLogado;
    private VendedorDAO vendedorDAO;
    private ClienteDAO clienteDAO;
    private CarroDAO carroDAO;
    private VendaDAO vendaDAO;
    private CarroVendaDAO carroVendaDAO;
    private IndiceCarroVenda indiceCarroVenda;
    private IndiceVendedorVendas indiceVendedorVendas;
    private IndiceClienteVendas indiceClienteVendas;
    
    public MainFrame(Vendedor vendedor, VendedorDAO vendedorDAO) {
        this.vendedorLogado = vendedor;
        this.vendedorDAO = vendedorDAO;
        
        try {
            //inicializa todos os DAOs
            clienteDAO = new ClienteDAO();
            carroDAO = new CarroDAO();
            vendaDAO = new VendaDAO();
            carroVendaDAO = new CarroVendaDAO();
            indiceCarroVenda = new IndiceCarroVenda();
            indiceVendedorVendas = new IndiceVendedorVendas();
            indiceClienteVendas = new IndiceClienteVendas();
            
            initComponents();
            configurarJanela();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "erro ao inicializar sistema: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Inicializa o tabbedPane PRIMEIRO
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Barra de menu superior
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Sistema
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> sair());
        menuSistema.add(itemSair);
        menuBar.add(menuSistema);
        
        // Menu Ferramentas
        JMenu menuFerramentas = new JMenu("Ferramentas");
        JMenuItem itemCompressao = new JMenuItem("Compressão de Arquivos");
        itemCompressao.addActionListener(e -> abrirCompressao());
        JMenuItem itemPesquisaPadrao = new JMenuItem("Pesquisa por Padrão");
        itemPesquisaPadrao.addActionListener(e -> abrirPesquisaPadrao());
        menuFerramentas.add(itemCompressao);
        menuFerramentas.add(itemPesquisaPadrao);
        menuBar.add(menuFerramentas);
        
        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e -> mostrarSobre());
        menuAjuda.add(itemSobre);
        menuBar.add(menuAjuda);
        
        setJMenuBar(menuBar);
        
       // ===== CABEÇALHO ORGANIZADO PROFISSIONAL =====
JPanel painelCabecalho = new JPanel(new GridBagLayout());
painelCabecalho.setBackground(new Color(52, 152, 219));
painelCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

GridBagConstraints h = new GridBagConstraints();
h.insets = new Insets(5, 10, 5, 10);
h.fill = GridBagConstraints.HORIZONTAL;

// ===== TÍTULO (ESQUERDA) =====
h.gridx = 0;
h.gridy = 0;
h.weightx = 1;
h.anchor = GridBagConstraints.WEST;

JLabel lblTitulo = new JLabel("SISTEMA DE GERENCIAMENTO DE VENDAS");
lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
lblTitulo.setForeground(Color.WHITE);
painelCabecalho.add(lblTitulo, h);

// ===== USUÁRIO =====
h.gridx = 1;
h.weightx = 0;
h.anchor = GridBagConstraints.EAST;

JLabel lblUsuario = new JLabel("Usuário: " + vendedorLogado.getNome());
lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
lblUsuario.setForeground(Color.WHITE);
painelCabecalho.add(lblUsuario, h);

// ===== BOTÃO PESQUISA =====
h.gridx = 2;

JButton btnPesquisa = new JButton("🔍 Pesquisa");
btnPesquisa.setBackground(new Color(155, 89, 182));
btnPesquisa.setForeground(Color.WHITE);
btnPesquisa.setFocusPainted(false);
btnPesquisa.setBorderPainted(false);
btnPesquisa.addActionListener(e -> abrirPesquisaPadrao());
painelCabecalho.add(btnPesquisa, h);

// ===== BOTÃO COMPRESSÃO =====
h.gridx = 3;

JButton btnCompressao = new JButton("🗜️ Compressão");
btnCompressao.setBackground(new Color(41, 128, 185));
btnCompressao.setForeground(Color.WHITE);
btnCompressao.setFocusPainted(false);
btnCompressao.setBorderPainted(false);
btnCompressao.addActionListener(e -> abrirCompressao());
painelCabecalho.add(btnCompressao, h);

// ADD NO TOPO
add(painelCabecalho, BorderLayout.NORTH);
        
    
        try {
            // Adiciona as abas principais
            tabbedPane.addTab("🏢 Vendedores", new VendedorPanel(vendedorDAO));
            tabbedPane.addTab("👥 Clientes", new ClientePanel(clienteDAO));
            tabbedPane.addTab("🚗 Carros", new CarroPanel(carroDAO));
            tabbedPane.addTab("💰 Vendas", new VendaPanel(vendaDAO, clienteDAO, vendedorDAO, carroDAO, 
                                                         indiceVendedorVendas, indiceClienteVendas, 
                                                         indiceCarroVenda));
            tabbedPane.addTab("🔗 Relações", new RelacaoPanel(carroVendaDAO, carroDAO, vendaDAO, indiceCarroVenda));
            tabbedPane.addTab("🗜️ Compressão", new CompressaoPanel());
            tabbedPane.addTab("🔍 Pesquisa", new PesquisaPadraoPanel(carroDAO));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar abas: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Rodapé
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(new Color(236, 240, 241));
        painelRodape.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel lblStatus = new JLabel("Sistema pronto. Vendas: " + vendedorLogado.getNumero_vendas() + 
                                     " | Faturamento: R$ " + vendedorLogado.getFaturamento());
        painelRodape.add(lblStatus);
        
        add(painelRodape, BorderLayout.SOUTH);
    }
    
    private void configurarJanela() {
        setTitle("Sistema de Vendas - Concessionária - " + vendedorLogado.getNome());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Configura ação de fechamento
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharRecursos();
            }
        });
    }
    
    private void abrirCompressao() {
        try {
            tabbedPane.setSelectedIndex(5); //indice da aba Compressão
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Sistema de compressão disponível na aba '🗜️ Compressão'",
                "Informação",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirPesquisaPadrao() {
        try {
            tabbedPane.setSelectedIndex(6); // Índice da aba Pesquisa
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Sistema de pesquisa disponível na aba '🔍 Pesquisa'",
                "Informação",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this,
            "Sistema de Gerenciamento de Vendas - Concessionária\n" +
            "Versão 1.0\n\n" +
            "Funcionalidades:\n" +
            "- CRUD de Vendedores, Clientes, Carros e Vendas\n" +
            "- Relacionamentos N:N entre Carros e Vendas\n" +
            "- Índices com Hash Extensível e B+ Tree\n" +
            "- Compressão de arquivos (Huffman/LZW)\n" +
            "- Pesquisa por padrão (KMP/Boyer-Moore)\n\n" +
            "© 2024 - Todos os direitos reservados",
            "Sobre o Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void sair() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja realmente sair do sistema?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            fecharRecursos();
            System.exit(0);
        }
    }
    
    private void fecharRecursos() {
        try {
            if (vendedorDAO != null) vendedorDAO.close();
            if (clienteDAO != null) clienteDAO.close();
            if (carroDAO != null) carroDAO.close();
            if (vendaDAO != null) vendaDAO.close();
            if (carroVendaDAO != null) carroVendaDAO.close();
            if (indiceCarroVenda != null) indiceCarroVenda.close();
            if (indiceVendedorVendas != null) indiceVendedorVendas.close();
            if (indiceClienteVendas != null) indiceClienteVendas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}