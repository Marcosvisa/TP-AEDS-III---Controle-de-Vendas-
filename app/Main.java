package app;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import model.*;
import dao.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // DAOs atualizados
    private static VendedorDAO vendedorDAO;
    private static ClienteDAO clienteDAO;
    private static CarroDAO carroDAO;
    private static VendaDAO vendaDAO;

    public static void main(String[] args) {
        
        try {
            // Inicializa todos os DAOs
            vendedorDAO = new VendedorDAO();
            clienteDAO = new ClienteDAO();
            carroDAO = new CarroDAO();
            vendaDAO = new VendaDAO();
            
            int opcao = -1;

            do {
                exibirMenuPrincipal();
                try {
                    opcao = Integer.parseInt(sc.nextLine());
                    switch (opcao) {
                        case 1:
                            gerenciarVendedores();
                            break;
                        case 2:
                            gerenciarClientes();
                            break;
                        case 3:
                            gerenciarCarros();
                            break;
                        case 4:
                            gerenciarVendas();
                            break;
                        case 0:
                            System.out.println("Saindo do sistema. Adeus!");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Por favor, digite um número.");
                    opcao = -1; 
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro: " + e.getMessage());
                    e.printStackTrace();
                }
            } while (opcao != 0);
            
            // Fecha os arquivos ao sair
            vendedorDAO.close();
            clienteDAO.close();
            carroDAO.close();
            vendaDAO.close();

        } catch (Exception e) {
            System.err.println("Erro fatal ao inicializar o sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n----------- Menu Principal -----------");
        System.out.println("1 - Gerenciar Vendedores");
        System.out.println("2 - Gerenciar Clientes");
        System.out.println("3 - Gerenciar Carros");
        System.out.println("4 - Gerenciar Vendas");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }
    
    private static LocalDate lerData(String prompt) {
        LocalDate data = null;
        boolean valido = false;
        do {
            System.out.print(prompt + " (formato dd/MM/yyyy): ");
            String dataStr = sc.nextLine();
            if (dataStr.trim().isEmpty()) return null;
            try {
                data = LocalDate.parse(dataStr, DF);
                valido = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Tente novamente.");
            }
        } while (!valido);
        return data;
    }

    // ======================================================================
    // MÉTODOS AUXILIARES PARA BUSCA POR CPF
    // ======================================================================

    private static Vendedor buscarVendedorPorCpf(String cpf) throws Exception {
        ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
        for (Vendedor v : vendedores) {
            if (v.getCpf().equals(cpf)) {
                return v;
            }
        }
        return null;
    }

    private static Cliente buscarClientePorCpf(String cpf) throws Exception {
        ArrayList<Cliente> clientes = clienteDAO.readAll();
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }

    private static boolean deletarVendedorPorCpf(String cpf) throws Exception {
        ArrayList<Vendedor> vendedores = vendedorDAO.readAll();
        for (Vendedor v : vendedores) {
            if (v.getCpf().equals(cpf)) {
                return vendedorDAO.delete(v.getId());
            }
        }
        return false;
    }

    private static boolean deletarClientePorCpf(String cpf) throws Exception {
        ArrayList<Cliente> clientes = clienteDAO.readAll();
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return clienteDAO.delete(c.getId());
            }
        }
        return false;
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE VENDEDORES 
    // ======================================================================

    private static void gerenciarVendedores() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Vendedores -----");
            System.out.println("1 - Incluir Vendedor (Create)");
            System.out.println("2 - Buscar Vendedor por CPF (Read)");
            System.out.println("3 - Listar Todos os Vendedores (Read All)");
            System.out.println("4 - Atualizar Vendedor (Update)");
            System.out.println("5 - Excluir Vendedor (Delete)");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                subOpcao = Integer.parseInt(sc.nextLine());
                switch (subOpcao) {
                    case 1: incluirVendedor(); break;
                    case 2: buscarVendedor(); break;
                    case 3: listarTodosVendedores(); break;
                    case 4: atualizarVendedor(); break;
                    case 5: excluirVendedor(); break;
                    case 0: System.out.println("Voltando..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                subOpcao = -1;
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
                e.printStackTrace();
            }
        } while (subOpcao != 0);
    }
    
    private static void incluirVendedor() throws Exception {
        System.out.println("\n-- Inclusão de Novo Vendedor --");
        
        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        
        // Verifica se já existe vendedor com este CPF
        if (buscarVendedorPorCpf(cpf) != null) {
            System.out.println("Já existe um vendedor com este CPF!");
            return;
        }
        
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        
        System.out.print("Emails (separados por vírgula): ");
        String[] emails = sc.nextLine().split(",");
        for (int i = 0; i < emails.length; i++) {
            emails[i] = emails[i].trim();
        }
        
        LocalDate dataContratacao = lerData("Data de contratação (ou deixe em branco para hoje)");
        if (dataContratacao == null) dataContratacao = LocalDate.now();
        
        Vendedor novoVendedor = new Vendedor(cpf, nome, emails, dataContratacao, 0, 0f);
        vendedorDAO.create(novoVendedor);
        System.out.println("Vendedor incluído com sucesso! CPF: " + cpf);
    }

    private static void buscarVendedor() throws Exception {
        System.out.println("\n-- Busca de Vendedor --");
        System.out.print("CPF do Vendedor a buscar: ");
        String cpf = sc.nextLine();
        
        Vendedor vendedor = buscarVendedorPorCpf(cpf);
        if (vendedor != null) {
            System.out.println("Vendedor encontrado: " + vendedor.toString());
        } else {
            System.out.println("Vendedor com CPF " + cpf + " não encontrado.");
        }
    }

    private static void listarTodosVendedores() throws Exception {
        System.out.println("\n-- Lista de Todos os Vendedores --");
        ArrayList<Vendedor> lista = vendedorDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhum vendedor cadastrado.");
        } else {
            for (Vendedor v : lista) {
                System.out.println(v.toString());
            }
        }
    }

    private static void atualizarVendedor() throws Exception {
        System.out.println("\n-- Atualização de Vendedor --");
        System.out.print("CPF do Vendedor a atualizar: ");
        String cpf = sc.nextLine();
        
        Vendedor vendedor = buscarVendedorPorCpf(cpf);
        if (vendedor != null) {
            System.out.println("Vendedor atual: " + vendedor.toString());

            System.out.print("Novo Nome (deixe em branco para manter '" + vendedor.getNome() + "'): ");
            String novoNome = sc.nextLine();
            if (!novoNome.trim().isEmpty()) {
                vendedor.setNome(novoNome);
            }

            System.out.print("Novos Emails (separados por vírgula, deixe em branco para manter): ");
            String novosEmailsStr = sc.nextLine();
            if (!novosEmailsStr.trim().isEmpty()) {
                String[] novosEmails = novosEmailsStr.split(",");
                for (int i = 0; i < novosEmails.length; i++) {
                    novosEmails[i] = novosEmails[i].trim();
                }
                vendedor.setEmail(novosEmails);
            }

            if (vendedorDAO.update(vendedor)) {
                System.out.println("Vendedor atualizado com sucesso!");
            } else {
                System.out.println("Erro ao atualizar o vendedor.");
            }
        } else {
            System.out.println("Vendedor com CPF " + cpf + " não encontrado.");
        }
    }
    
    private static void excluirVendedor() throws Exception {
        System.out.println("\n-- Exclusão de Vendedor --");
        System.out.print("CPF do Vendedor a excluir: ");
        String cpf = sc.nextLine();
        
        if (deletarVendedorPorCpf(cpf)) {
            System.out.println("Vendedor com CPF " + cpf + " excluído com sucesso!");
        } else {
            System.out.println("Vendedor com CPF " + cpf + " não encontrado.");
        }
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE CLIENTES
    // ======================================================================

    private static void gerenciarClientes() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Clientes -----");
            System.out.println("1 - Incluir Cliente (Create)");
            System.out.println("2 - Buscar Cliente por CPF (Read)");
            System.out.println("3 - Listar Todos os Clientes (Read All)");
            System.out.println("4 - Atualizar Cliente (Update)");
            System.out.println("5 - Excluir Cliente (Delete)");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                subOpcao = Integer.parseInt(sc.nextLine());
                switch (subOpcao) {
                    case 1: incluirCliente(); break;
                    case 2: buscarCliente(); break;
                    case 3: listarTodosClientes(); break;
                    case 4: atualizarCliente(); break;
                    case 5: excluirCliente(); break;
                    case 0: System.out.println("Voltando..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                subOpcao = -1;
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
                e.printStackTrace();
            }
        } while (subOpcao != 0);
    }
    
    private static void incluirCliente() throws Exception {
        System.out.println("\n-- Inclusão de Novo Cliente --");
        
        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        
        // Verifica se já existe cliente com este CPF
        if (buscarClientePorCpf(cpf) != null) {
            System.out.println("Já existe um cliente com este CPF!");
            return;
        }
        
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        
        System.out.print("Emails (separados por vírgula): ");
        String[] emails = sc.nextLine().split(",");
        for (int i = 0; i < emails.length; i++) {
            emails[i] = emails[i].trim();
        }
        
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();
        
        LocalDate dataCadastro = lerData("Data de cadastro (ou deixe em branco para hoje)");
        if (dataCadastro == null) dataCadastro = LocalDate.now();
        
        Cliente novoCliente = new Cliente(cpf, nome, emails, dataCadastro, telefone);
        clienteDAO.create(novoCliente);
        System.out.println("Cliente incluído com sucesso! CPF: " + cpf);
    }

    private static void buscarCliente() throws Exception {
        System.out.println("\n-- Busca de Cliente --");
        System.out.print("CPF do Cliente a buscar: ");
        String cpf = sc.nextLine();
        
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente != null) {
            System.out.println("Cliente encontrado: " + cliente.toString());
        } else {
            System.out.println("Cliente com CPF " + cpf + " não encontrado.");
        }
    }

    private static void listarTodosClientes() throws Exception {
        System.out.println("\n-- Lista de Todos os Clientes --");
        ArrayList<Cliente> lista = clienteDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            for (Cliente c : lista) {
                System.out.println(c.toString());
            }
        }
    }

    private static void atualizarCliente() throws Exception {
        System.out.println("\n-- Atualização de Cliente --");
        System.out.print("CPF do Cliente a atualizar: ");
        String cpf = sc.nextLine();
        
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente != null) {
            System.out.println("Cliente atual: " + cliente.toString());

            System.out.print("Novo Nome (deixe em branco para manter '" + cliente.getNome() + "'): ");
            String novoNome = sc.nextLine();
            if (!novoNome.trim().isEmpty()) {
                cliente.setNome(novoNome);
            }

            System.out.print("Novos Emails (separados por vírgula, deixe em branco para manter): ");
            String novosEmailsStr = sc.nextLine();
            if (!novosEmailsStr.trim().isEmpty()) {
                String[] novosEmails = novosEmailsStr.split(",");
                for (int i = 0; i < novosEmails.length; i++) {
                    novosEmails[i] = novosEmails[i].trim();
                }
                cliente.setEmail(novosEmails);
            }

            System.out.print("Novo Telefone (deixe em branco para manter '" + cliente.getTelefone() + "'): ");
            String novoTelefone = sc.nextLine();
            if (!novoTelefone.trim().isEmpty()) {
                cliente.setTelefone(novoTelefone);
            }

            if (clienteDAO.update(cliente)) {
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Erro ao atualizar o cliente.");
            }
        } else {
            System.out.println("Cliente com CPF " + cpf + " não encontrado.");
        }
    }
    
    private static void excluirCliente() throws Exception {
        System.out.println("\n-- Exclusão de Cliente --");
        System.out.print("CPF do Cliente a excluir: ");
        String cpf = sc.nextLine();
        
        if (deletarClientePorCpf(cpf)) {
            System.out.println("Cliente com CPF " + cpf + " excluído com sucesso!");
        } else {
            System.out.println("Cliente com CPF " + cpf + " não encontrado.");
        }
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE CARROS
    // ======================================================================

    private static void gerenciarCarros() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Carros -----");
            System.out.println("1 - Incluir Carro (Create)");
            System.out.println("2 - Buscar Carro por ID (Read)");
            System.out.println("3 - Listar Todos os Carros (Read All)");
            System.out.println("4 - Atualizar Carro (Update)");
            System.out.println("5 - Excluir Carro (Delete)");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                subOpcao = Integer.parseInt(sc.nextLine());
                switch (subOpcao) {
                    case 1: incluirCarro(); break;
                    case 2: buscarCarro(); break;
                    case 3: listarTodosCarros(); break;
                    case 4: atualizarCarro(); break;
                    case 5: excluirCarro(); break;
                    case 0: System.out.println("Voltando..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                subOpcao = -1;
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
                e.printStackTrace();
            }
        } while (subOpcao != 0);
    }
    
    private static void incluirCarro() throws Exception {
        System.out.println("\n-- Inclusão de Novo Carro --");
        
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        
        System.out.print("Cores disponíveis (separadas por vírgula): ");
        String[] cores = sc.nextLine().split(",");
        for (int i = 0; i < cores.length; i++) {
            cores[i] = cores[i].trim();
        }

        LocalDate dataFab = lerData("Data de fabricação (ou deixe em branco para hoje)");
        if (dataFab == null) dataFab = LocalDate.now();
        
        float preco = 0f;
        System.out.print("Preço: R$ ");
        try { 
            preco = Float.parseFloat(sc.nextLine()); 
        } catch (NumberFormatException e) { 
            System.out.println("Preço inválido. Usando R$ 0.0."); 
        }
        
        Carro novoCarro = new Carro(0, modelo, cores, dataFab, preco);
        int novoId = carroDAO.create(novoCarro);
        System.out.println("Carro incluído com sucesso! ID: " + novoId);
    }

    private static void buscarCarro() throws Exception {
        System.out.println("\n-- Busca de Carro --");
        System.out.print("ID do Carro a buscar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Carro carro = carroDAO.read(id);
            if (carro != null) {
                System.out.println("Carro encontrado: " + carro.toString());
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private static void listarTodosCarros() throws Exception {
        System.out.println("\n-- Lista de Todos os Carros --");
        ArrayList<Carro> lista = carroDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhum carro cadastrado.");
        } else {
            for (Carro c : lista) {
                System.out.println(c.toString());
            }
        }
    }

    private static void atualizarCarro() throws Exception {
        System.out.println("\n-- Atualização de Carro --");
        System.out.print("ID do Carro a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Carro carro = carroDAO.read(id);
            if (carro != null) {
                System.out.println("Carro atual: " + carro.toString());

                System.out.print("Novo Modelo (deixe em branco para manter '" + carro.getModelo() + "'): ");
                String novoModelo = sc.nextLine();
                if (!novoModelo.trim().isEmpty()) {
                    carro.setModelo(novoModelo);
                }

                System.out.print("Novas Cores (separadas por vírgula, deixe em branco para manter): ");
                String novasCoresStr = sc.nextLine();
                if (!novasCoresStr.trim().isEmpty()) {
                    String[] novasCores = novasCoresStr.split(",");
                    for (int i = 0; i < novasCores.length; i++) {
                        novasCores[i] = novasCores[i].trim();
                    }
                    carro.setCores(novasCores);
                }
                
                LocalDate novaDataFab = lerData("Nova Data de fabricação (deixe em branco para manter)");
                if (novaDataFab != null) {
                    carro.setData_fabricacao(novaDataFab);
                }
                
                System.out.print("Novo Preço (deixe em branco para manter " + carro.getPreco() + "): R$ ");
                String novoPrecoStr = sc.nextLine();
                if (!novoPrecoStr.trim().isEmpty()) {
                    try {
                        float novoPreco = Float.parseFloat(novoPrecoStr);
                        carro.setPreco(novoPreco);
                    } catch (NumberFormatException e) {
                        System.out.println("Preço inválido. Mantendo o anterior.");
                    }
                }

                if (carroDAO.update(carro)) {
                    System.out.println("Carro atualizado com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar o carro.");
                }
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
    
    private static void excluirCarro() throws Exception {
        System.out.println("\n-- Exclusão de Carro --");
        System.out.print("ID do Carro a excluir: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (carroDAO.delete(id)) {
                System.out.println("Carro com ID " + id + " excluído com sucesso!");
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE VENDAS
    // ======================================================================

    private static void gerenciarVendas() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Vendas -----");
            System.out.println("1 - Registrar Nova Venda (Create)");
            System.out.println("2 - Buscar Venda por ID (Read)");
            System.out.println("3 - Listar Todas as Vendas (Read All)");
            System.out.println("4 - Atualizar Venda (Update)");
            System.out.println("5 - Excluir Venda (Delete)");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                subOpcao = Integer.parseInt(sc.nextLine());
                switch (subOpcao) {
                    case 1: registrarNovaVenda(); break;
                    case 2: buscarVenda(); break;
                    case 3: listarTodasVendas(); break;
                    case 4: atualizarVenda(); break;
                    case 5: excluirVenda(); break;
                    case 0: System.out.println("Voltando..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                subOpcao = -1;
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
                e.printStackTrace();
            }
        } while (subOpcao != 0);
    }

    private static void registrarNovaVenda() throws Exception {
        System.out.println("\n-- Registro de Nova Venda --");
        
        String cpfVendedor = "";
        String cpfCliente = "";
        float valorTotal = 0f;
        int[] idsCarros = new int[0];
        
        // 1. CPF Vendedor
        do {
            System.out.print("CPF do Vendedor: ");
            cpfVendedor = sc.nextLine();
            Vendedor vendedor = buscarVendedorPorCpf(cpfVendedor);
            if (vendedor == null) {
                System.out.println("Vendedor com CPF " + cpfVendedor + " não existe. Tente novamente.");
                cpfVendedor = "";
            }
        } while (cpfVendedor.isEmpty());
        
        // 2. CPF Cliente
        do {
            System.out.print("CPF do Cliente: ");
            cpfCliente = sc.nextLine();
            Cliente cliente = buscarClientePorCpf(cpfCliente);
            if (cliente == null) {
                System.out.println("Cliente com CPF " + cpfCliente + " não existe. Tente novamente.");
                cpfCliente = "";
            }
        } while (cpfCliente.isEmpty());

        // 3. IDs Carros e Cálculo do Valor Total
        System.out.print("IDs dos Carros (separados por vírgula e sem espaços, ex: 1,3,5): ");
        String idsStr = sc.nextLine();
        String[] idsArrayStr = idsStr.split(",");
        
        ArrayList<Integer> validIds = new ArrayList<>();
        valorTotal = 0f;

        for (String idS : idsArrayStr) {
            try {
                int id = Integer.parseInt(idS.trim());
                Carro carro = carroDAO.read(id);
                if (carro != null) {
                    validIds.add(id);
                    valorTotal += carro.getPreco();
                } else {
                    System.out.println("Carro com ID " + id + " não encontrado e será ignorado.");
                }
            } catch (NumberFormatException e) {
                // Ignora IDs inválidos
            }
        }
        
        if (validIds.isEmpty()) {
            System.out.println("Nenhum carro válido informado. Venda cancelada.");
            return;
        }
        
        idsCarros = validIds.stream().mapToInt(i->i).toArray();

        System.out.println("Valor total calculado: R$ " + valorTotal);

        // 4. Data da Venda
        LocalDate dataVenda = lerData("Data da Venda (ou deixe em branco para hoje)");
        if (dataVenda == null) dataVenda = LocalDate.now();
        
        // 5. Criação do Objeto
        Venda novaVenda = new Venda(0, cpfVendedor, cpfCliente, idsCarros, dataVenda, valorTotal);
        int novoId = vendaDAO.create(novaVenda);
        
        // Atualizar número de vendas e faturamento do vendedor
        Vendedor vendedorAtualizado = buscarVendedorPorCpf(cpfVendedor);
        if (vendedorAtualizado != null) {
            vendedorAtualizado.setNumero_vendas(vendedorAtualizado.getNumero_vendas() + 1);
            vendedorAtualizado.setFaturamento(vendedorAtualizado.getFaturamento() + valorTotal);
            vendedorDAO.update(vendedorAtualizado);
        }

        System.out.println("Venda registrada com sucesso! ID: " + novoId + ". Valor Total: R$ " + valorTotal);
    }

    private static void buscarVenda() throws Exception {
        System.out.println("\n-- Busca de Venda --");
        System.out.print("ID da Venda a buscar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Venda venda = vendaDAO.read(id);
            if (venda != null) {
                System.out.println("Venda encontrada: " + venda.toString());
            } else {
                System.out.println("Venda com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private static void listarTodasVendas() throws Exception {
        System.out.println("\n-- Lista de Todas as Vendas --");
        ArrayList<Venda> lista = vendaDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma venda cadastrada.");
        } else {
            for (Venda v : lista) {
                System.out.println(v.toString());
            }
        }
    }

    private static void atualizarVenda() throws Exception {
        System.out.println("\n-- Atualização de Venda --");
        System.out.print("ID da Venda a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Venda venda = vendaDAO.read(id);
            if (venda != null) {
                System.out.println("Venda atual: " + venda.toString());
                
                System.out.print("Novo CPF Vendedor (deixe em branco para manter " + venda.getCpfVendedor() + "): ");
                String novoCpfVendedor = sc.nextLine();
                if (!novoCpfVendedor.trim().isEmpty()) {
                    venda.setCpfVendedor(novoCpfVendedor);
                }
                
                System.out.print("Novo CPF Cliente (deixe em branco para manter " + venda.getCpfCliente() + "): ");
                String novoCpfCliente = sc.nextLine();
                if (!novoCpfCliente.trim().isEmpty()) {
                    venda.setCpfCliente(novoCpfCliente);
                }
                
                System.out.print("Novos IDs Carros (separados por vírgula, deixe em branco para manter " + Arrays.toString(venda.getIdsCarros()) + "): ");
                String novosIdsStr = sc.nextLine();
                if (!novosIdsStr.trim().isEmpty()) {
                    try {
                        String[] idsArrayStr = novosIdsStr.split(",");
                        int[] novosIds = Arrays.stream(idsArrayStr).map(String::trim).mapToInt(Integer::parseInt).toArray();
                        venda.setIdsCarros(novosIds);
                    } catch (NumberFormatException e) {
                         System.out.println("Algum ID de carro inválido. Mantendo o anterior.");
                    }
                }
                
                LocalDate novaDataVenda = lerData("Nova Data de Venda (deixe em branco para manter)");
                if (novaDataVenda != null) {
                    venda.setData_venda(novaDataVenda);
                }

                if (vendaDAO.update(venda)) {
                    System.out.println("Venda atualizada com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar a venda.");
                }
            } else {
                System.out.println("Venda com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private static void excluirVenda() throws Exception {
        System.out.println("\n-- Exclusão de Venda --");
        System.out.print("ID da Venda a excluir: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (vendaDAO.delete(id)) {
                System.out.println("Venda com ID " + id + " excluída com sucesso!");
            } else {
                System.out.println("Venda com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
}