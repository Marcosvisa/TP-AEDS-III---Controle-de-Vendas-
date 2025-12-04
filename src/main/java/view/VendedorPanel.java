package view;

import util.ButtonStyler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.VendedorDAO;
import model.Vendedor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class VendedorPanel extends JPanel {
    private VendedorDAO vendedorDAO;
    private JTable tabelaVendedores;
    private DefaultTableModel tableModel;
    private JTextField txtCpf, txtNome, txtSenha;
    private JTextArea txtEmails;
    private JButton btnIncluir, btnAtualizar, btnExcluir, btnLimpar, btnBuscar;
    
    public VendedorPanel(VendedorDAO vendedorDAO) {
        this.vendedorDAO = vendedorDAO;
        initComponents();
        carregarVendedores();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // painel de forulario a esquerda
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Cadastro de Vendedor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // CPF
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("CPF:"), gbc);
        
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        painelFormulario.add(txtCpf, gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);
        
        // Emails
        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Emails (um por linha):"), gbc);
        
        gbc.gridx = 1;
        txtEmails = new JTextArea(3, 20);
        txtEmails.setLineWrap(true);
        JScrollPane scrollEmails = new JScrollPane(txtEmails);
        painelFormulario.add(scrollEmails, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        painelFormulario.add(txtSenha, gbc);
        
        // Painel de botões do formulário
        // No método initComponents():

JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

btnIncluir = ButtonStyler.createStyledButton("Incluir", ButtonStyler.COLOR_SUCCESS);
btnIncluir.addActionListener(e -> incluirVendedor());  // ← DEVE SER incluirVendedor()

btnAtualizar = ButtonStyler.createStyledButton("Atualizar", ButtonStyler.COLOR_PRIMARY);
btnAtualizar.addActionListener(e -> atualizarVendedor());  // ← DEVE SER atualizarVendedor()

btnExcluir = ButtonStyler.createStyledButton("Excluir", ButtonStyler.COLOR_DANGER);
btnExcluir.addActionListener(e -> excluirVendedor());  // ← DEVE SER excluirVendedor()

btnLimpar = ButtonStyler.createStyledButton("Limpar", ButtonStyler.COLOR_SECONDARY);
btnLimpar.addActionListener(e -> limparCampos());

painelBotoesForm.add(btnIncluir);
painelBotoesForm.add(btnAtualizar);
painelBotoesForm.add(btnExcluir);
painelBotoesForm.add(btnLimpar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        painelFormulario.add(painelBotoesForm, gbc);
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar por CPF"));
        
        JTextField txtBuscaCpf = new JTextField(15);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarVendedor(txtBuscaCpf.getText()));
        
        painelBusca.add(new JLabel("CPF:"));
        painelBusca.add(txtBuscaCpf);
        painelBusca.add(btnBuscar);
        
        // Painel esquerdo combinado
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.add(painelFormulario, BorderLayout.NORTH);
        painelEsquerdo.add(painelBusca, BorderLayout.SOUTH);
        
        // Tabela de vendedores (direita)
        String[] colunas = {"CPF", "Nome", "Emails", "Data Contratação", "Vendas", "Faturamento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaVendedores = new JTable(tableModel);
        tabelaVendedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarVendedorNaTabela();
            }
        });
        
        JScrollPane scrollTabela = new JScrollPane(tabelaVendedores);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Vendedores Cadastrados"));
        
        // Botões da tabela
        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRecarregar = new JButton("Recarregar");
        btnRecarregar.addActionListener(e -> carregarVendedores());
        painelBotoesTabela.add(btnRecarregar);
        
        // Painel direito combinado
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.add(scrollTabela, BorderLayout.CENTER);
        painelDireito.add(painelBotoesTabela, BorderLayout.SOUTH);
        
        // Adiciona os painéis principais
        add(painelEsquerdo, BorderLayout.WEST);
        add(painelDireito, BorderLayout.CENTER);
        
        // Configura tamanhos
        painelEsquerdo.setPreferredSize(new Dimension(400, getHeight()));
    }
    
    private void carregarVendedores() {
        try {
            tableModel.setRowCount(0);
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Vendedor v : vendedores) {
                String emails = String.join(", ", v.getEmail());
                tableModel.addRow(new Object[]{
                    v.getCpf(),
                    v.getNome(),
                    emails,
                    v.getData_contratacao().format(df),
                    v.getNumero_vendas(),
                    String.format("R$ %.2f", v.getFaturamento())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendedores: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selecionarVendedorNaTabela() {
        int linhaSelecionada = tabelaVendedores.getSelectedRow();
        if (linhaSelecionada >= 0) {
            String cpf = (String) tableModel.getValueAt(linhaSelecionada, 0);
            
            try {
                ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
                for (Vendedor v : vendedores) {
                    if (v.getCpf().equals(cpf)) {
                        txtCpf.setText(v.getCpf());
                        txtNome.setText(v.getNome());
                        
                        StringBuilder emails = new StringBuilder();
                        for (String email : v.getEmail()) {
                            emails.append(email).append("\n");
                        }
                        txtEmails.setText(emails.toString().trim());
                        
                        txtSenha.setText(""); // Senha não é exibida por segurança
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar vendedor: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void incluirVendedor() {
        String cpf = txtCpf.getText().trim();
        String nome = txtNome.getText().trim();
        String senha = new String(((JPasswordField) txtSenha).getPassword());
        
        if (cpf.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha CPF, nome e senha!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Verifica se já existe
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
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
            
            Vendedor novoVendedor = new Vendedor(cpf, nome, emails, LocalDate.now(), 0, 0f, senha);
            vendedorDAO.create(novoVendedor);
            
            JOptionPane.showMessageDialog(this, "Vendedor incluído com sucesso!",
                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            limparCampos();
            carregarVendedores();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao incluir vendedor: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarVendedor() {
        String cpf = txtCpf.getText().trim();
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para atualizar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            Vendedor vendedor = null;
            
            for (Vendedor v : vendedores) {
                if (v.getCpf().equals(cpf)) {
                    vendedor = v;
                    break;
                }
            }
            
            if (vendedor == null) {
                JOptionPane.showMessageDialog(this, "Vendedor não encontrado!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Atualiza dados
            vendedor.setNome(txtNome.getText().trim());
            
            String[] emails = txtEmails.getText().split("\\r?\\n");
            for (int i = 0; i < emails.length; i++) {
                emails[i] = emails[i].trim();
            }
            vendedor.setEmail(emails);
            
            String senha = new String(((JPasswordField) txtSenha).getPassword());
            if (!senha.isEmpty()) {
                vendedor.setSenha(senha);
            }
            
            if (vendedorDAO.update(vendedor)) {
                JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!",
                                             "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarVendedores();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar vendedor.",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar vendedor: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirVendedor() {
        String cpf = txtCpf.getText().trim();
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para excluir!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir o vendedor com CPF " + cpf + "?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
                for (Vendedor v : vendedores) {
                    if (v.getCpf().equals(cpf)) {
                        if (vendedorDAO.delete(v.getId())) {
                            JOptionPane.showMessageDialog(this, "Vendedor excluído com sucesso!",
                                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            limparCampos();
                            carregarVendedores();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir vendedor: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarVendedor(String cpf) {
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF para buscar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            Vendedor encontrado = null;
            
            for (Vendedor v : vendedores) {
                if (v.getCpf().equals(cpf)) {
                    encontrado = v;
                    break;
                }
            }
            
            if (encontrado != null) {
                // Seleciona na tabela
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(cpf)) {
                        tabelaVendedores.setRowSelectionInterval(i, i);
                        tabelaVendedores.scrollRectToVisible(tabelaVendedores.getCellRect(i, 0, true));
                        break;
                    }
                }
                
                // Mostra mensagem
                JOptionPane.showMessageDialog(this,
                    "Vendedor encontrado:\n" +
                    "Nome: " + encontrado.getNome() + "\n" +
                    "Vendas: " + encontrado.getNumero_vendas() + "\n" +
                    "Faturamento: R$ " + encontrado.getFaturamento(),
                    "Vendedor Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Vendedor com CPF " + cpf + " não encontrado.",
                                             "Não Encontrado", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparCampos() {
        txtCpf.setText("");
        txtNome.setText("");
        txtEmails.setText("");
        txtSenha.setText("");
        tabelaVendedores.clearSelection();
    }
}