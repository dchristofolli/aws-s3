#### AWS-S3 + Spring Webflux

Sistema para teste de integração com a S3 usando Spring WebFlux.

A ideia é que seja possível fazer uploads, downloads e verificações em um bucket na S3.

##### Dependências
Java 11, Spring boot(WebFlux), Gradle e OpenCSV

##### Ambiente
Para testar esse sistema, é necessário que o arquivo rootkey.csv esteja na raiz do projeto

Exemplo de rootkey.csv:<br>
AWSAccessKeyId;AWSSecretKey<br>
****************;*****************<br>


##### Endpoint
POST localhost:8080/files/upload

A requisição deve ser feita pelo Postman(ou outra coisa parecida). <br>
Body-type: form-data<br>
Key: "file", marcar a opção File<br>
Value: Selecionar o arquivo que será upado para o S3.
 
