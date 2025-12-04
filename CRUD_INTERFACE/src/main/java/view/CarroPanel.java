package view;

import util.ButtonStyler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.CarroDAO;
import model.Carro;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import util.ButtonStyler;

public class CarroPanel extends JPanel {
    private CarroDAO carroDAO;
    private JTable tabelaCarros;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtModelo, txtPreco;
    private JTextArea txtCores;
    private JTextField txtDataFabricacao;
    private JButton btnIncluir, btnAtualizar, btnExcluir, btnLimpar, btnBuscar;
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public CarroPanel(CarroDAO carroDAO) {
        this.carroDAO = carroDAO;
        initComponents();
        carregarCarros();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de formulário (esquerda)
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Cadastro de Carro"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID (somente para busca)
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("ID (busca):"), gbc);
        
        gbc.gridx = 1;
        txtId = new JTextField(10);
        txtId.setEditable(false); // Não editável, só para exibição
        painelFormulario.add(txtId, gbc);
        
        // Modelo
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Modelo:"), gbc);
        
        gbc.gridx = 1;
        txtModelo = new JTextField(20);
        painelFormulario.add(txtModelo, gbc);
        
        // Cores
        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Cores (uma por linha):"), gbc);
        
        gbc.gridx = 1;
        txtCores = new JTextArea(4, 20);
        txtCores.setLineWrap(true);
        JScrollPane scrollCores = new JScrollPane(txtCores);
        painelFormulario.add(scrollCores, gbc);
        
        // Data de Fabricação
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Data Fabricação:"), gbc);
        
        gbc.gridx = 1;
        txtDataFabricacao = new JTextField(10);
        txtDataFabricacao.setText(LocalDate.now().format(DATE_FORMATTER));
        painelFormulario.add(txtDataFabricacao, gbc);
        
        JButton btnHoje = new JButton("Hoje");
        btnHoje.addActionListener(e -> txtDataFabricacao.setText(LocalDate.now().format(DATE_FORMATTER)));
        painelFormulario.add(btnHoje, gbc);
        
        // Preço
gbc.gridx = 0; gbc.gridy = 4;
painelFormulario.add(new JLabel("Preço (R$):"), gbc);

gbc.gridx = 1;
txtPreco = new JTextField(10);
painelFormulario.add(txtPreco, gbc);

        
        // Painel de botões do formulário
        
// No método initComponents():

JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

btnIncluir = ButtonStyler.createStyledButton("Incluir", ButtonStyler.COLOR_SUCCESS);
btnIncluir.addActionListener(e -> incluirCarro());  // ← DEVE SER incluirCarro(), NÃO incluirVendedor()

btnAtualizar = ButtonStyler.createStyledButton("Atualizar", ButtonStyler.COLOR_PRIMARY);
btnAtualizar.addActionListener(e -> atualizarCarro());  // ← DEVE SER atualizarCarro()

btnExcluir = ButtonStyler.createStyledButton("Excluir", ButtonStyler.COLOR_DANGER);
btnExcluir.addActionListener(e -> excluirCarro());  // ← DEVE SER excluirCarro()

btnLimpar = ButtonStyler.createStyledButton("Limpar", ButtonStyler.COLOR_SECONDARY);
btnLimpar.addActionListener(e -> limparCampos());

painelBotoesForm.add(btnIncluir);
painelBotoesForm.add(btnAtualizar);
painelBotoesForm.add(btnExcluir);
painelBotoesForm.add(btnLimpar);

gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
painelFormulario.add(painelBotoesForm, gbc);

        
        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Carro"));
        
        JTextField txtBuscaId = new JTextField(10);
        btnBuscar = new JButton("Buscar por ID");
        btnBuscar.addActionListener(e -> buscarCarroPorId(txtBuscaId.getText()));
        
        JButton btnBuscarModelo = new JButton("Buscar por Modelo");
        btnBuscarModelo.addActionListener(e -> buscarCarroPorModelo());
        
        ButtonStyler.estiloSecundario(btnBuscar);
ButtonStyler.estiloSecundario(btnBuscarModelo);

painelBusca.add(new JLabel("ID:"));
painelBusca.add(txtBuscaId);
painelBusca.add(btnBuscar);
painelBusca.add(btnBuscarModelo);
        
        // Painel esquerdo combinado
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.add(painelFormulario, BorderLayout.NORTH);
        painelEsquerdo.add(painelBusca, BorderLayout.SOUTH);
        
        // Tabela de carros (direita)
        String[] colunas = {"ID", "Modelo", "Cores", "Data Fabricação", "Preço (R$)"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaCarros = new JTable(tableModel);
        tabelaCarros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCarros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarCarroNaTabela();
            }
        });
        
        // Ajusta largura das colunas
        tabelaCarros.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaCarros.getColumnModel().getColumn(1).setPreferredWidth(150); // Modelo
        tabelaCarros.getColumnModel().getColumn(2).setPreferredWidth(150); // Cores
        tabelaCarros.getColumnModel().getColumn(3).setPreferredWidth(100); // Data
        tabelaCarros.getColumnModel().getColumn(4).setPreferredWidth(100); // Preço
        
        JScrollPane scrollTabela = new JScrollPane(tabelaCarros);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Carros Cadastrados"));
        
        // Botões da tabela
        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRecarregar = new JButton("Recarregar Tabela");
        btnRecarregar.addActionListener(e -> carregarCarros());
        
        JButton btnExportar = new JButton("Exportar para CSV");
        btnExportar.addActionListener(e -> exportarParaCSV());
        
        painelBotoesTabela.add(btnRecarregar);
        painelBotoesTabela.add(btnExportar);
        
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
    
    private void carregarCarros() {
        try {
            tableModel.setRowCount(0);
            ArrayList<Carro> carros = carroDAO.readAll();
            
            for (Carro c : carros) {
                String cores = String.join(", ", c.getCores());
                String dataFormatada = c.getData_fabricacao().format(DATE_FORMATTER);
                String precoFormatado = String.format("R$ %.2f", c.getPreco());
                
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getModelo(),
                    cores,
                    dataFormatada,
                    precoFormatado
                });
            }
            
            JOptionPane.showMessageDialog(this, "Carregados " + carros.size() + " carros", 
                                         "Informação", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar carros: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selecionarCarroNaTabela() {
        int linhaSelecionada = tabelaCarros.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int id = (int) tableModel.getValueAt(linhaSelecionada, 0);
            
            try {
                Carro carro = carroDAO.read(id);
                if (carro != null) {
                    txtId.setText(String.valueOf(carro.getId()));
                    txtModelo.setText(carro.getModelo());
                    
                    StringBuilder cores = new StringBuilder();
                    for (String cor : carro.getCores()) {
                        cores.append(cor).append("\n");
                    }
                    txtCores.setText(cores.toString().trim());
                    
                    txtDataFabricacao.setText(carro.getData_fabricacao().format(DATE_FORMATTER));
                    txtPreco.setText(String.format("%.2f", carro.getPreco()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar carro: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void incluirCarro() {
        String modelo = txtModelo.getText().trim();
        String coresText = txtCores.getText().trim();
        String dataText = txtDataFabricacao.getText().trim();
        String precoText = txtPreco.getText().trim();
        
        // Validações
        if (modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o modelo do carro!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (coresText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite pelo menos uma cor!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate dataFabricacao;
        try {
            dataFabricacao = LocalDate.parse(dataText, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use formato dd/MM/yyyy",
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        float preco;
        try {
            preco = Float.parseFloat(precoText.replace(",", "."));
            if (preco <= 0) {
                throw new NumberFormatException("Preço deve ser positivo");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use números (ex: 45000.50)",
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Processa cores
            String[] cores = coresText.split("\\r?\\n");
            for (int i = 0; i < cores.length; i++) {
                cores[i] = cores[i].trim();
            }
            
            // Cria novo carro
            Carro novoCarro = new Carro(0, modelo, cores, dataFabricacao, preco);
            int novoId = carroDAO.create(novoCarro);
            
            JOptionPane.showMessageDialog(this, 
                "Carro incluído com sucesso!\n" +
                "ID: " + novoId + "\n" +
                "Modelo: " + modelo + "\n" +
                "Preço: R$ " + String.format("%.2f", preco),
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            limparCampos();
            carregarCarros();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao incluir carro: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarCarro() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um carro para atualizar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idText);
            Carro carro = carroDAO.read(id);
            
            if (carro == null) {
                JOptionPane.showMessageDialog(this, "Carro não encontrado!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Atualiza dados
            String modelo = txtModelo.getText().trim();
            if (!modelo.isEmpty()) {
                carro.setModelo(modelo);
            }
            
            String coresText = txtCores.getText().trim();
            if (!coresText.isEmpty()) {
                String[] cores = coresText.split("\\r?\\n");
                for (int i = 0; i < cores.length; i++) {
                    cores[i] = cores[i].trim();
                }
                carro.setCores(cores);
            }
            
            String dataText = txtDataFabricacao.getText().trim();
            if (!dataText.isEmpty()) {
                try {
                    LocalDate dataFabricacao = LocalDate.parse(dataText, DATE_FORMATTER);
                    carro.setData_fabricacao(dataFabricacao);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Mantendo data anterior.",
                                                 "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            String precoText = txtPreco.getText().trim();
            if (!precoText.isEmpty()) {
                try {
                    float preco = Float.parseFloat(precoText.replace(",", "."));
                    if (preco > 0) {
                        carro.setPreco(preco);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Preço inválido! Mantendo preço anterior.",
                                                 "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            if (carroDAO.update(carro)) {
                JOptionPane.showMessageDialog(this, "Carro atualizado com sucesso!",
                                             "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarCarros();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar o carro.",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido!",
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar carro: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirCarro() {
        String idText = txtId.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um carro para excluir!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir o carro ID " + idText + "?\n" +
            "Esta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idText);
                if (carroDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Carro excluído com sucesso!",
                                                 "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCampos();
                    carregarCarros();
                } else {
                    JOptionPane.showMessageDialog(this, "Carro não encontrado!",
                                                 "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir carro: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarCarroPorId(String idText) {
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um ID para buscar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idText);
            Carro carro = carroDAO.read(id);
            
            if (carro != null) {
                // Seleciona na tabela
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(id)) {
                        tabelaCarros.setRowSelectionInterval(i, i);
                        tabelaCarros.scrollRectToVisible(tabelaCarros.getCellRect(i, 0, true));
                        break;
                    }
                }
                
                // Mostra detalhes
                StringBuilder detalhes = new StringBuilder();
                detalhes.append("✅ Carro Encontrado\n\n");
                detalhes.append("ID: ").append(carro.getId()).append("\n");
                detalhes.append("Modelo: ").append(carro.getModelo()).append("\n");
                detalhes.append("Cores: ").append(String.join(", ", carro.getCores())).append("\n");
                detalhes.append("Data Fabricação: ").append(carro.getData_fabricacao().format(DATE_FORMATTER)).append("\n");
                detalhes.append("Preço: R$ ").append(String.format("%.2f", carro.getPreco())).append("\n");
                
                JOptionPane.showMessageDialog(this, detalhes.toString(),
                                             "Carro Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Carro com ID " + id + " não encontrado.",
                                             "Não Encontrado", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido! Digite um número.",
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarCarroPorModelo() {
        String modelo = JOptionPane.showInputDialog(this,
            "Digite parte do modelo para buscar:",
            "Buscar por Modelo",
            JOptionPane.QUESTION_MESSAGE);
            
        if (modelo != null && !modelo.trim().isEmpty()) {
            modelo = modelo.trim().toLowerCase();
            
            try {
                ArrayList<Carro> carros = carroDAO.readAll();
                ArrayList<Carro> encontrados = new ArrayList<>();
                
                for (Carro c : carros) {
                    if (c.getModelo().toLowerCase().contains(modelo)) {
                        encontrados.add(c);
                    }
                }
                
                if (!encontrados.isEmpty()) {
                    // Filtra a tabela
                    tableModel.setRowCount(0);
                    for (Carro c : encontrados) {
                        String cores = String.join(", ", c.getCores());
                        String dataFormatada = c.getData_fabricacao().format(DATE_FORMATTER);
                        String precoFormatado = String.format("R$ %.2f", c.getPreco());
                        
                        tableModel.addRow(new Object[]{
                            c.getId(),
                            c.getModelo(),
                            cores,
                            dataFormatada,
                            precoFormatado
                        });
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        "Encontrados " + encontrados.size() + " carros com '" + modelo + "'",
                        "Resultado da Busca",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Nenhum carro encontrado com '" + modelo + "'",
                        "Não Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro na busca: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarParaCSV() {
        try {
            ArrayList<Carro> carros = carroDAO.readAll();
            if (carros.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há carros para exportar!",
                                             "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            StringBuilder csv = new StringBuilder();
            csv.append("ID;Modelo;Cores;Data Fabricação;Preço\n");
            
            for (Carro c : carros) {
                String cores = String.join(", ", c.getCores());
                String dataFormatada = c.getData_fabricacao().format(DATE_FORMATTER);
                String precoFormatado = String.format("%.2f", c.getPreco());
                
                csv.append(c.getId()).append(";")
                   .append(c.getModelo()).append(";")
                   .append("\"").append(cores).append("\";")
                   .append(dataFormatada).append(";")
                   .append(precoFormatado).append("\n");
            }
            
            // Mostra em uma área de texto para cópia
            JTextArea textArea = new JTextArea(csv.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane,
                                         "Exportação CSV - Copie o conteúdo",
                                         JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparCampos() {
        txtId.setText("");
        txtModelo.setText("");
        txtCores.setText("");
        txtDataFabricacao.setText(LocalDate.now().format(DATE_FORMATTER));
        txtPreco.setText("");
        tabelaCarros.clearSelection();
    }
}