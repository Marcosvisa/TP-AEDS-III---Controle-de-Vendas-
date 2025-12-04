# 🚗 Sistema de Gerenciamento de Vendas de Concessionária

Sistema para gerenciamento de vendas de uma concessionária, disponível em duas versões:

* ✅ **Versão com Interface Gráfica**
* ✅ **Versão em Modo Console**

O sistema utiliza estruturas de dados avançadas para otimizar performance, segurança e armazenamento.

---

## 📋 Funcionalidades

* ✅ **CRUD** de:

  * Vendedores
  * Clientes
  * Carros
  * Vendas
* ✅ **Hashing Extensível** para buscas rápidas
* ✅ **Árvore B+** para indexação eficiente
* ✅ **Persistência de dados** em arquivos binários
* ✅ **Compressão de dados** com **Huffman** e **LZW**
* ✅ **Criptografia** para proteção de informações sensíveis
* ✅ **Casamento de Padrões** para busca por entradas textuais

---

## 💻 Requisitos

* Java JDK 8 ou superior
* Terminal (Prompt de Comando, PowerShell ou Linux)
* IDE opcional:

  * NetBeans
  * VS Code
  * IntelliJ IDEA

---

# 🖥️ VERSÃO COM INTERFACE GRÁFICA

## 🏗️ Estrutura do Projeto

```
projeto/
├── src/
│   ├── app/           (Main e controladores)
│   ├── dao/           (Acesso a dados)
│   ├── model/         (Modelos)
│   ├── util/          (Utilitários)
│   └── view/          (Interfaces gráficas)
├── insumos/           (Imagens e recursos)
└── dados/             (Arquivos .db)
```

---

## 🚀 Compilação e Execução

### 📌 Compilar o projeto:

```bash
javac -cp . util/*.java model/*.java dao/*.java view/*.java app/Main.java
```

### ▶️ Executar o sistema:

```bash
java -cp . app.Main
```

---

# 🖥️ VERSÃO SEM INTERFACE (CONSOLE)

## 🏗️ Estrutura do Projeto

```
projeto/
├── app/           (Aplicação principal)
├── model/         (Cliente, Vendedor, Carro, Venda)
├── dao/           (Camada de acesso a dados)
├── util/          (Utilitários)
└── dados/         (Arquivos de dados)
```

---

## 🚀 Compilação e Execução

### 📌 Compilar o projeto:

```bash
javac -cp . util/*.java model/*.java dao/*.java app/Main.java
```

### ▶️ Executar o sistema:

```bash
java -cp . app.Main
```

---

## 📌 Observações

* Os arquivos de dados são gerados automaticamente na pasta `dados/`.
* O sistema foi desenvolvido com fins **acadêmicos**, aplicando conceitos de:

  * Estruturas de Dados
  * Arquivos
  * Algoritmos de Compressão
  * Criptografia
  * Organização de Sistemas

---
