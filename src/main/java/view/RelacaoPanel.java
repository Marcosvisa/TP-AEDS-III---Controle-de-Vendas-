package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import util.ButtonStyler;
import dao.*;
import model.*;

public class RelacaoPanel extends JPanel {
    private CarroVendaDAO carroVendaDAO;
    private CarroDAO carroDAO;
    private VendaDAO vendaDAO;
    private IndiceCarroVenda indiceCarroVenda;
    
    private JButton btnConsultarCarro, btnConsultarVenda;
    
    public RelacaoPanel(CarroVendaDAO carroVendaDAO, CarroDAO carroDAO, 
                       VendaDAO vendaDAO, IndiceCarroVenda indiceCarroVenda) {
        this.carroVendaDAO = carroVendaDAO;
        this.carroDAO = carroDAO;
        this.vendaDAO = vendaDAO;
        this.indiceCarroVenda = indiceCarroVenda;
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel principal com título
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 20));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titulo = new JLabel("Consultas Carro-Venda", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 0, 15));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        btnConsultarCarro = ButtonStyler.createStyledButton("Consultar Vendas de um Carro", ButtonStyler.COLOR_PRIMARY);
        btnConsultarCarro.addActionListener(e -> consultarVendasPorCarro());
        
        btnConsultarVenda = ButtonStyler.createStyledButton("Consultar Carros de uma Venda", ButtonStyler.COLOR_PRIMARY);
        btnConsultarVenda.addActionListener(e -> consultarCarrosPorVenda());
        
        JButton btnRecarregar = ButtonStyler.createStyledButton("Atualizar Consultas", ButtonStyler.COLOR_SECONDARY);
        btnRecarregar.addActionListener(e -> mostrarMensagemAtualizacao());
        
        // Ajustar tamanho dos botões
        Dimension tamanhoBotao = new Dimension(300, 50);
        btnConsultarCarro.setPreferredSize(tamanhoBotao);
        btnConsultarVenda.setPreferredSize(tamanhoBotao);
        btnRecarregar.setPreferredSize(tamanhoBotao);
        
        JPanel painelBtnCarro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnCarro.add(btnConsultarCarro);
        
        JPanel painelBtnVenda = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnVenda.add(btnConsultarVenda);
        
        JPanel painelBtnAtualizar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnAtualizar.add(btnRecarregar);
        
        painelBotoes.add(painelBtnCarro);
        painelBotoes.add(painelBtnVenda);
        painelBotoes.add(painelBtnAtualizar);
        
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);
        
        // Descrição/instruções
        JTextArea instrucoes = new JTextArea();
        instrucoes.setText("Esta tela permite realizar consultas específicas sobre as relações entre carros e vendas.\n\n" +
                          "• 'Consultar Vendas de um Carro': Mostra todas as vendas em que um carro específico foi vendido.\n" +
                          "• 'Consultar Carros de uma Venda': Mostra todos os carros incluídos em uma venda específica.\n" +
                          "• 'Atualizar Consultas': Atualiza os dados disponíveis para consulta.");
        instrucoes.setFont(new Font("Arial", Font.PLAIN, 12));
        instrucoes.setEditable(false);
        instrucoes.setOpaque(false);
        instrucoes.setLineWrap(true);
        instrucoes.setWrapStyleWord(true);
        instrucoes.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 30));
        
        painelPrincipal.add(instrucoes, BorderLayout.SOUTH);
        
        add(painelPrincipal, BorderLayout.CENTER);
    }
    
    private void mostrarMensagemAtualizacao() {
        JOptionPane.showMessageDialog(this,
            "Os dados de consulta foram atualizados com sucesso!",
            "Atualização Concluída",
            JOptionPane.INFORMATION_MESSAGE);
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
                        sb.append("Modelo: ").append(carro.getModelo()).append("\n");
                        sb.append("Preço: R$ ").append(carro.getPreco()).append("\n\n");
                    }
                    
                    for (int idVenda : vendas) {
                        Venda venda = vendaDAO.read(idVenda);
                        if (venda != null) {
                            sb.append("• Venda ID: ").append(idVenda)
                              .append(" | Valor: R$ ").append(venda.getValor_total())
                              .append(" | Data: ").append(venda.getData_venda())
                              .append("\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        sb.toString(),
                        "Vendas do Carro",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido! Digite um número válido.",
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
                              .append(" | Modelo: ").append(carro.getModelo())
                              .append(" | Preço: R$ ").append(carro.getPreco())
                              .append("\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        sb.toString(),
                        "Carros da Venda",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido! Digite um número válido.",
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro na consulta: " + e.getMessage(),
                                             "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}