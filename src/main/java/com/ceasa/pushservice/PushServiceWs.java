package com.ceasa.pushservice;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.exceptions.NetworkIOException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Serviço para envio de mensagens Push para aplicações Android
 *
 * @author William
 * @version 1.0
 */
@Stateless
@Path("/push/")
public class PushServiceWs {

    /**
     * Identificador do device para Sistema Operacional Android
     */
    public static final String DEVICE_ANDROID = "android";
    /**
     * Identificador do device para Sistema Operacional iOS
     */
    public static final String DEVICE_IOS = "ios";

    private static final String APS_CERT_DEVELOPMENT = "aps_development.p12";
    private static final String APS_CERT_PRODUCTION = "aps_production.p12";
    //private static final String APS_CERT = APS_CERT_DEVELOPMENT;
    private static final boolean APS_IN_PRODUCTION = true;
    private static final String APS_PASSWORD = "prn12291";

    /**
     *
     * Envia uma mensagem a uma lista de destinatários Android via push,
     * utilizando a api GCM. para acessar o serviço via HTTP, acessar a URL com
     * o formato "http://host:port/PushService/push/push/send/apiKey/idList/msg"
     * <p/>
     * Por exemplo, para enviar a mensage "teste" para os ids
     * "ddddddddddd","eeeeeeeeeeeee" e "fffffffffff", utilizando a apiKey
     * "apitestexxxxxx", utilizar a url:
     * <p/>
     * #{@code "http://host:port/PushService/push/push/send/apitestexxxxxx/["ddddddddddd","eeeeeeeeeeeee","fffffffffff"]/teste"}
     * .<p/>
     * Para enviar para um único id, (exemplo: ggggggggggggg) utilizar a url:
     * <p/>
     * #{@code "http://host:port/PushService/push/push/send/apitestexxxxxx/ggggggggggggg/teste"}
     * .<p/>
     *
     * @param apiKey GCM apiKey, utilizada para identificar o serviço no
     * <b>GCM</b>. Para enviar mensagens para a APN (Apple), este valor é ignorado 
     * (pode ser informada uma string qualquer). Se houverem ids da GCM e tokens da APN
     * misturados no parâmetro <b>regIds</b>, este valor será considerado apenas para o
     * envio dos ids da GCM, e será ignorado pelos tokens da APN
     * 
     * @param regIds String contendo uma lista de ids (GCM) ou tokens (APN) dos destinatários
     * formatada como um array JSON. <br/>
     * Cada id/token é o identificador de registro fornecido pela GCM/APN a cada aparelho
     * ao se registrar junto à GCM/APN. <br/>
     * Cada regId identifica um aparelho e é utilizado para localizar o
     * destinatário e entregar a mensagem.<br/>
     * Por exemplo, para enviar os ids "ddddddddddd", "eeeeeeeeeeeee" e
     * "fffffffffff", deve-se enviar a string:
     * {@code '["ddddddddddd","eeeeeeeeeeeee","fffffffffff"]'}. Caso deseja-se
     * enviar a mensagem para um único id, este parâmetro pode conter apenas o
     * id desejado, sem necessidade de estar formatado como json array. Por
     * exemplo, para enviar para o id "ggggggggggggg", pode-se enviar apenas a
     * string "ggggggggggggg"
     * .<p/>
     *
     *
     * @param msg A mensagem a ser enviada.<p/>
     *
     *
     * @return um objeto {@link SenderResponse} contendo o resultado do envio da
     * mensagem. A propriedade <b>Status</b> indicará "FAIL" caso haja algum
     * erro de execução e a propriedade <b>message</b>
     * conterá a mensagem de erro; caso contrário, indicará "OK" e a propriedade
     * <b>responses</b> conterá a lista de resultados individuais do envio de
     * cada mensagem. Ver {@link MsgResponse} para entender o resultado de cada
     * envio.<p/>
     * Durante a invocação do serviço via HTTP GET, o objeto
     * {@link SenderResponse} será retornado no formato <b>json</b>.<br/>
     * Exemplo de retorno:
     * <p/>
     *
     *
     * {"message":"",<br>
     * "responses":[<br>
     * &nbsp &nbsp &nbsp {<br>
     * &nbsp &nbsp &nbsp &nbsp "id":"teste",<br>
     * &nbsp &nbsp &nbsp &nbsp "message":"",<br>
     * &nbsp &nbsp &nbsp &nbsp "status":"OK"<br>
     * &nbsp &nbsp &nbsp },<br>
     * &nbsp &nbsp &nbsp {<br>
     * &nbsp &nbsp &nbsp &nbsp "id":"teste2",<br>
     * &nbsp &nbsp &nbsp &nbsp "message":"HTTP Status Code: 401",<br>
     * &nbsp &nbsp &nbsp &nbsp "status":"FAIL"<br>
     * &nbsp &nbsp &nbsp },<br>
     * &nbsp &nbsp &nbsp {<br>
     * &nbsp &nbsp &nbsp &nbsp "id":"teste3",<br>
     * &nbsp &nbsp &nbsp &nbsp "message":"HTTP Status Code: 401",<br>
     * &nbsp &nbsp &nbsp &nbsp "status":"FAIL"<br>
     * &nbsp &nbsp &nbsp }<br>
     * &nbsp &nbsp ],<br>
     * "status":"OK"}<br>
     *
     *
     *
     */
    @GET
    @Path("/send/{apiKey}/{regIds}/{msg}/")
    @Produces("application/json")
    public SenderResponse sendMsg(@PathParam("apiKey") String apiKey,
            @PathParam("regIds") String regIds,
            @PathParam("msg") String msg) {
        System.out.println("Executando serviço de envio de mensagens.");
        SenderResponse response = new SenderResponse();

        ArrayList array;
        if (regIds.contains(",") && regIds.contains("[") && regIds.contains("]")) {
            try {
                JSONParser parser = new JSONParser();
                JSONArray jarray;
                jarray = (JSONArray) parser.parse(regIds);
                array = (ArrayList) jarray;
            } catch (ParseException ex) {
                response.setStatus("FAIL");
                response.setMessage(ex.getMessage());
                Logger.getLogger(PushServiceWs.class.getName()).log(Level.SEVERE, null, ex);
                return response;
            }
        } else {
            array = new ArrayList<>();
            array.add(regIds);
        }

        for (Object id : array) {
            String device = (((String)id).length() > 64) ? DEVICE_ANDROID : DEVICE_IOS;
            switch (device.toLowerCase()) {
                case DEVICE_ANDROID: {
                    System.out.println("Enviando msg '" + msg + "' para o id '" + id + "' utilizando a api '" + apiKey + "'");
                    Sender sender = new Sender(apiKey);
                    Calendar cal = Calendar.getInstance();
                    Message mensagem = new Message.Builder()
                            .addData("message", msg)
                            .addData("title", "Evento Report Online")
                            //.addData("msgcnt",Integer.toString(cnt))
                            .addData("notId", Integer.toString((int) cal.getTimeInMillis()))
                            .timeToLive(60)
                            .build();
                    MsgResponse resp = new MsgResponse();
                    resp.setId((String) id);
                    try {
                        Result res = sender.send(mensagem, (String) id, 10);
                        if ((res.getErrorCodeName() == null) || res.getErrorCodeName().equals("")) {
                            resp.setStatus("OK");
                            resp.setMessage("messageId=" + (res.getMessageId() != null ? res.getMessageId() : "null")
                                    + ", canonicalRegId=" + (res.getCanonicalRegistrationId() != null ? res.getCanonicalRegistrationId() : "null"));
                            response.addResponse(resp);
                        } else {
                            resp.setStatus("FAIL");
                            resp.setMessage("Erro retornado pela GCM: " + res.getErrorCodeName());
                            response.addResponse(resp);
                        }
                    } catch (IOException ex) {
                        resp.setStatus("FAIL");
                        resp.setMessage(ex.getMessage());
                        response.addResponse(resp);
                    }
                    break;
                }
                case DEVICE_IOS: {
                    System.out.println("Enviando msg '" + msg + "' para o token '" + id);
                    /*if (apnsService == null) {
                        apnsService = APNS.newService()
                                .withCert(APS_CERT, APS_PASSWORD)
                                .withSandboxDestination()
                                .build();
                    }
                    MsgResponse resp = new MsgResponse();
                    resp.setId((String) id);
                    String payload = APNS.newPayload().alertBody(msg).build();*/
                    MsgResponse resp = new MsgResponse();
                    try {
                        //ApnsNotification notification = apnsService.push((String) id, payload);
                        URL url = this.getClass().getResource("/"+ (APS_IN_PRODUCTION ? APS_CERT_PRODUCTION : APS_CERT_DEVELOPMENT));
                        String path = "";
                        if (url != null){
                            path = url.toString();
                            int pos = path.indexOf("file:/");
                            pos += "file:/".length();
                            path = path.substring(pos);
                        }
                        Push.alert(msg, path ,APS_PASSWORD , APS_IN_PRODUCTION, id);
                        resp.setStatus("OK");
                        response.addResponse(resp);
                    } catch (NetworkIOException | KeystoreException | CommunicationException ex) {
                        resp.setStatus("FAIL");
                        resp.setMessage(ex.getMessage());
                        response.addResponse(resp);
                    } 
                    break;
                }
            }
        }
        response.setStatus("OK");
        response.setMessage("");
        return response;
    }

}
