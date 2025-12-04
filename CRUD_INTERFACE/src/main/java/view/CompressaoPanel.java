package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import util.*;

public class CompressaoPanel extends JPanel {
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtArquivoSaida;
    private JList<String> listArquivos;
    private DefaultListModel<String> listModel;
    private JLabel lblStatus;
    private JButton btnSelecionarArquivos;
    private JButton btnRemoverArquivos;
    private JButton btnComprimir;
    private JButton btnDescomprimir;
    private JButton btnSelecionarCompactado;
    private JTextField txtArquivoCompactado;
    private JTextField txtPastaDestino;
    
    public CompressaoPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba 1: Compressão
        tabbedPane.addTab("🗜️ Comprimir", criarPainelCompressao());
        
        // Aba 2: Descompressão
        tabbedPane.addTab("📤 Descomprimir", criarPainelDescompressao());
        
        // Aba 3: Informações
        tabbedPane.addTab("📋 Informações", criarPainelInformacoes());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Painel de status
        JPanel painelStatus = new JPanel(new BorderLayout());
        painelStatus.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblStatus = new JLabel("Pronto para comprimir/descomprimir arquivos");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        painelStatus.add(lblStatus, BorderLayout.CENTER);
        
        add(painelStatus, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelCompressao() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior - Configurações
        JPanel painelConfig = new JPanel(new GridBagLayout());
        painelConfig.setBorder(BorderFactory.createTitledBorder("⚙️ Configurações da Compressão"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Algoritmo
        gbc.gridx = 0; gbc.gridy = 0;
        painelConfig.add(new JLabel("Algoritmo:"), gbc);
        
        gbc.gridx = 1;
        comboAlgoritmo = new JComboBox<>(new String[]{"Huffman (.huf)", "LZW (.lzw)"});
        comboAlgoritmo.setPreferredSize(new Dimension(150, 25));
        painelConfig.add(comboAlgoritmo, gbc);
        
        // Nome do arquivo compactado
        gbc.gridx = 0; gbc.gridy = 1;
        painelConfig.add(new JLabel("Nome do Backup:"), gbc);
        
        gbc.gridx = 1;
        txtArquivoSaida = new JTextField("backup_" + System.currentTimeMillis());
        painelConfig.add(txtArquivoSaida, gbc);
        
        gbc.gridx = 2;
        JButton btnGerarNome = ButtonStyler.createStyledButton("🔄", ButtonStyler.COLOR_PRIMARY);
        btnGerarNome.setPreferredSize(new Dimension(40, 25));
        btnGerarNome.addActionListener(e -> {
            txtArquivoSaida.setText("backup_" + System.currentTimeMillis());
        });
        painelConfig.add(btnGerarNome, gbc);
        
        // Botão comprimir
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        btnComprimir = ButtonStyler.createStyledButton("🚀 COMPRIMIR ARQUIVOS", ButtonStyler.COLOR_SUCCESS);
        btnComprimir.setFont(new Font("Arial", Font.BOLD, 14));
        btnComprimir.setPreferredSize(new Dimension(300, 40));
        btnComprimir.addActionListener(e -> executarCompressao());
        painelConfig.add(btnComprimir, gbc);
        
        painel.add(painelConfig, BorderLayout.NORTH);
        
        // Painel central - Lista de arquivos
        JPanel painelArquivos = new JPanel(new BorderLayout(10, 10));
        painelArquivos.setBorder(BorderFactory.createTitledBorder("📋 Arquivos para Compactar"));
        
        listModel = new DefaultListModel<>();
        listArquivos = new JList<>(listModel);
        listArquivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollArquivos = new JScrollPane(listArquivos);
        
        JPanel painelBotoesArquivos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSelecionarArquivos = ButtonStyler.createStyledButton("➕ Adicionar Arquivos", ButtonStyler.COLOR_PRIMARY);
        btnSelecionarArquivos.addActionListener(e -> selecionarArquivos());
        painelBotoesArquivos.add(btnSelecionarArquivos);
        
        btnRemoverArquivos = ButtonStyler.createStyledButton("➖ Remover Selecionados", ButtonStyler.COLOR_DANGER);
        btnRemoverArquivos.addActionListener(e -> removerArquivosSelecionados());
        painelBotoesArquivos.add(btnRemoverArquivos);
        
        JButton btnAdicionarDBs = ButtonStyler.createStyledButton("🗃️ Adicionar Arquivos DB", ButtonStyler.COLOR_PRIMARY);
        btnAdicionarDBs.addActionListener(e -> adicionarArquivosDB());
        painelBotoesArquivos.add(btnAdicionarDBs);
        
        painelArquivos.add(scrollArquivos, BorderLayout.CENTER);
        painelArquivos.add(painelBotoesArquivos, BorderLayout.SOUTH);
        
        painel.add(painelArquivos, BorderLayout.CENTER);
        
        return painel;
    }
    
    private JPanel criarPainelDescompressao() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("DESCOMPRESSÃO DE ARQUIVOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(155, 89, 182));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblTitulo, gbc);
        
        // Ícone
        gbc.gridy = 1;
        JLabel lblIcone = new JLabel("📤");
        lblIcone.setFont(new Font("Arial", Font.PLAIN, 72));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblIcone, gbc);
        
        // Arquivo compactado
        gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel lblArquivo = new JLabel("Arquivo Compactado:");
        lblArquivo.setFont(new Font("Arial", Font.BOLD, 12));
        painel.add(lblArquivo, gbc);
        
        gbc.gridx = 1;
        JPanel painelArquivo = new JPanel(new BorderLayout(5, 0));
        txtArquivoCompactado = new JTextField();
        txtArquivoCompactado.setEditable(false);
        painelArquivo.add(txtArquivoCompactado, BorderLayout.CENTER);
        
        btnSelecionarCompactado = ButtonStyler.createStyledButton("📁 Procurar", ButtonStyler.COLOR_PRIMARY);
        btnSelecionarCompactado.setPreferredSize(new Dimension(100, 25));
        btnSelecionarCompactado.addActionListener(e -> selecionarArquivoCompactado());
        painelArquivo.add(btnSelecionarCompactado, BorderLayout.EAST);
        painel.add(painelArquivo, gbc);
        
        // Pasta destino
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblDestino = new JLabel("Pasta Destino:");
        lblDestino.setFont(new Font("Arial", Font.BOLD, 12));
        painel.add(lblDestino, gbc);
        
        gbc.gridx = 1;
        JPanel painelDestino = new JPanel(new BorderLayout(5, 0));
        txtPastaDestino = new JTextField("dados");
        painelDestino.add(txtPastaDestino, BorderLayout.CENTER);
        
        JButton btnProcurarDestino = ButtonStyler.createStyledButton("📁", ButtonStyler.COLOR_PRIMARY);
        btnProcurarDestino.setPreferredSize(new Dimension(40, 25));
        btnProcurarDestino.addActionListener(e -> selecionarPastaDestino());
        painelDestino.add(btnProcurarDestino, BorderLayout.EAST);
        painel.add(painelDestino, gbc);
        
        // Botão descomprimir
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        btnDescomprimir = ButtonStyler.createStyledButton("📥 DESCOMPACTAR ARQUIVO", ButtonStyler.COLOR_WARNING);
        btnDescomprimir.setFont(new Font("Arial", Font.BOLD, 16));
        btnDescomprimir.setPreferredSize(new Dimension(300, 50));
        btnDescomprimir.addActionListener(e -> executarDescompressao());
        painel.add(btnDescomprimir, gbc);
        
        return painel;
    }
    
    private JPanel criarPainelInformacoes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea txtInfo = new JTextArea(
            "📋 SISTEMA DE COMPRESSÃO DE ARQUIVOS\n\n" +
            "Esta ferramenta permite compactar e descompactar arquivos do sistema\n" +
            "usando diferentes algoritmos de compressão.\n\n" +
            "📁 ARQUIVOS DO SISTEMA:\n" +
            "   • clientes.db         - Banco de dados de clientes\n" +
            "   • vendedores.db       - Banco de dados de vendedores\n" +
            "   • carros.db          - Banco de dados de veículos\n" +
            "   • vendas.db          - Registro de vendas\n" +
            "   • carro_venda.db     - Relacionamento carros/vendas\n" +
            "   • Índices diversos   - Arquivos de índice para buscas rápidas\n\n" +
            "⚙️ ALGORITMOS DISPONÍVEIS:\n" +
            "   • HUFFMAN (.huf) - Algoritmo de codificação de Huffman\n" +
            "     - Ideal para arquivos de texto\n" +
            "     - Compressão baseada em frequência de caracteres\n" +
            "     - Gera arquivos com extensão .huf\n\n" +
            "   • LZW (.lzw) - Algoritmo Lempel-Ziv-Welch\n" +
            "     - Excelente para dados com repetições\n" +
            "     - Cria dicionário dinâmico de padrões\n" +
            "     - Gera arquivos com extensão .lzw\n\n" +
            "📍 LOCAL DE ARMAZENAMENTO:\n" +
            "   • Arquivos compactados são salvos na pasta 'backups'\n" +
            "   • Arquivos descompactados são restaurados na pasta 'dados'\n\n" +
            "⚠️ RECOMENDAÇÕES:\n" +
            "   • Faça backups regularmente\n" +
            "   • Mantenha diferentes versões de backup\n" +
            "   • Teste a descompressão periodicamente"
        );
        
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtInfo.setBackground(getBackground());
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(txtInfo);
        painel.add(scroll, BorderLayout.CENTER);
        
        return painel;
    }
    
    private void selecionarArquivos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Selecionar Arquivos para Compressão");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                listModel.addElement(file.getAbsolutePath());
            }
            atualizarStatus("Adicionados " + fileChooser.getSelectedFiles().length + " arquivos");
        }
    }
    
    private void adicionarArquivosDB() {
        String[] arquivosDB = {
            "clientes.db",
            "vendedores.db",
            "carros.db",
            "vendas.db",
            "carro_venda.db"
        };
        
        int count = 0;
        for (String arquivo : arquivosDB) {
            File file = new File(arquivo);
            if (file.exists()) {
                listModel.addElement(file.getAbsolutePath());
                count++;
            }
        }
        
        atualizarStatus("Adicionados " + count + " arquivos DB do sistema");
    }
    
    private void removerArquivosSelecionados() {
        int[] indices = listArquivos.getSelectedIndices();
        if (indices.length > 0) {
            for (int i = indices.length - 1; i >= 0; i--) {
                listModel.remove(indices[i]);
            }
            atualizarStatus("Removidos " + indices.length + " arquivos");
        }
    }
    
    private void selecionarArquivoCompactado() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("backups"));
        if (!fileChooser.getCurrentDirectory().exists()) {
            fileChooser.setCurrentDirectory(new File("."));
        }
        
        fileChooser.setDialogTitle("Selecionar Arquivo Compactado");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".huf") 
                    || f.getName().toLowerCase().endsWith(".lzw");
            }
            
            @Override
            public String getDescription() {
                return "Arquivos Compactados (*.huf, *.lzw)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            txtArquivoCompactado.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void selecionarPastaDestino() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File("dados"));
        if (!fileChooser.getCurrentDirectory().exists()) {
            fileChooser.setCurrentDirectory(new File("."));
        }
        
        fileChooser.setDialogTitle("Selecionar Pasta Destino");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            txtPastaDestino.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void executarCompressao() {
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Selecione pelo menos um arquivo para compactar.",
                "Nenhum Arquivo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obter arquivos da lista
        List<String> arquivos = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            arquivos.add(listModel.get(i));
        }
        
        // Obter algoritmo selecionado
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
        String extensao = algoritmo.contains("Huffman") ? ".huf" : ".lzw";
        String nomeArquivo = txtArquivoSaida.getText().trim();
        
        if (nomeArquivo.isEmpty()) {
            nomeArquivo = "backup_" + System.currentTimeMillis();
        }
        
        if (!nomeArquivo.endsWith(extensao)) {
            nomeArquivo += extensao;
        }
        
        // Criar pasta backups se não existir
        File pastaBackups = new File("backups");
        if (!pastaBackups.exists()) {
            pastaBackups.mkdirs();
        }
        
        String caminhoCompleto = "backups" + File.separator + nomeArquivo;
        
        // Desabilitar botões durante a operação
        btnComprimir.setEnabled(false);
        btnSelecionarArquivos.setEnabled(false);
        btnRemoverArquivos.setEnabled(false);
        
        // Executar em thread separada para não travar a interface
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    atualizarStatus("Iniciando compressão...");
                    
                    Compressor compressor = algoritmo.contains("Huffman") ? 
                        new CompressorHuffman() : new CompressorLZW();
                    
                    // Converter lista para array
                    String[] arrayArquivos = arquivos.toArray(new String[0]);
                    
                    // Executar compressão
                    compressor.comprimir(arrayArquivos, caminhoCompleto);
                    
                    atualizarStatus("Compressão concluída com sucesso!");
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "Arquivos compactados com sucesso!\n" +
                            "Local: " + caminhoCompleto,
                            "Compressão Concluída",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "Erro durante a compressão:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    atualizarStatus("Erro na compressão: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Reabilitar botões
                btnComprimir.setEnabled(true);
                btnSelecionarArquivos.setEnabled(true);
                btnRemoverArquivos.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void executarDescompressao() {
        String arquivoCompactado = txtArquivoCompactado.getText().trim();
        String pastaDestino = txtPastaDestino.getText().trim();
        
        if (arquivoCompactado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecione um arquivo compactado para descomprimir.",
                "Arquivo não selecionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (pastaDestino.isEmpty()) {
            pastaDestino = "dados";
            txtPastaDestino.setText(pastaDestino);
        }
        
        File arquivo = new File(arquivoCompactado);
        if (!arquivo.exists()) {
            JOptionPane.showMessageDialog(this,
                "Arquivo não encontrado: " + arquivoCompactado,
                "Arquivo não existe",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Criar pasta destino se não existir
        File pasta = new File(pastaDestino);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        
        // Desabilitar botões durante a operação
        btnDescomprimir.setEnabled(false);
        btnSelecionarCompactado.setEnabled(false);
        
        // Corrigido: passar as variáveis como final para o SwingWorker
        final String arquivoFinal = arquivoCompactado;
        final String pastaFinal = pastaDestino;
        
        // Executar em thread separada
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    atualizarStatus("Iniciando descompressão...");
                    
                    // Verificar tipo de arquivo pelo nome
                    Compressor compressor;
                    if (arquivoFinal.toLowerCase().endsWith(".huf")) {
                        compressor = new CompressorHuffman();
                    } else if (arquivoFinal.toLowerCase().endsWith(".lzw")) {
                        compressor = new CompressorLZW();
                    } else {
                        throw new Exception("Formato de arquivo não suportado. Use .huf ou .lzw");
                    }
                    
                    // Executar descompressão
                    compressor.descomprimir(arquivoFinal, pastaFinal);
                    
                    atualizarStatus("Descompressão concluída com sucesso!");
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "Arquivo descompactado com sucesso!\n" +
                            "Arquivos restaurados em: " + pastaFinal,
                            "Descompressão Concluída",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "Erro durante a descompressão:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    atualizarStatus("Erro na descompressão: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Reabilitar botões
                btnDescomprimir.setEnabled(true);
                btnSelecionarCompactado.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void atualizarStatus(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(mensagem);
        });
    }
}