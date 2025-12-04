package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.VendedorDAO;
import model.Vendedor;

public class CadastroVendedorFrame extends JDialog {
    private VendedorDAO vendedorDAO;
    private LoginFrame parent;
    
    private JTextField txtCpf, txtNome;
    private JTextArea txtEmails;
    private JPasswordField txtSenha;
    private JButton btnSalvar, btnCancelar;
    
    public CadastroVendedorFrame(VendedorDAO vendedorDAO, LoginFrame parent) {
        super(parent, "Cadastrar Novo Vendedor", true);
        this.vendedorDAO = vendedorDAO;
        this.parent = parent;
        initComponents();
        configurarJanela();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("CADASTRAR NOVO VENDEDOR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(52, 152, 219));
        add(lblTitulo, gbc);
        
        // CPF
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("CPF:"), gbc);
        
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        add(txtCpf, gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        add(txtNome, gbc);
        
        // Emails
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Emails (um por linha):"), gbc);
        
        gbc.gridx = 1;
        txtEmails = new JTextArea(3, 20);
        txtEmails.setLineWrap(true);
        JScrollPane scrollEmails = new JScrollPane(txtEmails);
        add(scrollEmails, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        add(txtSenha, gbc);
        
        // Botões
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvarVendedor());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        
        add(painelBotoes, gbc);
    }
    
    private void configurarJanela() {
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void salvarVendedor() {
        String cpf = txtCpf.getText().trim();
        String nome = txtNome.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        if (cpf.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Verifica se já existe
            var vendedores = vendedorDAO.readAll();
            for (Vendedor v : vendedores) {
                if (v.getCpf().equals(cpf)) {
                    JOptionPane.showMessageDialog(this, "Já existe um vendedor com este CPF!",
                                                 "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Processa emails
            String[] emails = txtEmails.getText().split("\\r?\\n");
            for (int i = 0; i < emails.length; i++) {
                emails[i] = emails[i].trim();
            }
            
            Vendedor novoVendedor = new Vendedor(cpf, nome, emails, 
                                                java.time.LocalDate.now(), 0, 0f, senha);
            vendedorDAO.create(novoVendedor);
            
            JOptionPane.showMessageDialog(this, "Vendedor cadastrado com sucesso!",
                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar vendedor: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}