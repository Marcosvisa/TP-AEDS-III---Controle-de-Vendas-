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
    private JLabel lblStatus;
    private JButton btnComprimir;
    private JButton btnDescomprimir;
    private JButton btnSelecionarCompactado;
    private JTextField txtArquivoCompactado;
    private JLabel lblContagemArquivos;
    
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
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel painelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("COMPRESSÃO DE BACKUP");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(52, 152, 219));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(lblTitulo, gbc);
        
        // Ícone
        gbc.gridy = 1;
        JLabel lblIcone = new JLabel("🗜️");
        lblIcone.setFont(new Font("Arial", Font.PLAIN, 72));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(lblIcone, gbc);
        
        // Informação sobre arquivos
        gbc.gridy = 2;
        JLabel lblInfo = new JLabel("<html><div style='text-align: center;'>"
                + "Serão comprimidos <b>TODOS</b> os arquivos da pasta <b>dados/</b><br>"
                + "(todos os arquivos .db e .dat encontrados)"
                + "</div></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(lblInfo, gbc);
        
        // Contagem de arquivos
        gbc.gridy = 3;
        lblContagemArquivos = new JLabel("Carregando...");
        lblContagemArquivos.setFont(new Font("Arial", Font.BOLD, 12));
        lblContagemArquivos.setHorizontalAlignment(SwingConstants.CENTER);
        lblContagemArquivos.setForeground(new Color(39, 174, 96));
        painelCentral.add(lblContagemArquivos, gbc);
        
        // Atualizar contagem em thread separada
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int contagem;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<String> arquivos = obterArquivosDados();
                contagem = arquivos.size();
                return null;
            }
            
            @Override
            protected void done() {
                lblContagemArquivos.setText("Arquivos encontrados: " + contagem + " arquivo(s)");
            }
        };
        worker.execute();
        
        // Botão para atualizar contagem - USANDO COR DISPONÍVEL
        gbc.gridy = 4;
        JButton btnAtualizarContagem = ButtonStyler.createStyledButton("🔄 Atualizar Contagem", new Color(52, 152, 219)); // Azul
        btnAtualizarContagem.addActionListener(e -> atualizarContagemArquivos());
        painelCentral.add(btnAtualizarContagem, gbc);
        
        // Algoritmo
        gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.gridx = 0;
        painelCentral.add(new JLabel("Algoritmo:"), gbc);
        
        gbc.gridx = 1;
        comboAlgoritmo = new JComboBox<>(new String[]{"Huffman (.huf)", "LZW (.lzw)"});
        comboAlgoritmo.setPreferredSize(new Dimension(200, 30));
        painelCentral.add(comboAlgoritmo, gbc);
        
        // Nome do arquivo compactado
        gbc.gridx = 0; gbc.gridy = 6;
        painelCentral.add(new JLabel("Nome do Backup:"), gbc);
        
        gbc.gridx = 1;
        JPanel painelNome = new JPanel(new BorderLayout(5, 0));
        txtArquivoSaida = new JTextField("backup_" + System.currentTimeMillis());
        painelNome.add(txtArquivoSaida, BorderLayout.CENTER);
        
        JButton btnGerarNome = ButtonStyler.createStyledButton("🔄", ButtonStyler.COLOR_PRIMARY);
        btnGerarNome.setPreferredSize(new Dimension(40, 30));
        btnGerarNome.addActionListener(e -> {
            txtArquivoSaida.setText("backup_" + System.currentTimeMillis());
        });
        painelNome.add(btnGerarNome, BorderLayout.EAST);
        painelCentral.add(painelNome, gbc);
        
        // Botão comprimir
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        btnComprimir = ButtonStyler.createStyledButton("🚀 GERAR BACKUP COMPACTADO", ButtonStyler.COLOR_SUCCESS);
        btnComprimir.setFont(new Font("Arial", Font.BOLD, 16));
        btnComprimir.setPreferredSize(new Dimension(350, 50));
        btnComprimir.addActionListener(e -> executarCompressao());
        painelCentral.add(btnComprimir, gbc);
        
        painel.add(painelCentral, BorderLayout.CENTER);
        
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
        JLabel lblTitulo = new JLabel("RESTAURAÇÃO DE BACKUP");
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
        
        // Informação
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("<html><div style='text-align: center;'>"
                + "Os arquivos serão descompactados na pasta <b>dados/</b><br>"
                + "(substituindo os arquivos .db e .dat existentes)"
                + "</div></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblInfo, gbc);
        
        // Botão descomprimir
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        btnDescomprimir = ButtonStyler.createStyledButton("📥 RESTAURAR BACKUP", ButtonStyler.COLOR_WARNING);
        btnDescomprimir.setFont(new Font("Arial", Font.BOLD, 16));
        btnDescomprimir.setPreferredSize(new Dimension(300, 50));
        btnDescomprimir.addActionListener(e -> executarDescompressao()); // CORRIGIDO: executarDescompressao() com um 's'
        painel.add(btnDescomprimir, gbc);
        
        return painel;
    }
    
    private JPanel criarPainelInformacoes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea txtInfo = new JTextArea(
            "📋 SISTEMA DE BACKUP E COMPRESSÃO\n\n" +
            "Esta ferramenta permite criar backups compactados de todos os\n" +
            "arquivos de dados do sistema de vendas e restaurá-los quando necessário.\n\n" +
            "📁 O QUE É INCLUÍDO NO BACKUP:\n" +
            "   • Todos os arquivos com extensão .db da pasta dados/\n" +
            "   • Todos os arquivos com extensão .dat da pasta dados/\n" +
            "   • Inclui bancos de dados principais\n" +
            "   • Inclui arquivos de índice e cache\n" +
            "   • Inclui tabelas temporárias\n\n" +
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
            "   • Backups compactados são salvos na pasta 'backups/'\n" +
            "   • Arquivos são restaurados na pasta 'dados/'\n\n" +
            "⚠️ RECOMENDAÇÕES:\n" +
            "   • Faça backups regularmente\n" +
            "   • Mantenha diferentes versões de backup\n" +
            "   • Teste a restauração periodicamente\n" +
            "   • Renomeie backups com datas significativas\n" +
            "   • Verifique se há espaço em disco suficiente"
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
    
    private List<String> obterArquivosDados() {
        List<String> arquivos = new ArrayList<>();
        File pastaDados = new File("dados");
        
        if (!pastaDados.exists() || !pastaDados.isDirectory()) {
            System.out.println("Pasta 'dados' não encontrada: " + pastaDados.getAbsolutePath());
            return arquivos;
        }
        
        // Listar todos os arquivos .db e .dat da pasta dados
        File[] arquivosEncontrados = pastaDados.listFiles((dir, nome) -> 
            nome.toLowerCase().endsWith(".db") || nome.toLowerCase().endsWith(".dat")
        );
        
        if (arquivosEncontrados != null) {
            for (File file : arquivosEncontrados) {
                if (file.isFile()) {
                    arquivos.add(file.getAbsolutePath());
                    System.out.println("Arquivo encontrado: " + file.getName() + " (" + file.length() + " bytes)");
                }
            }
        }
        
        return arquivos;
    }
    
    private void atualizarContagemArquivos() {
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return obterArquivosDados().size();
            }
            
            @Override
            protected void done() {
                try {
                    int contagem = get();
                    lblContagemArquivos.setText("Arquivos encontrados: " + contagem + " arquivo(s)");
                    atualizarStatus("Contagem atualizada: " + contagem + " arquivos");
                } catch (Exception e) {
                    lblContagemArquivos.setText("Erro ao contar arquivos");
                }
            }
        };
        worker.execute();
    }
    
    private void executarCompressao() {
        // Obter lista de arquivos da pasta dados
        List<String> arquivos = obterArquivosDados();
        
        if (arquivos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nenhum arquivo (.db ou .dat) encontrado na pasta dados/.\n"
                + "Verifique se a pasta existe e contém arquivos.",
                "Nenhum Arquivo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calcular tamanho total original ANTES do SwingWorker
        final long[] tamanhoTotalOriginalRef = {0};
        for (String arquivo : arquivos) {
            tamanhoTotalOriginalRef[0] += new File(arquivo).length();
        }
        final long tamanhoTotalOriginal = tamanhoTotalOriginalRef[0];
        
        // Mostrar lista de arquivos que serão comprimidos
        StringBuilder listaArquivos = new StringBuilder();
        for (int i = 0; i < Math.min(arquivos.size(), 10); i++) {
            File file = new File(arquivos.get(i));
            listaArquivos.append("• ").append(file.getName()).append(" (")
                       .append(file.length()).append(" bytes)\n");
        }
        if (arquivos.size() > 10) {
            listaArquivos.append("• ... e mais ").append(arquivos.size() - 10).append(" arquivos\n");
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "📦 CONFIRMAR BACKUP\n\n" +
            "Serão comprimidos " + arquivos.size() + " arquivo(s):\n\n" +
            listaArquivos.toString() + "\n" +
            "Tamanho total: " + tamanhoTotalOriginal + " bytes\n\n" +
            "Deseja continuar?",
            "Confirmar Backup",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
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
        
        final String caminhoCompleto = "backups" + File.separator + nomeArquivo;
        final List<String> arquivosFinal = arquivos;
        final String algoritmoFinal = algoritmo; // Corrigido: declarar como final
        
        // Desabilitar botão durante a operação
        btnComprimir.setEnabled(false);
        
        // Mostrar quantos arquivos serão comprimidos
        atualizarStatus("Preparando para compactar " + arquivos.size() + " arquivos...");
        
        // Executar em thread separada para não travar a interface
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    atualizarStatus("Iniciando compressão de " + arquivosFinal.size() + " arquivos...");
                    
                    Compressor compressor = algoritmoFinal.contains("Huffman") ? 
                        new CompressorHuffman() : new CompressorLZW();
                    
                    // Converter lista para array
                    String[] arrayArquivos = arquivosFinal.toArray(new String[0]);
                    
                    // Executar compressão
                    compressor.comprimir(arrayArquivos, caminhoCompleto);
                    
                    atualizarStatus("Compressão concluída com sucesso!");
                    
                    File arquivoBackup = new File(caminhoCompleto);
                    long tamanhoBackup = arquivoBackup.length();
                    
                    // Calcular taxa de compressão
                    double taxaCompressao = 0;
                    if (tamanhoTotalOriginal > 0) {
                        taxaCompressao = 100.0 - ((double) tamanhoBackup / tamanhoTotalOriginal * 100);
                    }
                    
                    final long tamanhoBackupFinal = tamanhoBackup;
                    final double taxaCompressaoFinal = taxaCompressao;
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "✅ BACKUP CRIADO COM SUCESSO!\n\n" +
                            "Arquivos comprimidos: " + arquivosFinal.size() + "\n" +
                            "Local: " + caminhoCompleto + "\n" +
                            "Tamanho original: " + tamanhoTotalOriginal + " bytes\n" +
                            "Tamanho backup: " + tamanhoBackupFinal + " bytes\n" +
                            String.format("Taxa de compressão: %.1f%%", taxaCompressaoFinal),
                            "Backup Concluído",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "❌ ERRO DURANTE O BACKUP:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    });
                    atualizarStatus("Erro na compressão: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Reabilitar botão
                btnComprimir.setEnabled(true);
                // Atualizar contagem
                atualizarContagemArquivos();
            }
        };
        
        worker.execute();
    }
    
    // MÉTODO CORRIGIDO: executarDescompressao() com um 's'
    private void executarDescompressao() {
        String arquivoCompactado = txtArquivoCompactado.getText().trim();
        
        if (arquivoCompactado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecione um arquivo compactado para restaurar.",
                "Arquivo não selecionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File arquivo = new File(arquivoCompactado);
        if (!arquivo.exists()) {
            JOptionPane.showMessageDialog(this,
                "Arquivo não encontrado: " + arquivoCompactado,
                "Arquivo não existe",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Perguntar confirmação antes de substituir arquivos
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️  ATENÇÃO - RESTAURAÇÃO DE BACKUP\n\n" +
            "Esta operação substituirá TODOS os arquivos .db e .dat da pasta dados/\n" +
            "pelos arquivos do backup.\n\n" +
            "Qualquer arquivo atual será perdido!\n\n" +
            "Tem certeza que deseja continuar?",
            "Confirmar Restauração",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Criar pasta dados se não existir
        File pastaDados = new File("dados");
        if (!pastaDados.exists()) {
            pastaDados.mkdirs();
        }
        
        // Desabilitar botões durante a operação
        btnDescomprimir.setEnabled(false);
        btnSelecionarCompactado.setEnabled(false);
        
        final String arquivoFinal = arquivoCompactado;
        
        // Executar em thread separada
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    atualizarStatus("Iniciando restauração do backup...");
                    
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
                    compressor.descomprimir(arquivoFinal, "dados");
                    
                    atualizarStatus("Restauração concluída com sucesso!");
                    
                    // Contar arquivos restaurados
                    List<String> arquivosRestaurados = obterArquivosDados();
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "✅ BACKUP RESTAURADO COM SUCESSO!\n\n" +
                            "Arquivos restaurados: " + arquivosRestaurados.size() + "\n" +
                            "Local: pasta dados/\n" +
                            "Backup restaurado: " + new File(arquivoFinal).getName(),
                            "Restauração Concluída",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CompressaoPanel.this,
                            "❌ ERRO DURANTE A RESTAURAÇÃO:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    });
                    atualizarStatus("Erro na restauração: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Reabilitar botões
                btnDescomprimir.setEnabled(true);
                btnSelecionarCompactado.setEnabled(true);
                // Atualizar contagem
                atualizarContagemArquivos();
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