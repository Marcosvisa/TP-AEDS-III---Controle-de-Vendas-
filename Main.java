import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays; // Para imprimir arrays de IDs de autopeças

public class Main {
    private static IndiceVendedorCarros indiceVendedorCarros;
    private static Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // DAOs (Assumindo que estão no pacote padrão para evitar erros de importação)
    private static VendedorDAO vendedorDAO;
    private static CarroDAO carroDAO;
    private static AutopecasDAO autopecasDAO;
    private static VendaAutopecasDAO vendaAutopecasDAO;


    public static void main(String[] args) {
        
        try {
            // Inicializa todos os DAOs. Isso garante que os arquivos de dados estejam prontos.
            vendedorDAO = new VendedorDAO();
            carroDAO = new CarroDAO();
            autopecasDAO = new AutopecasDAO();
            vendaAutopecasDAO = new VendaAutopecasDAO();
            indiceVendedorCarros = new IndiceVendedorCarros();
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
                            gerenciarCarros();
                            break;
                        case 3:
                            gerenciarAutopecas();
                            break;
                        case 4:
                            gerenciarVendasAutopecas();
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
            
            // Fecha os arquivos ao sair.
            vendedorDAO.close();
            carroDAO.close();
            autopecasDAO.close();
            vendaAutopecasDAO.close();

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
        System.out.println("2 - Gerenciar Carros");
        System.out.println("3 - Gerenciar Autopeças");
        System.out.println("4 - Gerenciar Vendas de Autopeças");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }
    
    /**
     * Tenta ler um LocalDate do console.
     * @param prompt A mensagem a ser exibida.
     * @return O LocalDate lido ou null em caso de erro.
     */
    private static LocalDate lerData(String prompt) {
        LocalDate data = null;
        boolean valido = false;
        do {
            System.out.print(prompt + " (formato dd/MM/yyyy): ");
            String dataStr = sc.nextLine();
            if (dataStr.trim().isEmpty()) return null; // Permite pular
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
    // MÉTODOS DE GERENCIAMENTO DE VENDEDORES (Já estavam implementados)
    // ======================================================================

    private static void gerenciarVendedores() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Vendedores -----");
            System.out.println("1 - Incluir Vendedor (Create)");
            System.out.println("2 - Buscar Vendedor por ID (Read)");
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
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        
        System.out.print("Emails (separados por vírgula): ");
        String[] emails = sc.nextLine().split(",");
        for (int i = 0; i < emails.length; i++) {
            emails[i] = emails[i].trim();
        }
        
        Vendedor novoVendedor = new Vendedor(0, nome, emails, LocalDate.now(), 0, 0f);
        int novoId = vendedorDAO.create(novoVendedor);
        System.out.println("Vendedor incluído com sucesso! ID: " + novoId);
    }

    private static void buscarVendedor() throws Exception {
        System.out.println("\n-- Busca de Vendedor --");
        System.out.print("ID do Vendedor a buscar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Vendedor vendedor = vendedorDAO.read(id);
            if (vendedor != null) {
                System.out.println("Vendedor encontrado: " + vendedor.toString());
            } else {
                System.out.println("Vendedor com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
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
        System.out.print("ID do Vendedor a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Vendedor vendedor = vendedorDAO.read(id);
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
                
                System.out.print("Novo Faturamento (deixe em branco para manter " + vendedor.getFaturamento() + "): ");
                String novoFaturamentoStr = sc.nextLine();
                if (!novoFaturamentoStr.trim().isEmpty()) {
                    try {
                        float novoFaturamento = Float.parseFloat(novoFaturamentoStr);
                        vendedor.setFaturamento(novoFaturamento);
                    } catch (NumberFormatException e) {
                        System.out.println("Faturamento inválido. Mantendo o anterior.");
                    }
                }

                if (vendedorDAO.update(vendedor)) {
                    System.out.println("Vendedor atualizado com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar o vendedor.");
                }
            } else {
                System.out.println("Vendedor com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
    
    private static void excluirVendedor() throws Exception {
        System.out.println("\n-- Exclusão de Vendedor --");
        System.out.print("ID do Vendedor a excluir: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (vendedorDAO.delete(id)) {
                System.out.println("Vendedor com ID " + id + " excluído (marcado como inativo) com sucesso!");
            } else {
                System.out.println("Vendedor com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
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
    
    // --- 1. Leitura dos dados do Carro ---
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
    
    // --- 2. Leitura da Chave Estrangeira (ID Vendedor) ---
    int idVendedor = 0;
    System.out.print("ID do Vendedor responsável pela venda: ");
    try { 
        idVendedor = Integer.parseInt(sc.nextLine()); 
    } catch (NumberFormatException e) { 
        System.out.println("ID Vendedor inválido. Usando ID 0."); 
    }
    
    // --- 3. Criação do Objeto ---
    // NOVO CONSTRUTOR: Carro(id, modelo, cores, data_fabricacao, preco, id_vendedor)
    Carro novoCarro = new Carro(0, modelo, cores, dataFab, preco, idVendedor);
    
    // --- 4. Persistência e Obtenção do Offset ---
    // Requer que CarroDAO utilize o método createWithOffset() da classe Arquivo<T>
    long offsetCarro = carroDAO.createWithOffset(novoCarro); 
    int novoId = novoCarro.getId(); // ID gerado pelo DAO

    // --- 5. Indexação no Hash Extensível (Índice Secundário) ---
    // A chave é o idVendedor, e o valor é o offset do Carro no arquivo de dados
    if (indiceVendedorCarros.create(idVendedor, offsetCarro)) {
         System.out.println("Vendedor ID " + idVendedor + " indexado com sucesso no Hash Extensível (Primeiro Carro).");
    } else {
         System.out.println("Aviso: Vendedor ID " + idVendedor + " já possuía uma entrada no índice Hash.");
         System.out.println("O carro foi salvo, mas a busca rápida pelo Hash continuará retornando o primeiro registro indexado.");
    }
    
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
                System.out.println("Carro com ID " + id + " excluído (marcado como inativo) com sucesso!");
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE AUTOPEÇAS
    // ======================================================================

    private static void gerenciarAutopecas() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Autopeças -----");
            System.out.println("1 - Incluir Autopeça (Create)");
            System.out.println("2 - Buscar Autopeça por ID (Read)");
            System.out.println("3 - Listar Todas as Autopeças (Read All)");
            System.out.println("4 - Atualizar Autopeça (Update)");
            System.out.println("5 - Excluir Autopeça (Delete)");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                subOpcao = Integer.parseInt(sc.nextLine());
                switch (subOpcao) {
                    case 1: incluirAutopeca(); break;
                    case 2: buscarAutopeca(); break;
                    case 3: listarTodasAutopecas(); break;
                    case 4: atualizarAutopeca(); break;
                    case 5: excluirAutopeca(); break;
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

    private static void incluirAutopeca() throws Exception {
        System.out.println("\n-- Inclusão de Nova Autopeça --");
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();
        
        System.out.print("Tipo: ");
        String tipo = sc.nextLine();
        
        LocalDate dataFab = lerData("Data de fabricação (ou deixe em branco para hoje)");
        if (dataFab == null) dataFab = LocalDate.now();
        
        float preco = 0f;
        System.out.print("Preço: R$ ");
        try { preco = Float.parseFloat(sc.nextLine()); } 
        catch (NumberFormatException e) { System.out.println("Preço inválido. Usando R$ 0.0."); }
        
        Autopecas novaPeca = new Autopecas(0, descricao, tipo, dataFab, preco);
        int novoId = autopecasDAO.create(novaPeca);
        System.out.println("Autopeça incluída com sucesso! ID: " + novoId);
    }

    private static void buscarAutopeca() throws Exception {
        System.out.println("\n-- Busca de Autopeça --");
        System.out.print("ID da Autopeça a buscar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Autopecas peca = autopecasDAO.read(id);
            if (peca != null) {
                System.out.println("Autopeça encontrada: " + peca.toString());
            } else {
                System.out.println("Autopeça com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private static void listarTodasAutopecas() throws Exception {
        System.out.println("\n-- Lista de Todas as Autopeças --");
        ArrayList<Autopecas> lista = autopecasDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma autopeça cadastrada.");
        } else {
            for (Autopecas a : lista) {
                System.out.println(a.toString());
            }
        }
    }

    private static void atualizarAutopeca() throws Exception {
        System.out.println("\n-- Atualização de Autopeça --");
        System.out.print("ID da Autopeça a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Autopecas peca = autopecasDAO.read(id);
            if (peca != null) {
                System.out.println("Autopeça atual: " + peca.toString());

                System.out.print("Nova Descrição (deixe em branco para manter '" + peca.getDescricao() + "'): ");
                String novaDescricao = sc.nextLine();
                if (!novaDescricao.trim().isEmpty()) {
                    peca.setDescricao(novaDescricao);
                }

                System.out.print("Novo Tipo (deixe em branco para manter '" + peca.getTipo() + "'): ");
                String novoTipo = sc.nextLine();
                if (!novoTipo.trim().isEmpty()) {
                    peca.setTipo(novoTipo);
                }
                
                LocalDate novaDataFab = lerData("Nova Data de fabricação (deixe em branco para manter)");
                if (novaDataFab != null) {
                    peca.setData_fabricacao(novaDataFab);
                }
                
                System.out.print("Novo Preço (deixe em branco para manter " + peca.getPreco() + "): R$ ");
                String novoPrecoStr = sc.nextLine();
                if (!novoPrecoStr.trim().isEmpty()) {
                    try {
                        float novoPreco = Float.parseFloat(novoPrecoStr);
                        peca.setPreco(novoPreco);
                    } catch (NumberFormatException e) {
                        System.out.println("Preço inválido. Mantendo o anterior.");
                    }
                }

                if (autopecasDAO.update(peca)) {
                    System.out.println("Autopeça atualizada com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar a autopeça.");
                }
            } else {
                System.out.println("Autopeça com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
    
    private static void excluirAutopeca() throws Exception {
        System.out.println("\n-- Exclusão de Autopeça --");
        System.out.print("ID da Autopeça a excluir: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (autopecasDAO.delete(id)) {
                System.out.println("Autopeça com ID " + id + " excluída (marcada como inativa) com sucesso!");
            } else {
                System.out.println("Autopeça com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    // ======================================================================
    // MÉTODOS DE GERENCIAMENTO DE VENDAS DE AUTOPEÇAS
    // ======================================================================

    private static void gerenciarVendasAutopecas() throws Exception {
        int subOpcao = -1;
        do {
            System.out.println("\n----- Gerenciar Vendas de Autopeças -----");
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
        
        int idVendedor = 0;
        int idCarro = 0;
        float valorTotal = 0f;
        int[] idsAutopecas = new int[0];
        
        // 1. ID Vendedor
        do {
            System.out.print("ID do Vendedor: ");
            try {
                idVendedor = Integer.parseInt(sc.nextLine());
                if (vendedorDAO.read(idVendedor) == null) {
                    System.out.println("Vendedor com ID " + idVendedor + " não existe. Tente novamente.");
                    idVendedor = 0;
                }
            } catch (NumberFormatException e) { 
                System.out.println("ID inválido.");
                idVendedor = 0;
            }
        } while (idVendedor == 0);
        
        // 2. ID Carro
        do {
            System.out.print("ID do Carro: ");
            try {
                idCarro = Integer.parseInt(sc.nextLine());
                if (carroDAO.read(idCarro) == null) {
                    System.out.println("Carro com ID " + idCarro + " não existe. Tente novamente.");
                    idCarro = 0;
                }
            } catch (NumberFormatException e) { 
                System.out.println("ID inválido.");
                idCarro = 0;
            }
        } while (idCarro == 0);

        // 3. IDs Autopeças e Cálculo do Valor Total
        System.out.print("IDs das Autopeças (separados por vírgula e sem espaços, ex: 1,3,5): ");
        String idsStr = sc.nextLine();
        String[] idsArrayStr = idsStr.split(",");
        
        ArrayList<Integer> validIds = new ArrayList<>();
        valorTotal = 0f;

        for (String idS : idsArrayStr) {
            try {
                int id = Integer.parseInt(idS.trim());
                Autopecas peca = autopecasDAO.read(id);
                if (peca != null) {
                    validIds.add(id);
                    valorTotal += peca.getPreco();
                } else {
                    System.out.println("Autopeça com ID " + id + " não encontrada e será ignorada.");
                }
            } catch (NumberFormatException e) {
                // Ignora IDs inválidos
            }
        }
        
        // Converte a lista de IDs válidos para o array int[] da classe Venda_Autopecas
        idsAutopecas = validIds.stream().mapToInt(i->i).toArray();

        // Adiciona o preço do carro ao valor total
        Carro carroVendido = carroDAO.read(idCarro);
        if(carroVendido != null) {
            valorTotal += carroVendido.getPreco();
        }

        System.out.println("Valor total calculado (Carro + Peças): R$ " + valorTotal);

        // 4. Data da Venda
        LocalDate dataVenda = lerData("Data da Venda (ou deixe em branco para hoje)");
        if (dataVenda == null) dataVenda = LocalDate.now();
        
        // 5. Criação do Objeto
        Venda_Autopecas novaVenda = new Venda_Autopecas(0, idVendedor, idsAutopecas, dataVenda, valorTotal);
        int novoId = vendaAutopecasDAO.create(novaVenda);
        
        // Opcional: Atualizar número de vendas do vendedor
        Vendedor vendedorAtualizado = vendedorDAO.read(idVendedor);
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
            Venda_Autopecas venda = vendaAutopecasDAO.read(id);
            if (venda != null) {
                System.out.println("Venda encontrada: " + venda.toString());
            } else {
                System.out.println("Venda com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private static void listarTodasVendas() throws Exception {
        System.out.println("\n-- Lista de Todas as Vendas --");
        ArrayList<Venda_Autopecas> lista = vendaAutopecasDAO.readAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma venda cadastrada.");
        } else {
            for (Venda_Autopecas v : lista) {
                System.out.println(v.toString());
            }
        }
    }

    private static void atualizarVenda() throws Exception {
        System.out.println("\n-- Atualização de Venda --");
        System.out.print("ID da Venda a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Venda_Autopecas venda = vendaAutopecasDAO.read(id);
            if (venda != null) {
                System.out.println("Venda atual: " + venda.toString());

                // NOTA: Para simplificar, não faremos o recálculo complexo do valor total e a 
                // revalidação dos IDs de vendedor/carro/peças. Apenas a data e IDs são alterados.
                
                System.out.print("Novo ID Vendedor (deixe em branco para manter " + venda.getId_vendedor() + "): ");
                String novoIdVendedorStr = sc.nextLine();
                if (!novoIdVendedorStr.trim().isEmpty()) {
                    try { venda.setId_vendedor(Integer.parseInt(novoIdVendedorStr)); } 
                    catch (NumberFormatException e) { System.out.println("ID Vendedor inválido. Mantendo o anterior."); }
                }
                
                System.out.print("Novos IDs Autopeças (separados por vírgula, deixe em branco para manter " + Arrays.toString(venda.getIds_autopecas()) + "): ");
                String novosIdsStr = sc.nextLine();
                if (!novosIdsStr.trim().isEmpty()) {
                    try {
                        String[] idsArrayStr = novosIdsStr.split(",");
                        int[] novosIds = Arrays.stream(idsArrayStr).map(String::trim).mapToInt(Integer::parseInt).toArray();
                        venda.setIds_autopecas(novosIds);
                    } catch (NumberFormatException e) {
                         System.out.println("Algum ID de autopeça inválido. Mantendo o anterior.");
                    }
                }
                
                LocalDate novaDataVenda = lerData("Nova Data de Venda (deixe em branco para manter)");
                if (novaDataVenda != null) {
                    venda.setData_venda(novaDataVenda);
                }

                // O valor total pode ser atualizado manualmente aqui, se necessário.
                
                if (vendaAutopecasDAO.update(venda)) {
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
            if (vendaAutopecasDAO.delete(id)) {
                System.out.println("Venda com ID " + id + " excluída (marcada como inativa) com sucesso!");
            } else {
                System.out.println("Venda com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
}