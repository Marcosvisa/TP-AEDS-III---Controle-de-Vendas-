package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.*;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class RelacaoPanel extends JPanel {
    private CarroVendaDAO carroVendaDAO;
    private CarroDAO carroDAO;
    private VendaDAO vendaDAO;
    private IndiceCarroVenda indiceCarroVenda;
    
    private JTable tabelaRelacoes;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboCarro, comboVenda;
    private JButton btnAdicionar, btnRemover, btnConsultarCarro, btnConsultarVenda;
    
    public RelacaoPanel(CarroVendaDAO carroVendaDAO, CarroDAO carroDAO, 
                       VendaDAO vendaDAO, IndiceCarroVenda indiceCarroVenda) {
        this.carroVendaDAO = carroVendaDAO;
        this.carroDAO = carroDAO;
        this.vendaDAO = vendaDAO;
        this.indiceCarroVenda = indiceCarroVenda;
        
        initComponents();
        carregarCombos();
        carregarRelacoes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior para adicionar relações
        JPanel painelAdicionar = new JPanel(new GridBagLayout());
        painelAdicionar.setBorder(BorderFactory.createTitledBorder("Adicionar Relação Carro-Venda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        painelAdicionar.add(new JLabel("Carro:"), gbc);
        
        gbc.gridx = 1;
        comboCarro = new JComboBox<>();
        comboCarro.setPreferredSize(new Dimension(200, 25));
        painelAdicionar.add(comboCarro, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        painelAdicionar.add(new JLabel("Venda:"), gbc);
        
        gbc.gridx = 1;
        comboVenda = new JComboBox<>();
        comboVenda.setPreferredSize(new Dimension(200, 25));
        painelAdicionar.add(comboVenda, gbc);
        
        // Botões
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel painelBotoesAdicionar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnAdicionar = new JButton("Adicionar Relação");
        btnAdicionar.setBackground(new Color(46, 204, 113));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> adicionarRelacao());
        
        btnRemover = new JButton("Remover Selecionada");
        btnRemover.setBackground(new Color(231, 76, 60));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.addActionListener(e -> removerRelacao());
        
        painelBotoesAdicionar.add(btnAdicionar);
        painelBotoesAdicionar.add(btnRemover);
        painelAdicionar.add(painelBotoesAdicionar, gbc);
        
        add(painelAdicionar, BorderLayout.NORTH);
        
        // Painel central com tabela
        String[] colunas = {"ID Carro", "Modelo do Carro", "ID Venda", "Valor Venda", "Data Venda"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaRelacoes = new JTable(tableModel);
        JScrollPane scrollTabela = new JScrollPane(tabelaRelacoes);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Relações Carro-Venda"));
        
        add(scrollTabela, BorderLayout.CENTER);
        
        // Painel inferior para consultas
        JPanel painelConsultas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelConsultas.setBorder(BorderFactory.createTitledBorder("Consultas Específicas"));
        
        btnConsultarCarro = new JButton("Consultar Vendas de um Carro");
        btnConsultarCarro.addActionListener(e -> consultarVendasPorCarro());
        
        btnConsultarVenda = new JButton("Consultar Carros de uma Venda");
        btnConsultarVenda.addActionListener(e -> consultarCarrosPorVenda());
        
        JButton btnRecarregar = new JButton("Recarregar Tudo");
        btnRecarregar.addActionListener(e -> {
            carregarCombos();
            carregarRelacoes();
        });
        
        painelConsultas.add(btnConsultarCarro);
        painelConsultas.add(btnConsultarVenda);
        painelConsultas.add(btnRecarregar);
        
        add(painelConsultas, BorderLayout.SOUTH);
    }
    
    private void carregarCombos() {
        try {
            comboCarro.removeAllItems();
            comboVenda.removeAllItems();
            
            // Carrega carros
            ArrayList<Carro> carros = carroDAO.readAll();
            for (Carro c : carros) {
                comboCarro.addItem(c.getId() + " - " + c.getModelo());
            }
            
            // Carrega vendas
            ArrayList<Venda> vendas = vendaDAO.readAll();
            for (Venda v : vendas) {
                comboVenda.addItem(v.getId() + " - R$ " + v.getValor_total());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void carregarRelacoes() {
        try {
            tableModel.setRowCount(0);
            ArrayList<CarroVenda> relacoes = carroVendaDAO.readAll();
            
            for (CarroVenda cv : relacoes) {
                Carro carro = carroDAO.read(cv.getIdCarro());
                Venda venda = vendaDAO.read(cv.getIdVenda());
                
                String modeloCarro = (carro != null) ? carro.getModelo() : "N/A";
                String valorVenda = (venda != null) ? String.format("R$ %.2f", venda.getValor_total()) : "N/A";
                String dataVenda = (venda != null) ? venda.getData_venda().toString() : "N/A";
                
                tableModel.addRow(new Object[]{
                    cv.getIdCarro(),
                    modeloCarro,
                    cv.getIdVenda(),
                    valorVenda,
                    dataVenda
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar relações: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adicionarRelacao() {
        String itemCarro = (String) comboCarro.getSelectedItem();
        String itemVenda = (String) comboVenda.getSelectedItem();
        
        if (itemCarro == null || itemVenda == null) {
            JOptionPane.showMessageDialog(this, "Selecione um carro e uma venda!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int idCarro = Integer.parseInt(itemCarro.split(" - ")[0]);
            int idVenda = Integer.parseInt(itemVenda.split(" - ")[0]);
            
            // Verifica se já existe
            CarroVenda existente = carroVendaDAO.read(idCarro, idVenda);
            if (existente != null) {
                JOptionPane.showMessageDialog(this, "Esta relação já existe!",
                                             "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cria a relação
            carroVendaDAO.create(idCarro, idVenda);
            
            // Atualiza índice B+ Tree
            indiceCarroVenda.addVendaToCarro(idCarro, idVenda);
            
            JOptionPane.showMessageDialog(this, "Relação adicionada com sucesso!",
                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            carregarRelacoes();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar relação: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removerRelacao() {
        int linhaSelecionada = tabelaRelacoes.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma relação para remover!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCarro = (int) tableModel.getValueAt(linhaSelecionada, 0);
        int idVenda = (int) tableModel.getValueAt(linhaSelecionada, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remover relação entre Carro ID " + idCarro + " e Venda ID " + idVenda + "?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (carroVendaDAO.delete(idCarro, idVenda)) {
                    // Atualiza índice B+ Tree
                    List<Integer> vendas = indiceCarroVenda.getVendasPorCarro(idCarro);
                    vendas.remove((Integer) idVenda);
                    
                    JOptionPane.showMessageDialog(this, "Relação removida com sucesso!",
                                                 "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarRelacoes();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover relação: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void consultarVendasPorCarro() {
        String input = JOptionPane.showInputDialog(this,
            "Digite o ID do carro:",
            "Consultar Vendas do Carro",
            JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int idCarro = Integer.parseInt(input.trim());
                List<Integer> vendas = indiceCarroVenda.getVendasPorCarro(idCarro);
                
                if (vendas.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Nenhuma venda encontrada para o carro ID " + idCarro,
                        "Resultado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Vendas do Carro ID ").append(idCarro).append(":\n\n");
                    
                    Carro carro = carroDAO.read(idCarro);
                    if (carro != null) {
                        sb.append("Modelo: ").append(carro.getModelo()).append("\n\n");
                    }
                    
                    for (int idVenda : vendas) {
                        Venda venda = vendaDAO.read(idVenda);
                        if (venda != null) {
                            sb.append("• Venda ID: ").append(idVenda)
                              .append(" | R$ ").append(venda.getValor_total())
                              .append(" | ").append(venda.getData_venda())
                              .append("\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        sb.toString(),
                        "Vendas do Carro",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro na consulta: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void consultarCarrosPorVenda() {
        String input = JOptionPane.showInputDialog(this,
            "Digite o ID da venda:",
            "Consultar Carros da Venda",
            JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int idVenda = Integer.parseInt(input.trim());
                List<Integer> carros = indiceCarroVenda.getCarrosPorVenda(idVenda);
                
                if (carros.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Nenhum carro encontrado para a venda ID " + idVenda,
                        "Resultado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Carros da Venda ID ").append(idVenda).append(":\n\n");
                    
                    Venda venda = vendaDAO.read(idVenda);
                    if (venda != null) {
                        sb.append("Valor Total: R$ ").append(venda.getValor_total()).append("\n");
                        sb.append("Data: ").append(venda.getData_venda()).append("\n\n");
                    }
                    
                    for (int idCarro : carros) {
                        Carro carro = carroDAO.read(idCarro);
                        if (carro != null) {
                            sb.append("• Carro ID: ").append(idCarro)
                              .append(" | ").append(carro.getModelo())
                              .append(" | R$ ").append(carro.getPreco())
                              .append("\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        sb.toString(),
                        "Carros da Venda",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido!",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro na consulta: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}