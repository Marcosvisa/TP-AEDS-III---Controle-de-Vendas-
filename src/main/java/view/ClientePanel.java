package view;

import util.ButtonStyler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.ClienteDAO;
import model.Cliente;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ClientePanel extends JPanel {
    private ClienteDAO clienteDAO;
    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private JTextField txtCpf, txtNome, txtTelefone, txtDataCadastro;
    private JTextArea txtEmails;
    private JButton btnIncluir, btnAtualizar, btnExcluir, btnLimpar, btnBuscar;
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public ClientePanel(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
        initComponents();
        carregarClientes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de formulário (esquerda)
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Cadastro de Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // CPF (chave primária)
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("CPF*:"), gbc);
        
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        painelFormulario.add(txtCpf, gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Nome*:"), gbc);
        
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
        
        // Data Cadastro
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Data Cadastro:"), gbc);
        
        gbc.gridx = 1;
        txtDataCadastro = new JTextField(10);
        txtDataCadastro.setText(LocalDate.now().format(DATE_FORMATTER));
        painelFormulario.add(txtDataCadastro, gbc);
        
        JButton btnHoje = new JButton("Hoje");
        btnHoje.addActionListener(e -> txtDataCadastro.setText(LocalDate.now().format(DATE_FORMATTER)));
        painelFormulario.add(btnHoje, gbc);
        
        // Telefone
        gbc.gridx = 0; gbc.gridy = 4;
        painelFormulario.add(new JLabel("Telefone:"), gbc);
        
        gbc.gridx = 1;
        txtTelefone = new JTextField(15);
        painelFormulario.add(txtTelefone, gbc);
        
        // Painel de botões do formulário
        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

btnIncluir = ButtonStyler.createStyledButton("Incluir", ButtonStyler.COLOR_SUCCESS);
btnIncluir.addActionListener(e -> incluirCliente());  // ← DEVE SER incluirCliente()

btnAtualizar = ButtonStyler.createStyledButton("Atualizar", ButtonStyler.COLOR_PRIMARY);
btnAtualizar.addActionListener(e -> atualizarCliente());

btnExcluir = ButtonStyler.createStyledButton("Excluir", ButtonStyler.COLOR_DANGER);
btnExcluir.addActionListener(e -> excluirCliente());

btnLimpar = ButtonStyler.createStyledButton("Limpar", ButtonStyler.COLOR_SECONDARY);
btnLimpar.addActionListener(e -> limparCampos());

painelBotoesForm.add(btnIncluir);
painelBotoesForm.add(btnAtualizar);
painelBotoesForm.add(btnExcluir);
painelBotoesForm.add(btnLimpar);

gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
painelFormulario.add(painelBotoesForm, gbc);
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new BorderLayout(10, 5));
painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Cliente"));
painelBusca.setPreferredSize(new Dimension(380, 90));

JPanel painelBuscaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
JLabel lblBuscaCpf = new JLabel("CPF:");
JTextField txtBuscaCpf = new JTextField(15);
JButton btnBuscarCpf = ButtonStyler.createStyledButton("Buscar por CPF", ButtonStyler.COLOR_PRIMARY);
btnBuscarCpf.addActionListener(e -> buscarClientePorCpf(txtBuscaCpf.getText()));

painelBuscaSuperior.add(lblBuscaCpf);
painelBuscaSuperior.add(txtBuscaCpf);
painelBuscaSuperior.add(btnBuscarCpf);
painelBusca.add(painelBuscaSuperior, BorderLayout.NORTH);

JPanel painelBuscaInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
JButton btnBuscarNome = ButtonStyler.createStyledButton("Buscar por Nome", ButtonStyler.COLOR_PRIMARY);
btnBuscarNome.addActionListener(e -> buscarClientePorNome());
painelBuscaInferior.add(btnBuscarNome);
painelBusca.add(painelBuscaInferior, BorderLayout.SOUTH);
    
    // Painel esquerdo combinado
    JPanel painelEsquerdo = new JPanel();
painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
painelEsquerdo.add(painelFormulario);
painelEsquerdo.add(Box.createVerticalStrut(10));
painelEsquerdo.add(painelBusca);
painelEsquerdo.setPreferredSize(new Dimension(400, 700));
        
        // Tabela de clientes (direita)
        String[] colunas = {"CPF", "Nome", "Emails", "Telefone", "Data Cadastro"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarClienteNaTabela();
            }
        });
        
        // Ajusta largura das colunas
        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(120); // CPF
        tabelaClientes.getColumnModel().getColumn(1).setPreferredWidth(150); // Nome
        tabelaClientes.getColumnModel().getColumn(2).setPreferredWidth(150); // Emails
        tabelaClientes.getColumnModel().getColumn(3).setPreferredWidth(100); // Telefone
        tabelaClientes.getColumnModel().getColumn(4).setPreferredWidth(100); // Data
        
        JScrollPane scrollTabela = new JScrollPane(tabelaClientes);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
        
        // Botões da tabela
        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRecarregar = new JButton("Recarregar Tabela");
        btnRecarregar.addActionListener(e -> carregarClientes());
        
        JButton btnEstatisticas = new JButton("Estatísticas");
        btnEstatisticas.addActionListener(e -> mostrarEstatisticas());
        
        painelBotoesTabela.add(btnRecarregar);
        painelBotoesTabela.add(btnEstatisticas);
        
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
    
    private void carregarClientes() {
        try {
            tableModel.setRowCount(0);
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            
            for (Cliente c : clientes) {
                String emails = String.join(", ", c.getEmail());
                String dataFormatada = c.getData_cadastro().format(DATE_FORMATTER);
                
                tableModel.addRow(new Object[]{
                    c.getCpf(),
                    c.getNome(),
                    emails,
                    c.getTelefone(),
                    dataFormatada
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selecionarClienteNaTabela() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada >= 0) {
            String cpf = (String) tableModel.getValueAt(linhaSelecionada, 0);
            
            try {
                ArrayList<Cliente> clientes = clienteDAO.readAll();
                for (Cliente c : clientes) {
                    if (c.getCpf().equals(cpf)) {
                        txtCpf.setText(c.getCpf());
                        txtNome.setText(c.getNome());
                        
                        StringBuilder emails = new StringBuilder();
                        for (String email : c.getEmail()) {
                            emails.append(email).append("\n");
                        }
                        txtEmails.setText(emails.toString().trim());
                        
                        txtTelefone.setText(c.getTelefone());
                        txtDataCadastro.setText(c.getData_cadastro().format(DATE_FORMATTER));
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar cliente: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void incluirCliente() {
        String cpf = txtCpf.getText().trim();
        String nome = txtNome.getText().trim();
        String emailsText = txtEmails.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String dataText = txtDataCadastro.getText().trim();
        
        // Validações
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o CPF do cliente!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome do cliente!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Valida CPF básico (apenas tamanho)
        if (cpf.length() != 11 && cpf.length() != 14) { // 11 dígitos ou 14 com pontos/traço
            int opcao = JOptionPane.showConfirmDialog(this,
                "CPF com formato incomum (" + cpf.length() + " caracteres).\n" +
                "Deseja continuar mesmo assim?",
                "Confirmar CPF",
                JOptionPane.YES_NO_OPTION);
            if (opcao != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        LocalDate dataCadastro;
        try {
            dataCadastro = LocalDate.parse(dataText, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use formato dd/MM/yyyy",
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Verifica se já existe cliente com este CPF
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            for (Cliente c : clientes) {
                if (c.getCpf().equals(cpf)) {
                    JOptionPane.showMessageDialog(this, "Já existe um cliente com este CPF!",
                                                 "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Processa emails
            String[] emails;
            if (emailsText.isEmpty()) {
                emails = new String[0];
            } else {
                emails = emailsText.split("\\r?\\n");
                for (int i = 0; i < emails.length; i++) {
                    emails[i] = emails[i].trim();
                }
            }
            
            // Cria novo cliente
            Cliente novoCliente = new Cliente(cpf, nome, emails, dataCadastro, telefone);
            clienteDAO.create(novoCliente);
            
            JOptionPane.showMessageDialog(this, 
                "Cliente incluído com sucesso!\n" +
                "CPF: " + cpf + "\n" +
                "Nome: " + nome,
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            limparCampos();
            carregarClientes();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao incluir cliente: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarCliente() {
        String cpf = txtCpf.getText().trim();
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para atualizar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            Cliente cliente = null;
            
            for (Cliente c : clientes) {
                if (c.getCpf().equals(cpf)) {
                    cliente = c;
                    break;
                }
            }
            
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Atualiza dados
            String nome = txtNome.getText().trim();
            if (!nome.isEmpty()) {
                cliente.setNome(nome);
            }
            
            String emailsText = txtEmails.getText().trim();
            if (!emailsText.isEmpty()) {
                String[] emails = emailsText.split("\\r?\\n");
                for (int i = 0; i < emails.length; i++) {
                    emails[i] = emails[i].trim();
                }
                cliente.setEmail(emails);
            }
            
            String telefone = txtTelefone.getText().trim();
            if (!telefone.isEmpty()) {
                cliente.setTelefone(telefone);
            }
            
            String dataText = txtDataCadastro.getText().trim();
            if (!dataText.isEmpty()) {
                try {
                    LocalDate dataCadastro = LocalDate.parse(dataText, DATE_FORMATTER);
                    cliente.setData_cadastro(dataCadastro);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Mantendo data anterior.",
                                                 "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            if (clienteDAO.update(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!",
                                             "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar o cliente.",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirCliente() {
        String cpf = txtCpf.getText().trim();
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir o cliente com CPF " + cpf + "?\n" +
            "Esta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ArrayList<Cliente> clientes = clienteDAO.readAll();
                for (Cliente c : clientes) {
                    if (c.getCpf().equals(cpf)) {
                        if (clienteDAO.delete(c.getId())) {
                            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!",
                                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            limparCampos();
                            carregarClientes();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarClientePorCpf(String cpfBusca) {
        if (cpfBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF para buscar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            Cliente encontrado = null;
            
            for (Cliente c : clientes) {
                if (c.getCpf().equals(cpfBusca)) {
                    encontrado = c;
                    break;
                }
            }
            
            if (encontrado != null) {
                // Seleciona na tabela
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(cpfBusca)) {
                        tabelaClientes.setRowSelectionInterval(i, i);
                        tabelaClientes.scrollRectToVisible(tabelaClientes.getCellRect(i, 0, true));
                        break;
                    }
                }
                
                // Mostra detalhes
                StringBuilder detalhes = new StringBuilder();
                detalhes.append("✅ Cliente Encontrado\n\n");
                detalhes.append("CPF: ").append(encontrado.getCpf()).append("\n");
                detalhes.append("Nome: ").append(encontrado.getNome()).append("\n");
                detalhes.append("Telefone: ").append(encontrado.getTelefone()).append("\n");
                detalhes.append("Data Cadastro: ").append(encontrado.getData_cadastro().format(DATE_FORMATTER)).append("\n");
                detalhes.append("Emails: ").append(String.join(", ", encontrado.getEmail())).append("\n");
                
                JOptionPane.showMessageDialog(this, detalhes.toString(),
                                             "Cliente Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Cliente com CPF " + cpfBusca + " não encontrado.",
                                             "Não Encontrado", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarClientePorNome() {
        String nome = JOptionPane.showInputDialog(this,
        "Digite parte do nome do CLIENTE para buscar:",
        "Buscar Cliente por Nome",
        JOptionPane.QUESTION_MESSAGE);
            
        if (nome != null && !nome.trim().isEmpty()) {
            nome = nome.trim().toLowerCase();
            
            try {
                ArrayList<Cliente> clientes = clienteDAO.readAll();
                ArrayList<Cliente> encontrados = new ArrayList<>();
                
                for (Cliente c : clientes) {
                    if (c.getNome().toLowerCase().contains(nome)) {
                        encontrados.add(c);
                    }
                }
                
                if (!encontrados.isEmpty()) {
                    // Filtra a tabela
                    tableModel.setRowCount(0);
                    for (Cliente c : encontrados) {
                        String emails = String.join(", ", c.getEmail());
                        String dataFormatada = c.getData_cadastro().format(DATE_FORMATTER);
                        
                        tableModel.addRow(new Object[]{
                            c.getCpf(),
                            c.getNome(),
                            emails,
                            c.getTelefone(),
                            dataFormatada
                        });
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        "Encontrados " + encontrados.size() + " clientes com '" + nome + "'",
                        "Resultado da Busca",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Nenhum cliente encontrado com '" + nome + "'",
                        "Não Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro na busca: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarEstatisticas() {
        try {
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            
            if (clientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há clientes cadastrados!",
                                             "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int totalClientes = clientes.size();
            int comTelefone = 0;
            int comEmail = 0;
            LocalDate dataMaisAntiga = LocalDate.now();
            LocalDate dataMaisRecente = LocalDate.now();
            
            for (Cliente c : clientes) {
                if (c.getTelefone() != null && !c.getTelefone().trim().isEmpty()) {
                    comTelefone++;
                }
                if (c.getEmail().length > 0) {
                    comEmail++;
                }
                if (c.getData_cadastro().isBefore(dataMaisAntiga)) {
                    dataMaisAntiga = c.getData_cadastro();
                }
                if (c.getData_cadastro().isAfter(dataMaisRecente)) {
                    dataMaisRecente = c.getData_cadastro();
                }
            }
            
            double percentTelefone = (double) comTelefone / totalClientes * 100;
            double percentEmail = (double) comEmail / totalClientes * 100;
            
            StringBuilder estatisticas = new StringBuilder();
            estatisticas.append("📊 ESTATÍSTICAS DE CLIENTES\n\n");
            estatisticas.append("Total de clientes: ").append(totalClientes).append("\n");
            estatisticas.append("Com telefone: ").append(comTelefone)
                       .append(" (").append(String.format("%.1f", percentTelefone)).append("%)\n");
            estatisticas.append("Com email: ").append(comEmail)
                       .append(" (").append(String.format("%.1f", percentEmail)).append("%)\n");
            estatisticas.append("Data mais antiga: ").append(dataMaisAntiga.format(DATE_FORMATTER)).append("\n");
            estatisticas.append("Data mais recente: ").append(dataMaisRecente.format(DATE_FORMATTER)).append("\n");
            
            JOptionPane.showMessageDialog(this, estatisticas.toString(),
                                         "Estatísticas", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao calcular estatísticas: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparCampos() {
        txtCpf.setText("");
        txtNome.setText("");
        txtEmails.setText("");
        txtTelefone.setText("");
        txtDataCadastro.setText(LocalDate.now().format(DATE_FORMATTER));
        tabelaClientes.clearSelection();
    }
}