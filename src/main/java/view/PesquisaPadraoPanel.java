package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import dao.CarroDAO;
import model.Carro;
import util.KMP;
import util.BoyerMoore;
import java.util.ArrayList;
import java.util.List;

public class PesquisaPadraoPanel extends JPanel {
    private CarroDAO carroDAO;
    private JRadioButton radioKMP, radioBM, radioAmbos;
    private JTextField txtPadrao;
    private JTextArea txtResultados;
    private JTable tabelaResultados;
    private DefaultTableModel tableModel;
    private JButton btnPesquisar, btnLimpar, btnDetalhes;
    private JLabel lblEstatisticas;
    private JLabel lblContadorCarros;
    
    public PesquisaPadraoPanel(CarroDAO carroDAO) {
        this.carroDAO = carroDAO;
        initComponents();
        carregarContadorCarros();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior de configuração
        JPanel painelSuperior = new JPanel(new GridBagLayout());
        painelSuperior.setBorder(BorderFactory.createTitledBorder("🔍 Configuração da Pesquisa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("PESQUISA POR PADRÃO EM MODELOS DE CARRO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(155, 89, 182));
        painelSuperior.add(lblTitulo, gbc);
        
        // Algoritmo
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel lblAlgoritmo = new JLabel("Algoritmo de Busca:");
        lblAlgoritmo.setFont(new Font("Arial", Font.BOLD, 12));
        painelSuperior.add(lblAlgoritmo, gbc);
        
        gbc.gridx = 1;
        ButtonGroup grupoAlgoritmos = new ButtonGroup();
        radioKMP = new JRadioButton("KMP (Knuth-Morris-Pratt)", true);
        radioBM = new JRadioButton("Boyer-Moore");
        radioAmbos = new JRadioButton("Comparar Ambos");
        
        grupoAlgoritmos.add(radioKMP);
        grupoAlgoritmos.add(radioBM);
        grupoAlgoritmos.add(radioAmbos);
        
        JPanel painelAlgoritmos = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        painelAlgoritmos.add(radioKMP);
        painelAlgoritmos.add(radioBM);
        painelAlgoritmos.add(radioAmbos);
        painelSuperior.add(painelAlgoritmos, gbc);
        
        // Descrição dos algoritmos
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JTextArea txtDescAlgoritmos = new JTextArea(
            "• KMP: Usa tabela de falhas, bom para padrões pequenos\n" +
            "• Boyer-Moore: Busca da direita para esquerda, mais rápido em textos grandes\n" +
            "• Comparar Ambos: Executa os dois e mostra diferenças de desempenho"
        );
        txtDescAlgoritmos.setEditable(false);
        txtDescAlgoritmos.setBackground(painelSuperior.getBackground());
        txtDescAlgoritmos.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDescAlgoritmos.setForeground(Color.DARK_GRAY);
        painelSuperior.add(txtDescAlgoritmos, gbc);
        
        // Padrão de busca
        gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel lblPadrao = new JLabel("Padrão a buscar:");
        lblPadrao.setFont(new Font("Arial", Font.BOLD, 12));
        painelSuperior.add(lblPadrao, gbc);
        
        gbc.gridx = 1;
        txtPadrao = new JTextField(30);
        txtPadrao.setText("Gol");
        txtPadrao.setFont(new Font("Arial", Font.PLAIN, 14));
        painelSuperior.add(txtPadrao, gbc);
        
        // Botões de ação
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnPesquisar = criarBotaoEstilizado("🔎 Pesquisar", new Color(46, 204, 113));
        btnPesquisar.addActionListener(e -> executarPesquisa());
        
        btnLimpar = criarBotaoEstilizado("🗑️ Limpar", new Color(149, 165, 166));
        btnLimpar.addActionListener(e -> limparResultados());
        
        btnDetalhes = criarBotaoEstilizado("📊 Estatísticas", new Color(52, 152, 219));
        btnDetalhes.addActionListener(e -> mostrarEstatisticas());
        
        painelBotoes.add(btnPesquisar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnDetalhes);
        
        painelSuperior.add(painelBotoes, gbc);
        
        // Label de estatísticas
        gbc.gridy = 5;
        lblEstatisticas = new JLabel("Pronto para pesquisar. Digite um padrão acima.");
        lblEstatisticas.setFont(new Font("Arial", Font.ITALIC, 11));
        lblEstatisticas.setForeground(Color.GRAY);
        painelSuperior.add(lblEstatisticas, gbc);
        
        add(painelSuperior, BorderLayout.NORTH);
        
        // Painel de resultados com abas
        JTabbedPane tabbedResultados = new JTabbedPane();
        tabbedResultados.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Aba 1: Tabela de resultados
        String[] colunas = {"ID", "Modelo", "Posição", "Ocorrências", "Preço (R$)"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 2 || columnIndex == 3) {
                    return Integer.class;
                } else if (columnIndex == 4) {
                    return String.class;
                }
                return String.class;
            }
        };
        
        tabelaResultados = new JTable(tableModel);
        tabelaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaResultados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhesCarro();
            }
        });
        
        // Ajusta largura das colunas
        tabelaResultados.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaResultados.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaResultados.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaResultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaResultados.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollTabela = new JScrollPane(tabelaResultados);
        scrollTabela.setBorder(BorderFactory.createEmptyBorder());
        tabbedResultados.addTab("📋 Resultados", scrollTabela);
        
        // Aba 2: Log detalhado
        txtResultados = new JTextArea(20, 60);
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResultados.setBackground(new Color(245, 245, 245));
        
        txtResultados.append("=== PESQUISA POR PADRÃO ===\n");
        txtResultados.append("Digite um padrão e clique em Pesquisar.\n");
        txtResultados.append("Exemplos: 'Gol', 'Civic', 'Fusca', 'Palio'\n\n");
        
        JScrollPane scrollLog = new JScrollPane(txtResultados);
        scrollLog.setBorder(BorderFactory.createEmptyBorder());
        tabbedResultados.addTab("📝 Log Detalhado", scrollLog);
        
        add(tabbedResultados, BorderLayout.CENTER);
        
        // Painel inferior com informações
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.setBackground(new Color(240, 240, 240));
        painelInferior.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblInfo = new JLabel("💡 Dica: A pesquisa não diferencia maiúsculas/minúsculas");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        painelInferior.add(lblInfo, BorderLayout.WEST);
        
        lblContadorCarros = new JLabel("Carregando...");
        lblContadorCarros.setFont(new Font("Arial", Font.BOLD, 11));
        lblContadorCarros.setForeground(new Color(155, 89, 182));
        painelInferior.add(lblContadorCarros, BorderLayout.EAST);
        
        add(painelInferior, BorderLayout.SOUTH);
    }
    
    private JButton criarBotaoEstilizado(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(150, 35));
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(cor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(cor);
            }
        });
        
        return botao;
    }
    
    private void carregarContadorCarros() {
        new Thread(() -> {
            try {
                ArrayList<Carro> carros = carroDAO.readAll();
                SwingUtilities.invokeLater(() -> {
                    lblContadorCarros.setText("Carros cadastrados: " + carros.size());
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblContadorCarros.setText("Erro ao carregar contador");
                });
            }
        }).start();
    }
    
    private void executarPesquisa() {
        final String padrao = txtPadrao.getText().trim();
        if (padrao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um padrão para pesquisar!",
                                         "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (padrao.length() < 2) {
            int opcao = JOptionPane.showConfirmDialog(this,
                "Padrão muito curto (" + padrao.length() + " caracteres).\n" +
                "A pesquisa pode retornar muitos resultados.\n" +
                "Deseja continuar mesmo assim?",
                "Padrão Curto",
                JOptionPane.YES_NO_OPTION);
            if (opcao != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                btnPesquisar.setEnabled(false);
                btnDetalhes.setEnabled(false);
                lblEstatisticas.setText("Pesquisando...");
            });
            
            try {
                ArrayList<Carro> todosCarros = carroDAO.readAll();
                
                if (todosCarros.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(PesquisaPadraoPanel.this,
                            "Não há carros cadastrados no sistema!",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    });
                    return;
                }
                
                if (radioKMP.isSelected()) {
                    executarKMP(padrao, todosCarros);
                } else if (radioBM.isSelected()) {
                    executarBoyerMoore(padrao, todosCarros);
                } else if (radioAmbos.isSelected()) {
                    compararAlgoritmos(padrao, todosCarros);
                }
                
            } catch (Exception e) {
                final String erroMsg = e.getMessage();
                SwingUtilities.invokeLater(() -> {
                    txtResultados.append("❌ Erro na pesquisa: " + erroMsg + "\n");
                    JOptionPane.showMessageDialog(PesquisaPadraoPanel.this,
                        "Erro na pesquisa:\n" + erroMsg,
                        "Erro", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnPesquisar.setEnabled(true);
                    btnDetalhes.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void executarKMP(String padrao, ArrayList<Carro> carros) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            txtResultados.setText("");
            txtResultados.append("🔍 PESQUISA COM ALGORITMO KMP\n");
            txtResultados.append("=".repeat(60) + "\n");
            txtResultados.append("Padrão: '" + padrao + "'\n");
            txtResultados.append("Total de carros: " + carros.size() + "\n");
            txtResultados.append("Iniciando busca...\n\n");
        });
        
        KMP kmp = new KMP();
        final int totalCarros = carros.size();
        final int[] contadores = new int[2]; // [encontrados, totalOcorrencias]
        
        long inicio = System.nanoTime();
        
        for (Carro carro : carros) {
            String modelo = carro.getModelo().toLowerCase();
            String padraoLower = padrao.toLowerCase();
            
            int posicao = kmp.search(modelo, padraoLower);
            
            if (posicao != -1) {
                contadores[0]++;
                int ocorrencias = kmp.countOccurrences(modelo, padraoLower);
                contadores[1] += ocorrencias;
                
                final Carro carroFinal = carro;
                final int posFinal = posicao;
                final int ocorrFinal = ocorrencias;
                
                SwingUtilities.invokeLater(() -> {
                    tableModel.addRow(new Object[]{
                        carroFinal.getId(),
                        carroFinal.getModelo(),
                        posFinal,
                        ocorrFinal,
                        String.format("R$ %.2f", carroFinal.getPreco())
                    });
                    
                    txtResultados.append("✓ ID: " + carroFinal.getId() + " | Modelo: " + carroFinal.getModelo() + "\n");
                    txtResultados.append("  Posição: " + posFinal + " | Ocorrências: " + ocorrFinal + "\n");
                    txtResultados.append("  Preço: R$ " + String.format("%.2f", carroFinal.getPreco()) + "\n\n");
                });
            }
        }
        
        long fim = System.nanoTime();
        final long tempo = fim - inicio;
        
        SwingUtilities.invokeLater(() -> {
            txtResultados.append("=".repeat(60) + "\n");
            txtResultados.append("📊 RESUMO:\n");
            txtResultados.append("Carros encontrados: " + contadores[0] + "/" + totalCarros + "\n");
            txtResultados.append("Total de ocorrências: " + contadores[1] + "\n");
            txtResultados.append("Tempo de execução: " + String.format("%.3f", tempo/1000000.0) + " ms\n");
            txtResultados.append("Algoritmo: KMP (Knuth-Morris-Pratt)\n");
            
            lblEstatisticas.setText(
                "KMP: " + contadores[0] + " carros encontrados em " + 
                String.format("%.3f", tempo/1000000.0) + " ms"
            );
        });
    }
    
    private void executarBoyerMoore(String padrao, ArrayList<Carro> carros) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            txtResultados.setText("");
            txtResultados.append("🔍 PESQUISA COM ALGORITMO BOYER-MOORE\n");
            txtResultados.append("=".repeat(60) + "\n");
            txtResultados.append("Padrão: '" + padrao + "'\n");
            txtResultados.append("Total de carros: " + carros.size() + "\n");
            txtResultados.append("Iniciando busca...\n\n");
        });
        
        BoyerMoore bm = new BoyerMoore();
        final int totalCarros = carros.size();
        final int[] contadores = new int[2];
        
        long inicio = System.nanoTime();
        
        for (Carro carro : carros) {
            String modelo = carro.getModelo().toLowerCase();
            String padraoLower = padrao.toLowerCase();
            
            int posicao = bm.search(modelo, padraoLower);
            
            if (posicao != -1) {
                contadores[0]++;
                int ocorrencias = bm.countOccurrences(modelo, padraoLower);
                contadores[1] += ocorrencias;
                
                final Carro carroFinal = carro;
                final int posFinal = posicao;
                final int ocorrFinal = ocorrencias;
                
                SwingUtilities.invokeLater(() -> {
                    tableModel.addRow(new Object[]{
                        carroFinal.getId(),
                        carroFinal.getModelo(),
                        posFinal,
                        ocorrFinal,
                        String.format("R$ %.2f", carroFinal.getPreco())
                    });
                    
                    txtResultados.append("✓ ID: " + carroFinal.getId() + " | Modelo: " + carroFinal.getModelo() + "\n");
                    txtResultados.append("  Posição: " + posFinal + " | Ocorrências: " + ocorrFinal + "\n");
                    txtResultados.append("  Preço: R$ " + String.format("%.2f", carroFinal.getPreco()) + "\n\n");
                });
            }
        }
        
        long fim = System.nanoTime();
        final long tempo = fim - inicio;
        
        SwingUtilities.invokeLater(() -> {
            txtResultados.append("=".repeat(60) + "\n");
            txtResultados.append("📊 RESUMO:\n");
            txtResultados.append("Carros encontrados: " + contadores[0] + "/" + totalCarros + "\n");
            txtResultados.append("Total de ocorrências: " + contadores[1] + "\n");
            txtResultados.append("Tempo de execução: " + String.format("%.3f", tempo/1000000.0) + " ms\n");
            txtResultados.append("Algoritmo: Boyer-Moore (Bad Character Heuristic)\n");
            
            lblEstatisticas.setText(
                "Boyer-Moore: " + contadores[0] + " carros em " + 
                String.format("%.3f", tempo/1000000.0) + " ms"
            );
        });
    }
    
    private void compararAlgoritmos(String padrao, ArrayList<Carro> carros) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            txtResultados.setText("");
            txtResultados.append("🔍 COMPARAÇÃO KMP vs BOYER-MOORE\n");
            txtResultados.append("=".repeat(60) + "\n");
            txtResultados.append("Padrão: '" + padrao + "'\n");
            txtResultados.append("Total de carros: " + carros.size() + "\n");
            txtResultados.append("Executando comparação...\n\n");
        });
        
        KMP kmp = new KMP();
        BoyerMoore bm = new BoyerMoore();
        final int totalCarros = carros.size();
        
        // Executa KMP
        long inicioKMP = System.nanoTime();
        List<Carro> encontradosKMP = new ArrayList<>();
        final int[] ocorrenciasKMP = new int[1];
        
        for (Carro carro : carros) {
            String modelo = carro.getModelo().toLowerCase();
            String padraoLower = padrao.toLowerCase();
            
            if (kmp.search(modelo, padraoLower) != -1) {
                encontradosKMP.add(carro);
                ocorrenciasKMP[0] += kmp.countOccurrences(modelo, padraoLower);
            }
        }
        
        long fimKMP = System.nanoTime();
        final long tempoKMP = fimKMP - inicioKMP;
        
        // Executa Boyer-Moore
        long inicioBM = System.nanoTime();
        List<Carro> encontradosBM = new ArrayList<>();
        final int[] ocorrenciasBM = new int[1];
        
        for (Carro carro : carros) {
            String modelo = carro.getModelo().toLowerCase();
            String padraoLower = padrao.toLowerCase();
            
            if (bm.search(modelo, padraoLower) != -1) {
                encontradosBM.add(carro);
                ocorrenciasBM[0] += bm.countOccurrences(modelo, padraoLower);
            }
        }
        
        long fimBM = System.nanoTime();
        final long tempoBM = fimBM - inicioBM;
        
        SwingUtilities.invokeLater(() -> {
            // Preenche tabela com resultados
            for (Carro carro : encontradosKMP) {
                int ocorr = kmp.countOccurrences(carro.getModelo().toLowerCase(), padrao.toLowerCase());
                tableModel.addRow(new Object[]{
                    carro.getId(),
                    carro.getModelo(),
                    "Várias",
                    ocorr,
                    String.format("R$ %.2f", carro.getPreco())
                });
            }
            
            txtResultados.append("📊 RESULTADOS DA COMPARAÇÃO:\n");
            txtResultados.append("-".repeat(60) + "\n");
            txtResultados.append(String.format("%-20s %-12s %-15s %-15s\n", 
                "ALGORITMO", "ENCONTRADOS", "OCORRÊNCIAS", "TEMPO (ms)"));
            txtResultados.append(String.format("%-20s %-12d %-15d %-15.3f\n", 
                "KMP", encontradosKMP.size(), ocorrenciasKMP[0], tempoKMP/1000000.0));
            txtResultados.append(String.format("%-20s %-12d %-15d %-15.3f\n", 
                "Boyer-Moore", encontradosBM.size(), ocorrenciasBM[0], tempoBM/1000000.0));
            txtResultados.append("-".repeat(60) + "\n\n");
            
            // Análise
            txtResultados.append("📈 ANÁLISE DE DESEMPENHO:\n");
            double diferenca = Math.abs(tempoKMP - tempoBM) / 1000000.0;
            
            if (tempoKMP < tempoBM) {
                txtResultados.append("• KMP foi " + String.format("%.3f", diferenca) + " ms mais rápido\n");
            } else {
                txtResultados.append("• Boyer-Moore foi " + String.format("%.3f", diferenca) + " ms mais rápido\n");
            }
            
            if (encontradosKMP.size() == encontradosBM.size()) {
                txtResultados.append("• Ambos encontraram a mesma quantidade de carros\n");
            } else {
                txtResultados.append("• ⚠️  Algoritmos encontraram quantidades diferentes!\n");
                txtResultados.append("  KMP: " + encontradosKMP.size() + ", Boyer-Moore: " + encontradosBM.size() + "\n");
            }
            
            txtResultados.append("• Ocorrências totais - KMP: " + ocorrenciasKMP[0] + 
                               ", Boyer-Moore: " + ocorrenciasBM[0] + "\n");
            
            // Mostra alguns carros encontrados
            if (!encontradosKMP.isEmpty()) {
                txtResultados.append("\n🚗 EXEMPLOS ENCONTRADOS (primeiros 3):\n");
                int limite = Math.min(3, encontradosKMP.size());
                for (int i = 0; i < limite; i++) {
                    Carro c = encontradosKMP.get(i);
                    txtResultados.append((i+1) + ". " + c.getModelo() + 
                                       " (ID: " + c.getId() + ", R$ " + 
                                       String.format("%.2f", c.getPreco()) + ")\n");
                }
            }
            
            lblEstatisticas.setText(
                "Comparação: KMP=" + encontradosKMP.size() + " | BM=" + encontradosBM.size() + 
                " | Diferença: " + String.format("%.3f", diferenca) + " ms"
            );
        });
    }
    
    private void mostrarDetalhesCarro() {
        int linhaSelecionada = tabelaResultados.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int id = (int) tableModel.getValueAt(linhaSelecionada, 0);
            
            try {
                Carro carro = carroDAO.read(id);
                if (carro != null) {
                    int posicao = (int) tableModel.getValueAt(linhaSelecionada, 2);
                    int ocorrencias = (int) tableModel.getValueAt(linhaSelecionada, 3);
                    
                    String detalhes = "🚗 DETALHES DO CARRO\n\n" +
                                    "ID: " + carro.getId() + "\n" +
                                    "Modelo: " + carro.getModelo() + "\n" +
                                    "Cores: " + String.join(", ", carro.getCores()) + "\n" +
                                    "Data Fabricação: " + carro.getData_fabricacao() + "\n" +
                                    "Preço: R$ " + String.format("%.2f", carro.getPreco()) + "\n\n" +
                                    "Padrão encontrado na posição: " + posicao + "\n" +
                                    "Ocorrências: " + ocorrencias;
                    
                    JOptionPane.showMessageDialog(this, detalhes,
                                                 "Detalhes do Carro",
                                                 JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar detalhes: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarEstatisticas() {
        try {
            ArrayList<Carro> carros = carroDAO.readAll();
            
            if (carros.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há carros cadastrados!",
                                             "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Calcula estatísticas básicas
            int totalCarros = carros.size();
            double precoTotal = 0;
            double precoMin = Double.MAX_VALUE;
            double precoMax = 0;
            int modelosUnicos = 0;
            java.util.Set<String> modelos = new java.util.HashSet<>();
            
            for (Carro c : carros) {
                double preco = c.getPreco();
                precoTotal += preco;
                if (preco < precoMin) precoMin = preco;
                if (preco > precoMax) precoMax = preco;
                modelos.add(c.getModelo().toLowerCase());
            }
            
            modelosUnicos = modelos.size();
            double precoMedio = precoTotal / totalCarros;
            
            String estatisticas = "📊 ESTATÍSTICAS DE CARROS\n\n" +
                                "Total de carros: " + totalCarros + "\n" +
                                "Modelos únicos: " + modelosUnicos + "\n" +
                                "Preço médio: R$ " + String.format("%.2f", precoMedio) + "\n" +
                                "Preço mínimo: R$ " + String.format("%.2f", precoMin) + "\n" +
                                "Preço máximo: R$ " + String.format("%.2f", precoMax) + "\n" +
                                "Valor total em estoque: R$ " + String.format("%.2f", precoTotal) + "\n\n" +
                                "Para pesquisar padrões, digite acima e clique em Pesquisar.";
            
            JOptionPane.showMessageDialog(this, estatisticas,
                                         "Estatísticas do Sistema",
                                         JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao calcular estatísticas: " + e.getMessage(),
                                         "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparResultados() {
        tableModel.setRowCount(0);
        txtResultados.setText("=== PESQUISA POR PADRÃO ===\n" +
                             "Digite um padrão e clique em Pesquisar.\n" +
                             "Exemplos: 'Gol', 'Civic', 'Fusca', 'Palio'\n\n");
        lblEstatisticas.setText("Pronto para pesquisar. Digite um padrão acima.");
        txtPadrao.requestFocus();
    }
}