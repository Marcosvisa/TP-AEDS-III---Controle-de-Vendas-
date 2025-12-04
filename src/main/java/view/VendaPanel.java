package view;

import util.ButtonStyler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.*;
import model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaPanel extends JPanel {
    private VendaDAO vendaDAO;
    private ClienteDAO clienteDAO;
    private VendedorDAO vendedorDAO;
    private CarroDAO carroDAO;
    private IndiceVendedorVendas indiceVendedorVendas;
    private IndiceClienteVendas indiceClienteVendas;
    private IndiceCarroVenda indiceCarroVenda;
    
    private JTable tabelaVendas;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboVendedor, comboCliente;
    private JList<String> listCarrosDisponiveis;
    private JList<String> listCarrosSelecionados;
    private JTextField txtValorTotal;
    private JButton btnAdicionarCarro, btnRemoverCarro, btnRegistrarVenda;
    private JButton btnDebugCarros; // Botão para debug
    
    public VendaPanel(VendaDAO vendaDAO, ClienteDAO clienteDAO, VendedorDAO vendedorDAO,
                     CarroDAO carroDAO, IndiceVendedorVendas indiceVendedorVendas,
                     IndiceClienteVendas indiceClienteVendas, IndiceCarroVenda indiceCarroVenda) {
        this.vendaDAO = vendaDAO;
        this.clienteDAO = clienteDAO;
        this.vendedorDAO = vendedorDAO;
        this.carroDAO = carroDAO;
        this.indiceVendedorVendas = indiceVendedorVendas;
        this.indiceClienteVendas = indiceClienteVendas;
        this.indiceCarroVenda = indiceCarroVenda;
        
        initComponents();
        carregarDadosCombos();
        carregarVendas();
        
        // Debug inicial
        debugCarros();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // painel superior - formulário de venda
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Nova Venda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // vendedor
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Vendedor:"), gbc);
        
        gbc.gridx = 1;
        comboVendedor = new JComboBox<>();
        comboVendedor.setPreferredSize(new Dimension(200, 25));
        painelFormulario.add(comboVendedor, gbc);
        
        // cliente
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        comboCliente = new JComboBox<>();
        comboCliente.setPreferredSize(new Dimension(200, 25));
        painelFormulario.add(comboCliente, gbc);
        
        // painel de seleção de carros
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblCarros = new JLabel("Seleção de Carros:");
        painelFormulario.add(lblCarros, gbc);
        
        gbc.gridy = 3; gbc.gridheight = 3;
        JPanel painelCarros = new JPanel(new GridLayout(1, 3, 10, 0));
        
        // lista de carros disponíveis
        DefaultListModel<String> modeloDisponiveis = new DefaultListModel<>();
        listCarrosDisponiveis = new JList<>(modeloDisponiveis);
        listCarrosDisponiveis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDisponiveis = new JScrollPane(listCarrosDisponiveis);
        scrollDisponiveis.setBorder(BorderFactory.createTitledBorder("Carros Disponíveis"));
        painelCarros.add(scrollDisponiveis);
        
        // botões de transferência
        JPanel painelBotoesTransfer = new JPanel(new GridLayout(2, 1, 0, 10));
        btnAdicionarCarro = ButtonStyler.createStyledButton("→", ButtonStyler.COLOR_PRIMARY);
        btnAdicionarCarro.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdicionarCarro.addActionListener(e -> adicionarCarroSelecionado());
        
        btnRemoverCarro = ButtonStyler.createStyledButton("←", ButtonStyler.COLOR_WARNING);
        btnRemoverCarro.setFont(new Font("Arial", Font.BOLD, 16));
        btnRemoverCarro.addActionListener(e -> removerCarroSelecionado());
        
        painelBotoesTransfer.add(btnAdicionarCarro);
        painelBotoesTransfer.add(btnRemoverCarro);
        painelCarros.add(painelBotoesTransfer);
        
        // lista de carros selecionados
        DefaultListModel<String> modeloSelecionados = new DefaultListModel<>();
        listCarrosSelecionados = new JList<>(modeloSelecionados);
        JScrollPane scrollSelecionados = new JScrollPane(listCarrosSelecionados);
        scrollSelecionados.setBorder(BorderFactory.createTitledBorder("Carros na Venda"));
        painelCarros.add(scrollSelecionados);
        
        painelFormulario.add(painelCarros, gbc);
        
        // valor total
        gbc.gridy = 6; gbc.gridheight = 1;
        painelFormulario.add(new JLabel("Valor Total:"), gbc);
        
        gbc.gridx = 1;
        txtValorTotal = new JTextField(15);
        txtValorTotal.setEditable(false);
        txtValorTotal.setText("R$ 0.00");
        painelFormulario.add(txtValorTotal, gbc);
        
        // botões de controle
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel painelBotoesControle = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnRegistrarVenda = ButtonStyler.createStyledButton("Registrar Venda", ButtonStyler.COLOR_SUCCESS);
        btnRegistrarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistrarVenda.addActionListener(e -> registrarVenda());
        
        btnDebugCarros = ButtonStyler.createStyledButton("Debug Carros", ButtonStyler.COLOR_SECONDARY);
        btnDebugCarros.addActionListener(e -> debugCarros());
        
        JButton btnRecarregarCarros = ButtonStyler.createStyledButton("Recarregar Carros", ButtonStyler.COLOR_PRIMARY);
        btnRecarregarCarros.addActionListener(e -> carregarDadosCombos());
        
        painelBotoesControle.add(btnRegistrarVenda);
        painelBotoesControle.add(btnDebugCarros);
        painelBotoesControle.add(btnRecarregarCarros);
        
        painelFormulario.add(painelBotoesControle, gbc);
        
        add(painelFormulario, BorderLayout.NORTH);
        
        // tabela de vendas
        String[] colunas = {"ID", "Vendedor", "Cliente", "Carros", "Data", "Valor Total"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaVendas = new JTable(tableModel);
        JScrollPane scrollTabela = new JScrollPane(tabelaVendas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Vendas Registradas"));
        
        add(scrollTabela, BorderLayout.CENTER);
    }
    
    private void carregarDadosCombos() {
        try {
            System.out.println("=== CARREGANDO DADOS PARA VENDA ===");
            
            comboVendedor.removeAllItems();
            comboCliente.removeAllItems();
            
            // carrega vendedores
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            System.out.println("Vendedores encontrados: " + vendedores.size());
            for (Vendedor v : vendedores) {
                String item = v.getCpf() + " - " + v.getNome();
                comboVendedor.addItem(item);
                System.out.println("Vendedor: " + item);
            }
            
            // carrega clientes
            ArrayList<Cliente> clientes = clienteDAO.readAll();
            System.out.println("Clientes encontrados: " + clientes.size());
            for (Cliente c : clientes) {
                String item = c.getCpf() + " - " + c.getNome();
                comboCliente.addItem(item);
                System.out.println("Cliente: " + item);
            }
            
            // carrega carros disponíveis
            DefaultListModel<String> modelo = new DefaultListModel<>();
            ArrayList<Carro> carros = carroDAO.readAll();
            System.out.println("Carros encontrados no DAO: " + carros.size());
            
            for (Carro c : carros) {
                String item = c.getId() + " - " + c.getModelo() + " (R$ " + c.getPreco() + ")";
                modelo.addElement(item);
                System.out.println("Carro adicionado: " + item);
            }
            
            listCarrosDisponiveis.setModel(modelo);
            
            // Verifica se há erros na lista
            if (modelo.isEmpty()) {
                System.out.println("ATENÇÃO: Lista de carros está VAZIA!");
                System.out.println("Verifique se:");
                System.out.println("1. Carros foram cadastrados no CarroPanel");
                System.out.println("2. O arquivo carros.db existe na pasta dados/");
                System.out.println("3. O DAO está funcionando corretamente");
            } else {
                System.out.println("Total de carros carregados na lista: " + modelo.size());
            }
            
        } catch (Exception e) {
            System.err.println("ERRO ao carregar dados: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void debugCarros() {
        try {
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("=== DEBUG DE CARROS ===\n\n");
            
            // Verifica DAO
            debugInfo.append("1. Verificando DAO...\n");
            if (carroDAO == null) {
                debugInfo.append("   ❌ CarroDAO é NULL!\n");
            } else {
                debugInfo.append("   ✅ CarroDAO OK\n");
            }
            
            // Tenta ler carros
            debugInfo.append("\n2. Tentando ler carros...\n");
            try {
                ArrayList<Carro> carros = carroDAO.readAll();
                debugInfo.append("   Total carros no DAO: ").append(carros.size()).append("\n");
                
                for (Carro c : carros) {
                    debugInfo.append("   - ID: ").append(c.getId())
                            .append(", Modelo: ").append(c.getModelo())
                            .append(", Preço: R$ ").append(c.getPreco())
                            .append("\n");
                }
                
                // Verifica lista JList
                debugInfo.append("\n3. Verificando JList...\n");
                DefaultListModel<String> modelo = (DefaultListModel<String>) listCarrosDisponiveis.getModel();
                debugInfo.append("   Itens na JList: ").append(modelo.size()).append("\n");
                
                for (int i = 0; i < modelo.size(); i++) {
                    debugInfo.append("   - ").append(modelo.getElementAt(i)).append("\n");
                }
                
            } catch (Exception e) {
                debugInfo.append("   ❌ ERRO ao ler carros: ").append(e.getMessage()).append("\n");
                e.printStackTrace();
            }
            
            // Mostra debug
            JTextArea textArea = new JTextArea(debugInfo.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane,
                                         "Debug de Carros",
                                         JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro no debug: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adicionarCarroSelecionado() {
        String selecionado = listCarrosDisponiveis.getSelectedValue();
        if (selecionado != null) {
            DefaultListModel<String> modeloDisponiveis = (DefaultListModel<String>) listCarrosDisponiveis.getModel();
            DefaultListModel<String> modeloSelecionados = (DefaultListModel<String>) listCarrosSelecionados.getModel();
            
            modeloDisponiveis.removeElement(selecionado);
            modeloSelecionados.addElement(selecionado);
            
            calcularValorTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um carro primeiro!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void removerCarroSelecionado() {
        String selecionado = listCarrosSelecionados.getSelectedValue();
        if (selecionado != null) {
            DefaultListModel<String> modeloDisponiveis = (DefaultListModel<String>) listCarrosDisponiveis.getModel();
            DefaultListModel<String> modeloSelecionados = (DefaultListModel<String>) listCarrosSelecionados.getModel();
            
            modeloSelecionados.removeElement(selecionado);
            modeloDisponiveis.addElement(selecionado);
            
            calcularValorTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um carro primeiro!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void calcularValorTotal() {
        try {
            float total = 0;
            DefaultListModel<String> modelo = (DefaultListModel<String>) listCarrosSelecionados.getModel();
            
            for (int i = 0; i < modelo.size(); i++) {
                String item = modelo.getElementAt(i);
                // extrai ID do carro
                String idStr = item.split(" - ")[0];
                int idCarro = Integer.parseInt(idStr);
                
                Carro carro = carroDAO.read(idCarro);
                if (carro != null) {
                    total += carro.getPreco();
                }
            }
            
            txtValorTotal.setText(String.format("R$ %.2f", total));
        } catch (Exception e) {
            txtValorTotal.setText("R$ 0.00");
            System.err.println("Erro ao calcular valor total: " + e.getMessage());
        }
    }
    
    private void registrarVenda() {
        if (comboVendedor.getSelectedItem() == null || comboCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor e um cliente!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultListModel<String> modelo = (DefaultListModel<String>) listCarrosSelecionados.getModel();
        if (modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um carro!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // extrai CPFs
            String vendedorItem = (String) comboVendedor.getSelectedItem();
            String clienteItem = (String) comboCliente.getSelectedItem();
            
            String cpfVendedor = vendedorItem.split(" - ")[0];
            String cpfCliente = clienteItem.split(" - ")[0];
            
            // extrai IDs dos carros
            List<Integer> idsCarros = new ArrayList<>();
            float valorTotal = 0;
            
            for (int i = 0; i < modelo.size(); i++) {
                String item = modelo.getElementAt(i);
                String idStr = item.split(" - ")[0];
                int idCarro = Integer.parseInt(idStr);
                idsCarros.add(idCarro);
                
                Carro carro = carroDAO.read(idCarro);
                if (carro != null) {
                    valorTotal += carro.getPreco();
                }
            }
            
            // converte para array
            int[] idsCarrosArray = idsCarros.stream().mapToInt(i -> i).toArray();
            
            // cria venda
            Venda novaVenda = new Venda(0, cpfVendedor, cpfCliente, idsCarrosArray, 
                                       LocalDate.now(), valorTotal);
            
            // salva no arquivo
            long offset = vendaDAO.createWithOffset(novaVenda);
            int idVenda = novaVenda.getId();
            
            // atualiza índices
            indiceVendedorVendas.addVenda(cpfVendedor, offset);
            indiceClienteVendas.addVenda(cpfCliente, offset);
            
            // atualiza índice carro-venda
            for (int idCarro : idsCarrosArray) {
                indiceCarroVenda.addVendaToCarro(idCarro, idVenda);
            }
            
            // atualiza vendedor
            ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
            for (Vendedor v : vendedores) {
                if (v.getCpf().equals(cpfVendedor)) {
                    v.setNumero_vendas(v.getNumero_vendas() + 1);
                    v.setFaturamento(v.getFaturamento() + valorTotal);
                    vendedorDAO.update(v);
                    break;
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                "Venda registrada com sucesso!\n" +
                "ID: " + idVenda + "\n" +
                "Valor Total: R$ " + valorTotal,
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpa seleção
            modelo.clear();
            txtValorTotal.setText("R$ 0.00");
            carregarDadosCombos();
            carregarVendas();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar venda: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void carregarVendas() {
        try {
            tableModel.setRowCount(0);
            ArrayList<Venda> vendas = vendaDAO.readAll();
            
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Venda v : vendas) {
                // Formata lista de carros
                StringBuilder carrosStr = new StringBuilder();
                for (int id : v.getIdsCarros()) {
                    carrosStr.append(id).append(", ");
                }
                if (carrosStr.length() > 0) {
                    carrosStr.setLength(carrosStr.length() - 2); 
                }
                
                tableModel.addRow(new Object[]{
                    v.getId(),
                    v.getCpfVendedor(),
                    v.getCpfCliente(),
                    carrosStr.toString(),
                    v.getData_venda().format(df),
                    String.format("R$ %.2f", v.getValor_total())
                });
            }
            
            System.out.println("Vendas carregadas: " + vendas.size());
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar vendas: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}