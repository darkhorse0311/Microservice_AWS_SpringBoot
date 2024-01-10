## 💻 Curso: Criando microsserviços em Java com AWS ECS Fargate e AWS CDK

- Código do curso "Criando microsserviços em Java com AWS ECS Fargate e AWS CDK" da Udemy

## :books: Conteúdos
- AWS CloudFormation
- AWS CDK
- VPC
- Serviços utilizados da AWS: ECS, SNS, SQS, RDS, DynamoDB e S3 onde são integrados em dois serviços Spring.
- Criado o projeto de infra, onde são definidas todas a stacks da AWS e suas integrações.
- Serviços:
    - Serviço 01:
      - Criado um CRUD de produtos, onde os mesmos são salvos no RDS.
      - Publica eventos de CREATED, UPDATED e DELETED em um tópico do SNS.
      - Inscrito neste tópico do SNS, um e-mail para receber os eventos de CRUD de produtos.
      - Criado um endpoint para solicitar URLs de upload, que fica disponível por um tempo pré definido. 
      - Com a URL disponibilizada, é possível fazer o upload de um arquivo de notas fiscais para um bucket no S3.
      - Quando o upload do arquivo é finalizado, é publicado um evento em outro tópico do SNS.
      - Inscrito neste outro tópico do SNS, uma fila do SQS onde serão publicados os eventos. Definido também a dead letter queue desta fila.
      - Criado um listener para consumir esta fila do SQS, onde o arquivo da nota fiscal é buscado no bucket do S3 e salvo algumas das informações do arquivo no banco RDS.
      - Criado um endpoint para listar as notas fiscais importadas.
    - Serviço 02:
      - Ouve os eventos de CREATED, UPDATED e DELETED dos produtos persistidos no serviço 01.
      - Os eventos são consumidos através de uma outra fila do SQS que também foi inscrita no tópico do SNS. Definido também a dead letter queue desta fila.
      - Persiste os eventos no DynamoDB.
      - Criado um endpoint para listar os eventos dos produtos persistidos.
      - Definido um auto scaling da tabela do DynamoDB e analisado as suas métricas.
    
    - Realizados testes de carga para analisar a performance do DynamoDB.
    - Definido um auto scaling para os ambos os serviços de no máximo 4 instâncias, baseado no uso da CPU do container, bem como o seu load balancer.  
      
☑️ Curso feito com o objetivo conhecer algumas stacks da AWS e itegrá-los com o Spring.
