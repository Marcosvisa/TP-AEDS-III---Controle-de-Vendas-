# 🚗 Sistema de Gerenciamento de Concessionária

Sistema para gerenciamento de concessionária com persistência em arquivos binários, hashing extensível e arvore B+. Ainda sem interface gráfica implementada.

## 📋 Funcionalidades
- **CRUD** de Vendedores, Clientes, Carros e Vendas
- **Hashing Extensível** para buscas rápidas
- **Arvore B+** para buscas rápidas 
- **Persistência** em arquivos binários

## 🏗️ Estrutura do Projeto

```
projeto/
├── app/           # Aplicação principal
├── model/         # Entidades (Cliente, Vendedor, Carro, Venda)
├── dao/           # Camada de acesso a dados
├── util/          # Utilitários
└── dados/         # Arquivos de dados (gerados automaticamente)
```

## 🚀 Compilação e Execução

### Compilar o projeto:
```bash
javac -cp . util/*.java model/*.java dao/*.java app/Main.java
```

### Executar o sistema:
```bash
java -cp . app.Main
```
