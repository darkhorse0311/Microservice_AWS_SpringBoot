## 💻 Curso: Criando microsserviços em Java com AWS ECS Fargate e AWS CDK

- Código do curso "Criando microsserviços em Java com AWS ECS Fargate e AWS CDK" da Udemy

## :books: Conteúdos
- AWS CloudFormation
- AWS CDK
- VPC
- Serviços utilizados da AWS: ECS, SNS, SQS, RDS, DynamoDB e S3 onde são integrados em dois serviços Spring.
- Criado o projeto de infra, onde é definida toda a infraestrutura da AWS e a integração entre os serviços.
- Serviços:
    - Serviço 01:
      - Criado um CRUD de produtos, onde os mesmos são salvos no RDS.
      - Publica eventos de CREATED, UPDATED e DELETED no em um tópico do SNS.
      - Criado um endpoint para solicitar URLs de upload, que fica disponível por um tempo pré definido. 
      - Com a URL disponibilizada, é possível fazer o upload de um arquivo de notas fiscais para um bucket no S3.
      - Quando o upload do arquivo é finalizado no bucket, é publicado um evento no SNS.
      - Inscrito no SNS, uma fila do SQS onde serão publicados os eventos dos arquivos. Definido também a dead letter queue desta fila.
      - Criado um listener para consumir a fila do SQS, onde o arquivo da nota fiscal é buscado no bucket e salvo no banco RDS.
      - Criado um endpoint para listar as notas fiscais importadas.
      - Inscrito no também no SNS, um e-mail para receber os eventos do CRUD de produtos.
      - Definido um auto scaling para esse serviço de no máximo 4 instâncias.
      - Definido o aplication load balancer para este serviço.
    - Serviço 02:
      - Ouve os eventos de CREATED, UPDATED e DELETED dos produtos persistidos no serviço 01.
      - Os eventos são consumidos através de uma fila do SQS que foi inscrita no tópico do SNS. Definido também a dead letter queue desta fila.
      - Persiste no DynamoDB os eventos recebidos.
      - Criado um endpoint para listar os eventos dos produtos persistidos.
      - Definido um auto scaling da tabela do DynamoDB e analisar as suas métricas.
      - Definido um auto scaling para esse serviço de no máximo 4 instâncias.
      - Definido o aplication load balancer para este serviço.    
 - Realizados testes de carga para analisar a performance do DynamoDB.

