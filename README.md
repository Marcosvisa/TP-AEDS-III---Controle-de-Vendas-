# 🚗 Sistema de Gerenciamento de Vendas de Concessionária

Sistema para gerenciamento de vendas de uma concessionára.

O sistema utiliza estruturas de dados avançadas para otimizar performance, segurança e armazenamento.

---

## 📋 Funcionalidades

* ✅ **CRUD** de Vendedores, Clientes, Carros, Vendas.
* ✅ **Hashing Extensível** para buscas rápidas
* ✅ **Árvore B+** para buscas rápidas
* ✅ **Persistência de dados** em arquivos binários
* ✅ **Compressão de dados** com Huffman e LZW
* ✅ **Criptografia** para proteção de informações sensíveis
* ✅ **Casamento de Padrões** para busca por entradas textuais
* ✅ **Interface Gráfica** com JavaSwing

---

## 💻 Requisitos

* Java JDK 8 ou superior
* Terminal (Prompt de Comando, PowerShell ou Linux)
* IDE opcional:

  * NetBeans
  * VS Code
  * IntelliJ IDEA

---

## 🏗️ Estrutura do Projeto

```
GERENCIADOR_VENDAS_CONSSESSIONARIA/
├── src/
│   ├── app/           (Main e controladores)
│   ├── dao/           (Acesso a dados)
│   ├── model/         (Modelos)
│   ├── util/          (Utilitários)
│   └── view/          (Interfaces gráficas)
├── insumos/           (Imagens e recursos)
├── backup/            (Criado quando é realizado uma copressao)
└── dados/             (Arquivos .db)
```

---

## 🚀 Compilação e Execução

### 📌 Compilar o projeto (Windows PowerShell):

```powershell
javac -d target/classes (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })
```

### ▶️ Executar o sistema:

```powershell
java -cp target/classes app.Main
```


## 📌 Observações

* Os arquivos de dados são gerados automaticamente na pasta `dados/`.
* O sistema foi desenvolvido com fins **acadêmicos**, aplicando conceitos de:

  * Estruturas de Dados
  * Arquivos
  * Algoritmos de Compressão
  * Criptografia
  * Organização de Sistemas

---
