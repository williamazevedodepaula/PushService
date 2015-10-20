<h1>Push Service</h1>

Este Web Service permite enviar uma mensagem a uma lista de destinatários Android via push, utilizando a api GCM

<h3>Requisição</h3>

Uma vez hospedado, utilizar o serviço a partir da URL:

http://host:port/PushService/push/push/send/apiKey/idList/title/msg

onde:

<b>host</b> - endereço do host em que o serviço está hospedado

<b>port</b> - porta de acesso ao servidor

<b>apiKey</b> - GCM apiKey, utilizada para identificar o serviço no GCM (Google Cloud Message).

<b>regIds</b> - String contendo uma lista de ids dos destinatários formatada como um array JSON. Cada id é o identificador de registro fornecido pela GCM a cada aparelho ao se registrar junto à GCM. Cada regId identifica um aparelho e é utilizado para localizar o destinatário e entregar a mensagem. Por exemplo, para enviar os ids "ddddddddddd", "eeeeeeeeeeeee" e "fffffffffff", deve-se enviar a string:

["ddddddddddd","eeeeeeeeeeeee","fffffffffff"]

 Caso deseja-se enviar a mensagem para um único id, este parâmetro pode conter apenas o id desejado, sem necessidade de estar formatado como json array. Por exemplo, para enviar para o id "ggggggggggggg", pode-se enviar apenas a string:
 
ggggggggggggg

<b>title</b> - O título da mensagem a ser enviada. Aparece no aparelho como titulo da notificação.

<b>msg</b> - A mensagem a ser enviada.


<h3>Retorno</h3>
    
Retorna um objeto SenderResponse, formatado como JSON, contendo o resultado do envio da mensagem.
Esse objeto possui os seguintes campos:

<b>Status</b> - indicará "FAIL" caso haja algum erro de execução; e "OK", caso contrário.

<b>message</b> - contem a mensagem de erro, caso o status tenha sido "FAIL"; ou vazio, caso contrário.

<b>responses</b> - resultados individuais do envio de cada mensagem. É um objeto MsgResponse, formatado como JSON, o qual possui os seguintes campos:

<b>id</b> - O regId do destinatário da mensagem

<b>status</b> -indicará "OK" caso o envio tenha sido realizado com sucesso; ou "FAIL", caso contrário.

<b>message</b> - contem a mensagem de erro, caso o status tenha sido "FAIL"; ou vazio, caso contrário.


<h1>Exemplo de Utilização</h1>    

Abaixo segue um exemplo para enviar a mensagem "teste" para os ids "ddddddddddd","eeeeeeeeeeeee" e "fffffffffff", utilizando a apiKey "apitestexxxxxx".

<h3>Requisição</h3>    

A requisição é feita na URL:

http://host:port/PushService/push/push/send/apitestexxxxxx/["ddddddddddd","eeeeeeeeeeeee","fffffffffff"]/titulo/teste

Para enviar para um único id, (exemplo: ggggggggggggg) utilizar a url:

"http://host:port/PushService/push/push/send/apitestexxxxxx/ggggggggggggg/titulo/teste".

<h3>Resposta</h3>

Um exemplo de resposta para a requisição é:
 

     
     {"message":"",
      "responses":[
                    {
                        "id":"teste",
                        "message":"",
                        "status":"OK"
                    },
                    {
                        "id":"teste2",
                        "message":"HTTP Status Code: 401",
                        "status":"FAIL"
                    },
                    {
                        "id":"teste3",
                        "message":"HTTP Status Code: 401",
                        "status":"FAIL"
                    }
                ],
        "status":"OK"}
        

<h1>Instruções</h1>

<b>1.</b> No diretório "target" está localizado o fonte compilado, para ser realizado deploy (Sugiro utilizar Glassfish). Utilizar o arquivo PushService-1.0-SNAPSHOT.war para realizar o deploy.

<b>2.</b> Para compilar o código fonte ou realizar alterações, utilizar o NetBeans (Necessária a utilização do Maven para download das dependências)

<b>3.</b> No diretório "target/site/apidocs" está localizada a documentação do projeto (javadoc), acessada através do arquivo index.html.
