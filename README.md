# Sistema de Contas a Pagar - Desafio Backend

Este projeto é uma API REST para gerenciar contas a pagar. Ele foi desenvolvido usando Java 17 com Spring Boot e orquestrado com Docker e Docker Compose.

## Requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em seu ambiente:

- Docker
- Docker Compose

## Configuração e Execução

### Passo 1: Clonar o repositório

Clone este repositório em seu ambiente local:

```bash
git clone git@github.com:Alberto-Monteiro/pagafacil.git
cd pagafacil
```

### Passo 2: Build da aplicação

Construa a aplicação usando Docker:

```bash
docker-compose -f .\src\docker\docker-compose.yaml up --build
```

Este comando irá:

- Construir a imagem da aplicação Java a partir do Dockerfile.
- Inicializar um container PostgreSQL com os detalhes de banco de dados especificados no arquivo `docker-compose.yaml`.

### Passo 3: Configurar o Banco de Dados

As variáveis de ambiente do banco de dados são configuradas automaticamente no Docker Compose:

- Banco de dados: `contasdb`
- Usuário: `postgres`
- Senha: `password`
- URL: `jdbc:postgresql://db:5432/contasdb`

A aplicação usará o **Flyway** para gerenciar as migrações do banco de dados, configurando automaticamente as tabelas ao iniciar.

### Passo 4: Executar a aplicação

Com a aplicação e o banco de dados configurados, você pode acessá-la através da porta `8080`. O sistema estará disponível na URL:

```
http://localhost:8080
```

## Endpoints

### 1. Criar uma Conta a Pagar

**POST /contas/cadastrar**

```bash
curl --location 'http://localhost:8080/contas/cadastrar' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0' \
--data '{
  "dataVencimento": "2024-10-06",
  "valor": 1500.75,
  "descricao": "Pagamento da fatura de eletricidade 10"
}'
```

### 2. Atualizar uma Conta

**PUT /contas/atualizar/:id**

```bash
curl --location --request PUT 'http://localhost:8080/contas/atualizar/2' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0' \
--data '{
  "dataVencimento": "2024-09-25",
  "valor": 1700.75,
  "descricao": "Pagamento da fatura de eletricidade 2"
}'
```

### 3. Alterar Situação da Conta

**PATCH /contas/alterar-situacao/:id?situacao=PAGO**

```bash
curl --location --request PATCH 'http://localhost:8080/contas/alterar-situacao/2?situacao=PAGO' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0'
```

### 4. Listar Contas A Pagar

**GET /contas/buscar-contas-a-pagar?dataVencimento=2024-10-06&sort=dataVencimento,asc&page=0&size=10**

```bash
curl --location 'http://localhost:8080/contas/buscar-contas-a-pagar?dataVencimento=2024-10-06&sort=dataVencimento%2Casc&page=0&size=10' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0'
```

### 5. Filtrar Conta por ID

**GET /contas/buscar/:id**

```bash
curl --location 'http://localhost:8080/contas/buscar/2' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0'
```

### 6. Obter Valor Total Pago por Período

**GET /contas/valor-total-pago?dataInicio=2024-09-01&dataFim=2024-09-30**

```bash
curl --location 'http://localhost:8080/contas/valor-total-pago?dataInicio=2024-09-01&dataFim=2024-09-30' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0'
```

### 7. Importar Contas via CSV

**POST /contas/importar-csv**

Envie um arquivo CSV com a lista de contas a serem importadas.

```bash
curl --location 'http://localhost:8080/contas/importar-csv' \
--header 'Authorization: Basic YWRtaW46c2VjcmV0' \
--form 'arquivo=@"/pagafacil/src/main/resources/contas.csv"'
```

## Autenticação

A API está protegida por autenticação básica. Para acessar os endpoints, você precisará fornecer um nome de usuário e senha. As credenciais padrão são:

- **Usuário**: `admin`
- **Senha**: `secret`

### Exemplo de Requisição Autenticada

```bash
curl -u admin:password -X GET http://localhost:8080/contas
```

## Segurança

A aplicação utiliza autenticação básica configurada no Spring Security. O arquivo `SecurityConfig.java` define as regras de acesso aos endpoints.

## Tecnologias Utilizadas

- **Java 17**: Linguagem de programação principal.
- **Spring Boot**: Framework para desenvolvimento rápido de aplicações Java.
- **PostgreSQL**: Banco de dados relacional.
- **Docker**: Orquestração de containers.
- **Flyway**: Gerenciamento de migrações do banco de dados.
- **JPA/Hibernate**: Persistência de dados.

## Como Contribuir

1. Faça um fork do projeto
2. Crie uma branch com sua feature: `git checkout -b minha-feature`
3. Faça commit de suas mudanças: `git commit -m 'Adiciona nova feature'`
4. Faça push para sua branch: `git push origin minha-feature`
5. Crie um novo Pull Request

## Licença

Este projeto está licenciado sob os termos da licença MIT.

---

Esse README cobre as instruções básicas para configurar, rodar e acessar a aplicação, bem como os detalhes de segurança e autenticação envolvidos.
