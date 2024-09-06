# Projeto Ecommerce

Esse projeto, implementado em Java, foi desenvolvido durante estágio na Compass UOL em desafio proposto.

## Como subir o banco de dados

```sh
docker build -t ecommerce:latest .

docker run -d --rm --name ecommercedb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_DB=ecommerce -e POSTGRES_PASSWORD=1234 ecommerce:latest
```
